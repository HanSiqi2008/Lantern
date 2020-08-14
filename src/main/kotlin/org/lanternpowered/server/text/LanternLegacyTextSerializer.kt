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
package org.lanternpowered.server.text

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.lanternpowered.api.text.serializer.LegacyTextSerializer

object LanternLegacyTextSerializer : LegacyTextSerializer,
        LanternFormattingCodeTextSerializer(LegacyComponentSerializer.SECTION_CHAR)
