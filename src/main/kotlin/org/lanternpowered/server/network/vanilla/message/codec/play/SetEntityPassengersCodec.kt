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
package org.lanternpowered.server.network.vanilla.message.codec.play

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.SetEntityPassengersMessage

class SetEntityPassengersCodec : Codec<SetEntityPassengersMessage> {

    override fun encode(context: CodecContext, message: SetEntityPassengersMessage): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        buf.writeVarInt(message.entityId)
        val passengersIds = message.passengersIds
        buf.writeVarInt(passengersIds.size)
        for (entityId in passengersIds) {
            buf.writeVarInt(entityId)
        }
        return buf
    }
}
