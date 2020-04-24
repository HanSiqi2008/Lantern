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
import org.lanternpowered.server.network.vanilla.message.type.play.GenerateJigsawStructureMessage

class GenerateJigsawStructureCodec : Codec<GenerateJigsawStructureMessage> {

    override fun decode(context: CodecContext, buf: ByteBuffer): GenerateJigsawStructureMessage {
        return buf.run {
            val position = readPosition()
            val levels = readInt()
            GenerateJigsawStructureMessage(position, levels)
        }
    }
}
