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
package org.lanternpowered.server.inventory;

import org.lanternpowered.server.data.AbstractImmutableDataHolder;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.data.value.KeyRegistration;
import org.lanternpowered.server.item.LanternItemType;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class LanternItemStackSnapshot implements ItemStackSnapshot, AbstractImmutableDataHolder<ItemStackSnapshot>, AbstractPropertyHolder {

    private final Map<Key<?>, KeyRegistration> rawValueMap;
    private final ItemType itemType;
    // TODO: Hmm, inconsistency the the name with itemstack?
    private final int quantity;

    public LanternItemStackSnapshot(ItemType itemType, int quantity) {
        this(itemType, quantity, new HashMap<>());
        ((LanternItemType) itemType).registerKeysFor(this);
    }

    LanternItemStackSnapshot(ItemType itemType, int quantity, Map<Key<?>, KeyRegistration> rawValueMap) {
        this.rawValueMap = rawValueMap;
        this.itemType = itemType;
        this.quantity = quantity;
    }

    @Override
    public ItemType getType() {
        return this.itemType;
    }

    @Override
    public int getCount() {
        return this.quantity;
    }

    @Override
    public ItemStack createStack() {
        return new LanternItemStack(this.itemType, this.quantity, copyRawValueMap());
    }

    @Override
    public GameDictionary.Entry createGameDictionaryEntry() {
        return null;
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getManipulators() {
        return null;
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return null;
    }

    @Override
    public Map<Key<?>, KeyRegistration> getRawValueMap() {
        return this.rawValueMap;
    }

    @Override
    public boolean supports(Key<?> key) {
        return false;
    }

    @Override
    public ItemStackSnapshot copy() {
        return null;
    }

    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        return null;
    }

    @Override
    public <T extends ImmutableDataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        return null;
    }

    @Override
    public boolean supports(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        return false;
    }

    @Override
    public <E> Optional<ItemStackSnapshot> transform(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        return null;
    }

    @Override
    public <E> Optional<ItemStackSnapshot> with(Key<? extends BaseValue<E>> key, E value) {
        return null;
    }

    @Override
    public Optional<ItemStackSnapshot> with(BaseValue<?> value) {
        return null;
    }

    @Override
    public Optional<ItemStackSnapshot> with(ImmutableDataManipulator<?, ?> valueContainer) {
        return null;
    }

    @Override
    public Optional<ItemStackSnapshot> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
        return null;
    }

    @Override
    public Optional<ItemStackSnapshot> without(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        return null;
    }

    @Override
    public ItemStackSnapshot merge(ItemStackSnapshot that) {
        return null;
    }

    @Override
    public ItemStackSnapshot merge(ItemStackSnapshot that, MergeFunction function) {
        return null;
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getContainers() {
        return null;
    }
}
