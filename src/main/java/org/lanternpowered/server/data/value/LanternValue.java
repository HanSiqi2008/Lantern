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
package org.lanternpowered.server.data.value;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.Value;

import java.util.Objects;

public abstract class LanternValue<E> implements Value<E> {

    protected final Key<? extends Value<E>> key;
    protected E value;

    protected LanternValue(Key<? extends Value<E>> key, E value) {
        this.value = checkNotNull(value, "value");
        this.key = checkNotNull(key, "key");
    }

    @Override
    public E get() {
        return this.value;
    }

    @Override
    public Key<? extends Value<E>> getKey() {
        return this.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final LanternValue other = (LanternValue) obj;
        return Objects.equals(this.key, other.key)
               && Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("key", this.key)
            .add("value", this.value)
            .toString();
    }

}
