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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import org.lanternpowered.server.network.packet.Packet;

/**
 * This message is send when the player jumps with a vehicle.
 */
public class PacketPlayInPlayerVehicleJump implements Packet {

    private final boolean jump;
    private final float powerProgress;

    public PacketPlayInPlayerVehicleJump(boolean jump, float powerProgress) {
        this.powerProgress = powerProgress;
        this.jump = jump;
    }

    /**
     * Gets the progress of strength (charge) bar, scales between 0 and 1.
     * 
     * <p>This value will only return something greater then 0 if the player
     * is riding a horse and the new jump state ({@link #isJumping()} returns {@code false}.
     * Which means that the player released the jump button.</p>
     * 
     * @return the power progress
     */
    public float getPowerProgress() {
        return this.powerProgress;
    }

    /**
     * Gets the jumping state.
     * 
     * @return the jumping state
     */
    public boolean isJumping() {
        return this.jump;
    }

}
