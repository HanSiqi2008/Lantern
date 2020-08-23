/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.plugin

import com.google.inject.Guice
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.withCause
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.plugin.PluginManager
import org.lanternpowered.api.util.collections.getAndRemoveAll
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.launch.LanternClassLoader
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.event.LanternEventManager
import org.lanternpowered.server.event.lifecycle.LanternConstructPluginEvent
import org.lanternpowered.server.plugin.inject.ParentGuiceModule
import org.spongepowered.plugin.PluginCandidate
import org.spongepowered.plugin.PluginEnvironment
import org.spongepowered.plugin.PluginKeys
import org.spongepowered.plugin.PluginLanguageService
import org.spongepowered.plugin.PluginLoader
import org.spongepowered.plugin.PluginResource
import org.spongepowered.plugin.PluginResourceLocatorService
import org.spongepowered.plugin.metadata.PluginMetadata
import java.nio.file.Path
import java.util.Optional
import java.util.ServiceConfigurationError
import java.util.ServiceLoader

class LanternPluginManager(
        private val game: LanternGame,
        private val logger: Logger,
        private val eventManager: LanternEventManager,
        private val baseDirectory: Path,
        private val pluginsDirectory: Path
) : PluginManager {

    companion object {

        const val LANTERN_ID = "lantern"
        const val SPONGE_API_ID = "spongeapi"
        const val SPONGE_ID = "sponge"
        const val MINECRAFT_ID = "minecraft"
    }

    private val byId = mutableMapOf<String, PluginContainer>()
    private val byInstance = mutableMapOf<Any, PluginContainer>()

    private lateinit var pluginEnvironment: PluginEnvironment
    private lateinit var resources: List<Path>
    private lateinit var candidatesByService: Map<LoadedLanguageService, List<PluginCandidate<PluginResource>>>

    lateinit var lanternPlugin: PluginContainer
        private set

    lateinit var spongeApiPlugin: PluginContainer
        private set

    lateinit var spongePlugin: PluginContainer
        private set

    lateinit var minecraftPlugin: PluginContainer
        private set

    fun findCandidates() {
        val locatorServices = this.findLocatorServices()
        val languageServices = this.findLanguageServices()
        this.findCandidates(locatorServices, languageServices)
    }

    private fun findLocatorServices(): List<PluginResourceLocatorService<PluginResource>> {
        val services = mutableListOf<PluginResourceLocatorService<PluginResource>>()

        val it = ServiceLoader.load(PluginResourceLocatorService::class.java).iterator()
        while (it.hasNext()) {
            try {
                @Suppress("UNCHECKED_CAST")
                services += it.next() as PluginResourceLocatorService<PluginResource>
            } catch (ex: ServiceConfigurationError) {
                this.logger.error("Error encountered initializing plugin resource locator service!", ex)
                continue
            }
        }

        return services
    }

    private class LoadedLanguageService(
            val service: PluginLanguageService<PluginResource>,
            val loader: PluginLoader<PluginResource, PluginContainer>
    )

    private fun findLanguageServices(): List<LoadedLanguageService> {
        val services = mutableListOf<LoadedLanguageService>()

        val it = ServiceLoader.load(PluginLanguageService::class.java).iterator()
        while (it.hasNext()) {
            val service = try {
                @Suppress("UNCHECKED_CAST")
                it.next() as PluginLanguageService<PluginResource>
            } catch (ex: ServiceConfigurationError) {
                this.logger.error("Error encountered initializing plugin language service!", ex)
                continue
            }
            val pluginLoaderClass = try {
                Class.forName(service.pluginLoader)
            } catch (t: ClassNotFoundException) {
                this.logger.error("Failed to find plugin loader class ${service.pluginLoader} for the " +
                        "plugin language service ${service.name}.", t)
                continue
            }
            val pluginLoader = try {
                @Suppress("UNCHECKED_CAST")
                pluginLoaderClass.getConstructor().newInstance() as PluginLoader<PluginResource, PluginContainer>
            } catch (t: Throwable) {
                this.logger.error("Failed to instantiate plugin loader ${service.pluginLoader} for the " +
                        "plugin language service ${service.name}.", t)
                continue
            }
            services += LoadedLanguageService(service, pluginLoader)
        }

        return services
    }

    private fun findCandidates(
            locatorServices: List<PluginResourceLocatorService<PluginResource>>,
            languageServices: List<LoadedLanguageService>
    ) {
        this.pluginEnvironment = PluginEnvironment(LogManager.getLogger("plugin-manager"))
        this.pluginEnvironment.blackboard.getOrCreate(PluginKeys.BASE_DIRECTORY) { this.baseDirectory }
        this.pluginEnvironment.blackboard.getOrCreate(PluginKeys.PLUGIN_DIRECTORIES) { listOf(this.pluginsDirectory) }

        val resourcesByLocator = mutableMapOf<PluginResourceLocatorService<PluginResource>, List<PluginResource>>()
        for (service in locatorServices) {
            try {
                resourcesByLocator[service] = service.locatePluginResources(this.pluginEnvironment)
            } catch (t: Throwable) {
                this.logger.error("The plugin resource locator ${service.name} failed to locate resources.", t)
                continue
            }
        }

        val candidatesByService = mutableMapOf<LoadedLanguageService, MutableList<PluginCandidate<PluginResource>>>()
        for (service in languageServices) {
            for (resource in resourcesByLocator.values.flatten()) {
                val candidates = try {
                    service.service.createPluginCandidates(this.pluginEnvironment, resource)
                } catch (t: Throwable) {
                    this.logger.error("The plugin language service ${service.service.name} failed to create plugin candidates.", t)
                    continue
                }
                if (candidates.isEmpty())
                    continue
                candidatesByService.computeIfAbsent(service) { ArrayList() }.addAll(candidates)
            }
        }

        fun extractInternalCandidate(id: String): PluginContainer {
            var firstCandidate: PluginCandidate<*>? = null
            for ((_, candidates) in candidatesByService) {
                val candidate = candidates.getAndRemoveAll { candidate -> candidate.metadata.id == id }
                if (firstCandidate == null)
                    firstCandidate = candidate.firstOrNull()
            }

            val metadata = firstCandidate?.metadata ?: PluginMetadata.builder().setId(id).build()
            val file = firstCandidate?.resource?.path ?: this.baseDirectory.resolve("magic/$id.jar") // Use something as path.

            val pluginContainer = InternalPluginContainer(file, metadata, this.logger, this.game)
            this.byId[id] = pluginContainer
            return pluginContainer
        }

        // These are internal plugins and shouldn't be constructed through the service

        this.lanternPlugin = extractInternalCandidate(LANTERN_ID)
        this.spongeApiPlugin = extractInternalCandidate(SPONGE_API_ID)
        this.spongePlugin = extractInternalCandidate(SPONGE_ID)
        this.minecraftPlugin = extractInternalCandidate(MINECRAFT_ID)

        this.resources = resources.toImmutableList()
        this.candidatesByService = candidatesByService
                .mapValues { (_, value) -> value.toImmutableList() }
                .toImmutableMap()
    }

    fun instantiate() {
        val classLoader = LanternClassLoader.get()

        val injector = Guice.createInjector(ParentGuiceModule(this.game))
        this.pluginEnvironment.blackboard.getOrCreate(LanternPluginLoader.PARENT_INJECTOR) { injector }

        // Load all the plugin jars and directories
        // TODO: Only add jars/directories that aren't on the classpath
        for (path in resources)
            classLoader.addBaseURL(path.toUri().toURL())

        // Load other plugin instances
        val causeStack = CauseStack.current()

        for ((service, candidates) in this.candidatesByService) {
            for (candidate in candidates) {
                val pluginContainer = try {
                    service.loader.createPluginContainer(candidate, this.pluginEnvironment).orNull() ?: continue
                } catch (t: Throwable) {
                    this.logger.error("Failed to create the plugin plugin of $candidate", t)
                    continue
                }

                // Already store it so it can be looked up through injections.
                this.byId[candidate.metadata.id] = pluginContainer

                // Already put the plugin container in the cause stack so
                // it's available during injections, etc.
                causeStack.withCause(pluginContainer) {
                    // Instantiate the plugin instance.
                    try {
                        service.loader.loadPlugin(this.pluginEnvironment, pluginContainer, classLoader)
                        this.byInstance[pluginContainer.instance] = pluginContainer
                    } catch (t: Throwable) {
                        this.logger.error("Failed to instantiate the plugin instance of $candidate", t)
                        // Remove the failed plugin
                        this.byId -= candidate.metadata.id
                    }
                }
            }
        }
    }

    /**
     * Registers all the listeners of the plugins and
     * calls their construction events.
     */
    fun construct() {
        // Register the listeners of all the plugins first, so they can
        // already receive events posted by other plugins.
        for (plugin in this.byInstance.values)
            this.eventManager.registerListeners(plugin, plugin.instance)

        for (plugin in this.byInstance.values)
            this.eventManager.postFor(LanternConstructPluginEvent(this.game, plugin), plugin)
    }

    override fun isLoaded(id: String): Boolean =
            this.byId.containsKey(id)

    override fun getPlugin(id: String): Optional<PluginContainer> =
            this.byId[id].asOptional()

    override fun getPlugins(): Collection<PluginContainer> =
            this.byId.values.toImmutableList()

    override fun fromInstance(instance: Any): Optional<PluginContainer> =
            if (instance is PluginContainer) instance.asOptional() else this.byInstance[instance].asOptional()
}
