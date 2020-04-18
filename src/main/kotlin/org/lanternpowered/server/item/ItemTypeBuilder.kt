/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.item

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.data.Key
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.text.translation.Translation
import org.lanternpowered.server.behavior.Behavior
import org.lanternpowered.server.behavior.pipeline.MutableBehaviorPipeline
import org.lanternpowered.server.data.LocalKeyRegistry

/**
 * Constructs a new [ItemType].
 */
fun itemTypeOf(key: CatalogKey, fn: ItemTypeBuilder.() -> Unit): ItemType {
    return LanternItemTypeBuilder().apply(fn).build(key)
}

@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@DslMarker
annotation class ItemTypeBuilderDsl

@ItemTypeBuilderDsl
interface ItemTypeBuilder {

    fun name(name: String)
    fun name(name: Translation)
    fun name(fn: @ItemTypeBuilderDsl ItemStack.() -> Translation)

    /**
     * Sets the maximum stack quantity.
     */
    fun maxStackQuantity(quantity: Int)

    /**
     * Applies properties to the [ItemType].
     */
    fun keys(fn: @ItemTypeBuilderDsl LocalKeyRegistry<ItemType>.() -> Unit)

    /**
     * Applies behaviors to the [ItemType].
     */
    fun behaviors(fn: @ItemTypeBuilderDsl MutableBehaviorPipeline<Behavior>.() -> Unit)

    /**
     * Applies a function that can be used to register
     * [Key]s on [ItemStack]s of the built item type.
     */
    fun stackKeys(fn: @ItemTypeBuilderDsl LocalKeyRegistry<ItemStack>.() -> Unit)
}
