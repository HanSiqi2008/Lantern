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
package org.lanternpowered.server.effect.particle;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.effect.particle.ParticleOption;

import java.util.function.Function;

import javax.annotation.Nullable;

public class LanternParticleOption<V> extends PluginCatalogType.Base implements ParticleOption<V> {

    private final String name;
    private final Class<V> valueType;
    @Nullable private final Function<V, IllegalArgumentException> valueValidator;

    public LanternParticleOption(String pluginId, String id, String name, Class<V> valueType,
            @Nullable Function<V, IllegalArgumentException> valueValidator) {
        super(pluginId, id, name);
        this.valueValidator = valueValidator;
        this.valueType = valueType;
        this.name = name;
    }

    public LanternParticleOption(String pluginId, String id, String name, Class<V> valueType) {
        this(pluginId, id, name, valueType, null);
    }

    @Nullable
    public IllegalArgumentException validateValue(V value) {
        if (this.valueValidator != null) {
            return this.valueValidator.apply(value);
        }
        return null;
    }

    @Override
    public Class<V> getValueType() {
        return this.valueType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("valueType", this.valueType);
    }
}
