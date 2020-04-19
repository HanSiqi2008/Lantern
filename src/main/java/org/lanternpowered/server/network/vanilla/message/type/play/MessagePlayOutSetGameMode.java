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
package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.network.message.Message;

public final class MessagePlayOutSetGameMode implements Message {

    private final LanternGameMode gameMode;

    /**
     * Creates a new set game mode message.
     * 
     * @param gameMode the game mode
     */
    public MessagePlayOutSetGameMode(LanternGameMode gameMode) {
        this.gameMode = checkNotNull(gameMode, "gameMode");
    }

    /**
     * Gets the game mode.
     * 
     * @return the game mode
     */
    public LanternGameMode getGameMode() {
        return this.gameMode;
    }

}
