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
package org.lanternpowered.server.game.registry.type.bossbar;

import org.lanternpowered.server.boss.LanternBossBarColor;
import org.lanternpowered.server.game.registry.AdditionalInternalPluginCatalogRegistryModule;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;

public final class BossBarColorRegistryModule extends AdditionalInternalPluginCatalogRegistryModule<BossBarColor> {

    public BossBarColorRegistryModule() {
        super(BossBarColors.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternBossBarColor("minecraft", "pink", 0));
        register(new LanternBossBarColor("minecraft", "blue", 1));
        register(new LanternBossBarColor("minecraft", "red", 2));
        register(new LanternBossBarColor("minecraft", "green", 3));
        register(new LanternBossBarColor("minecraft", "yellow", 4));
        register(new LanternBossBarColor("minecraft", "purple", 5));
        register(new LanternBossBarColor("minecraft", "white", 6));
    }
}
