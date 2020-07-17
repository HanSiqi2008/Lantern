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
package org.lanternpowered.server.game.registry;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.Tuple;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public abstract class DefaultCatalogRegistryModule<T extends CatalogType>
        implements AlternateCatalogRegistryModule<T>, CatalogMappingDataHolder {

    public final static String VALUE_PATTERN = "[a-z][a-z0-9-_]*";
    public final static String DEFAULT_ID_PATTERN =
            "^" + VALUE_PATTERN + ":((" + VALUE_PATTERN + ")/)*" + VALUE_PATTERN + "$";

    /**
     * A cached values collection.
     */
    @Nullable private Collection<T> values;

    /**
     * The types mapped by their catalog key.
     */
    private Map<ResourceKey, T> types = new HashMap<>();

    /**
     * Patterns to validate the catalog keys.
     */
    private final String keyPatternValue;
    private final Pattern keyPattern;

    /**
     * The catalog classes that will be mapped.
     */
    private final Class<?>[] catalogClasses;

    public DefaultCatalogRegistryModule() {
        this(new Class[0], null);
    }

    public DefaultCatalogRegistryModule(Class<?>... catalogClasses) {
        this(catalogClasses, null);
    }

    public DefaultCatalogRegistryModule(KClass<?>... catalogClasses) {
        this(catalogClasses, null);
    }

    @SuppressWarnings("RedundantCast") // Not redundant
    public DefaultCatalogRegistryModule(KClass<?>[] catalogClasses, @Nullable String pattern) {
        this((Class[]) Arrays.stream(catalogClasses).map(JvmClassMappingKt::getJavaClass).toArray(Class[]::new), pattern);
    }

    public DefaultCatalogRegistryModule(Class<?>[] catalogClasses, @Nullable String pattern) {
        this.catalogClasses = checkNotNull(catalogClasses, "catalogClasses");
        for (Class<?> catalogClass : catalogClasses) {
            checkNotNull(catalogClass, "catalogClass is null in " + getClass().getName());
        }
        this.keyPatternValue = pattern == null ? DEFAULT_ID_PATTERN : pattern;
        this.keyPattern = Pattern.compile(this.keyPatternValue);
    }

    protected boolean isContentFinalized() {
        return this.values != null;
    }

    protected void checkFinalizedContent() {
        checkState(!isContentFinalized(), "The content is already finalized.");
    }

    /**
     * Finalizes the content of the registry module, any further attempts for
     * changes will throw exceptions.
     */
    protected void finalizeContent() {
        checkFinalizedContent();
        this.types = ImmutableMap.copyOf(this.types);
        this.values = ImmutableSet.copyOf(this.types.values());
    }

    /**
     * Registers a catalog type.
     *
     * @param catalogType The catalog type to register
     */
    protected <A extends T> A register(A catalogType) {
        checkFinalizedContent();
        doRegistration(catalogType, false);
        return catalogType;
    }

    /**
     * Registers a catalog type.
     *
     * @param catalogType The catalog type to register
     * @param disallowInbuiltPluginIds Whether inbuilt plugin ids aren't allowed
     */
    @SuppressWarnings("ConstantConditions")
    protected final <A extends T> A register(A catalogType, boolean disallowInbuiltPluginIds) {
        checkFinalizedContent();
        doRegistration(catalogType, disallowInbuiltPluginIds);
        return catalogType;
    }

    /**
     * Attempts to do the registration for the given catalog type.
     *
     * @param catalogType The catalog type
     * @param disallowInbuiltPluginIds Whether inbuilt plugin ids (namespaces) aren't allowed
     */
    @SuppressWarnings("ConstantConditions")
    protected void doRegistration(T catalogType, boolean disallowInbuiltPluginIds) {
        checkFinalizedContent();
        checkState(this.values == null, "The content is finalized and doesn't allow any new entries.");
        checkNotNull(catalogType, "catalogType");
        final ResourceKey key = catalogType.getKey();
        checkArgument(this.keyPattern.matcher(key.toString()).matches(),
                "The catalog type id %s must match the regex: %s", key, this.keyPatternValue);
        checkState(!this.types.containsKey(key), "There is already a catalog type registered with the id: %s", key);
        checkInbuiltPluginIds(catalogType, disallowInbuiltPluginIds);
        this.types.put(key, catalogType);
    }

    protected void checkInbuiltPluginIds(T catalogType, boolean disallowInbuiltPluginIds) {
        final String namespace = catalogType.getKey().getNamespace();
        if (disallowInbuiltPluginIds) {
            checkArgument(!InternalPluginsInfo.IDENTIFIERS.contains(namespace),
                    "Plugin trying to register a fake %s catalog type!", namespace);
        }
    }

    @Override
    public Map<String, T> provideCatalogMap() {
        final Map<String, Tuple<T, Integer>> mappings = new HashMap<>();
        for (T type : this.types.values()) {
            final String id = type.getKey().getValue().toLowerCase(Locale.ENGLISH);
            final String pluginId = type.getKey().getNamespace();

            final int priority = InternalPluginsInfo.IDENTIFIERS.indexOf(pluginId);
            if (mappings.containsKey(id)) {
                if (priority == -1) {
                    continue;
                }
                final int lastPriority = mappings.get(id).getSecond();
                if (lastPriority != -1 && priority > lastPriority) {
                    continue;
                }
            }
            mappings.put(id, new Tuple<>(type, priority));
        }
        //noinspection ConstantConditions
        return Maps.transformEntries(mappings, (key, value) -> value.getFirst());
    }

    @Override
    public Optional<T> get(ResourceKey key) {
        checkNotNull(key, "key");
        return Optional.ofNullable(this.types.get(key));
    }

    @Override
    public Collection<T> getAll() {
        return this.values != null ? this.values : ImmutableSet.copyOf(this.types.values());
    }

    @Override
    public List<CatalogMappingData> getCatalogMappings() {
        if (this.catalogClasses.length == 0) {
            return ImmutableList.of();
        }
        final ImmutableList.Builder<CatalogMappingData> builder = ImmutableList.builder();
        final Map<String, T> mappings = provideCatalogMap();
        for (Class<?> catalogClass : this.catalogClasses) {
            builder.add(new CatalogMappingData(catalogClass, mappings));
        }
        return builder.build();
    }
}
