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
@file:JvmName("AdvancementTriggerRegistry")
package org.lanternpowered.server.registry.type.advancement

import org.lanternpowered.api.registry.mutableCatalogTypeRegistry
import org.spongepowered.api.advancement.criteria.trigger.Trigger

@get:JvmName("get")
val AdvancementTriggerRegistry = mutableCatalogTypeRegistry<Trigger<*>>()
