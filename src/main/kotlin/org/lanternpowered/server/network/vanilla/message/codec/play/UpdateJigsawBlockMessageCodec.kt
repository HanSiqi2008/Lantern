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
import org.lanternpowered.server.network.vanilla.message.type.play.UpdateJigsawBlockMessage

class UpdateJigsawBlockMessageCodec : Codec<UpdateJigsawBlockMessage> {

    override fun decode(context: CodecContext, buf: ByteBuffer): UpdateJigsawBlockMessage {
        return buf.run {
            val position = readPosition()
            val name = readString()
            val target = readString()
            val pool = readString()
            val finalState = readString()
            val jointType = readString()
            UpdateJigsawBlockMessage(position, name, target, pool, finalState, jointType)
        }
    }
}
