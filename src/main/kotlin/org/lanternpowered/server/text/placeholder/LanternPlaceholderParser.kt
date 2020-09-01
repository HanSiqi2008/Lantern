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
package org.lanternpowered.server.text.placeholder

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.placeholder.PlaceholderContext
import org.lanternpowered.api.text.placeholder.PlaceholderParser
import org.lanternpowered.server.catalog.DefaultCatalogType

class LanternPlaceholderParser(
        key: NamespacedKey, val function: (context: PlaceholderContext) -> Text
) : DefaultCatalogType(key), PlaceholderParser {
    override fun parse(context: PlaceholderContext): Text = this.function(context)
}
