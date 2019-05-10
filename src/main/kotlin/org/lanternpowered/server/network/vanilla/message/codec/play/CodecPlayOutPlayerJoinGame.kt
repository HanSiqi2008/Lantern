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
package org.lanternpowered.server.network.vanilla.message.codec.play

import io.netty.util.AttributeKey
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerJoinGame
import org.lanternpowered.server.world.dimension.LanternDimensionType
import kotlin.experimental.or

class CodecPlayOutPlayerJoinGame : Codec<MessagePlayOutPlayerJoinGame> {

    override fun encode(context: CodecContext, message: MessagePlayOutPlayerJoinGame): ByteBuffer {
        context.channel.attr(PLAYER_ENTITY_ID).set(message.entityId)
        return context.byteBufAlloc().buffer().apply {
            writeInteger(message.entityId)
            var gameMode = (message.gameMode as LanternGameMode).internalId.toByte()
            if (message.isHardcore) {
                gameMode = gameMode or 0x8
            }
            writeByte(gameMode)
            writeInteger((message.dimensionType as LanternDimensionType<*>).internalId)
            writeByte(Math.min(message.playerListSize, 255).toByte())
            writeString(if (message.lowHorizon) "flat" else "default")
            writeVarInt(message.viewDistance)
            writeBoolean(message.reducedDebug)
        }
    }

    companion object {

        @JvmField val PLAYER_ENTITY_ID = AttributeKey.valueOf<Int>("player-entity-id")
    }
}
