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
package org.lanternpowered.server.permission

import org.lanternpowered.api.service.serviceOf
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.server.service.permission.LanternPermissionService
import org.spongepowered.api.service.context.Context
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.Subject
import org.spongepowered.api.service.permission.SubjectCollection
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.permission.SubjectReference
import org.spongepowered.api.util.Tristate
import java.util.Optional

interface ProxySubject : Subject {

    /**
     * The internal [SubjectReference].
     *
     * @return The internal subject reference
     */
    var internalSubject: SubjectReference?

    /**
     * Gets the identifier of the subject collection.
     *
     * @return The subject collection identifier
     */
    val subjectCollectionIdentifier: String

    /**
     * Gets the default [Tristate] result
     * for the given permission.
     *
     * @param permission The permission
     * @return The result
     */
    fun getPermissionDefault(permission: String): Tristate

    @JvmDefault
    fun resolveNullableSubject(): Subject? {
        var reference = this.internalSubject
        if (reference == null) {
            val service = serviceOf<PermissionService>()
            if (service != null) {
                // Try to update the internal subject
                // check if we're using the native Lantern impl
                // we can skip some unnecessary instance creation this way.
                reference = if (service is LanternPermissionService) {
                    service.get(this.subjectCollectionIdentifier).get(this.identifier).asSubjectReference()
                } else {
                    // build a new subject reference using the permission service
                    // this doesn't actually load the subject, so it will be lazily init'd when needed.
                    service.newSubjectReference(this.subjectCollectionIdentifier, this.identifier)
                }
                this.internalSubject = reference
            }
        }
        return reference?.resolve()?.join()
    }

    @JvmDefault
    fun resolveSubject(): Subject = resolveNullableSubject() ?: throw IllegalStateException("No subject present for $identifier")

    // Delegated methods

    @JvmDefault override fun asSubjectReference() = checkNotNull(this.internalSubject) { "No internal subject reference is set" }
    @JvmDefault override fun getContainingCollection(): SubjectCollection = resolveSubject().containingCollection
    @JvmDefault override fun getSubjectData(): SubjectData = resolveSubject().subjectData
    @JvmDefault override fun getTransientSubjectData(): SubjectData = resolveSubject().transientSubjectData
    @JvmDefault override fun isSubjectDataPersisted(): Boolean = resolveNullableSubject()?.isSubjectDataPersisted ?: false
    @JvmDefault override fun getFriendlyIdentifier(): Optional<String> = resolveNullableSubject()?.friendlyIdentifier ?: emptyOptional()

    @JvmDefault
    override fun hasPermission(contexts: Set<Context>, permission: String): Boolean {
        val subject = resolveNullableSubject()
        return if (subject == null) {
            getPermissionDefault(permission).asBoolean()
        } else {
            when (val ret = getPermissionValue(contexts, permission)) {
                Tristate.UNDEFINED -> getPermissionDefault(permission).asBoolean()
                else -> ret.asBoolean()
            }
        }
    }

    @JvmDefault
    override fun getPermissionValue(contexts: Set<Context>, permission: String): Tristate =
            resolveNullableSubject()?.getPermissionValue(contexts, permission) ?: getPermissionDefault(permission)

    @JvmDefault
    override fun isChildOf(parent: SubjectReference) = resolveNullableSubject()?.isChildOf(parent) ?: false

    @JvmDefault
    override fun isChildOf(contexts: Set<Context>, parent: SubjectReference) = resolveNullableSubject()?.isChildOf(contexts, parent) ?: false

    @JvmDefault
    override fun getParents(): List<SubjectReference> = resolveNullableSubject()?.parents ?: emptyList()

    @JvmDefault
    override fun getParents(contexts: Set<Context>): List<SubjectReference> = resolveNullableSubject()?.getParents(contexts) ?: emptyList()

    @JvmDefault
    override fun getActiveContexts(): Set<Context> = resolveNullableSubject()?.activeContexts ?: emptySet()

    @JvmDefault
    override fun getOption(key: String): Optional<String> = resolveNullableSubject()?.getOption(key) ?: emptyOptional()

    @JvmDefault
    override fun getOption(contexts: Set<Context>, key: String): Optional<String> =
            resolveNullableSubject()?.getOption(contexts, key) ?: emptyOptional()
}
