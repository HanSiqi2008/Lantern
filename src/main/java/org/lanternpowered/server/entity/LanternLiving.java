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
package org.lanternpowered.server.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.UUID;

public class LanternLiving extends LanternEntity implements Living {

    private Vector3d headRotation = Vector3d.ZERO;

    public LanternLiving(UUID uniqueId) {
        super(uniqueId);
    }

    @Override
    public void registerKeys() {
        super.registerKeys();
        registerKey(Keys.MAX_AIR, 300, 0, Integer.MAX_VALUE);
        registerKey(Keys.REMAINING_AIR, 300, 0, Keys.MAX_AIR);
        registerKey(Keys.MAX_HEALTH, 20.0, 0.0, 1024.0);
        registerKey(Keys.HEALTH, 20.0, 0.0, Keys.MAX_HEALTH);
        registerKey(Keys.POTION_EFFECTS, new ArrayList<>());
    }

    protected void setRawHeadRotation(Vector3d rotation) {
        this.headRotation = checkNotNull(rotation, "rotation");
    }

    @Override
    public Vector3d getHeadRotation() {
        return this.headRotation;
    }

    @Override
    public void setHeadRotation(Vector3d rotation) {
        setRawHeadRotation(rotation);
    }

    @Override
    public Text getTeamRepresentation() {
        return Text.of(getUniqueId().toString());
    }
}
