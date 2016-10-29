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
package org.lanternpowered.server.data.key;

import static org.lanternpowered.server.data.key.LanternKeyFactory.makeMutableBoundedValueKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeSetKey;
import static org.lanternpowered.server.data.key.LanternKeyFactory.makeValueKey;

import org.lanternpowered.server.data.type.LanternBedPart;
import org.lanternpowered.server.data.type.LanternDoorHalf;
import org.lanternpowered.server.inventory.InventorySnapshot;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.data.value.mutable.SetValue;
import org.spongepowered.api.data.value.mutable.Value;

public final class LanternKeys {

    public static final Key<Value<Boolean>> INVULNERABLE =
            makeValueKey(Boolean.class, DataQuery.of("Invulnerable"), "lantern:invulnerability");
    public static final Key<Value<Integer>> PORTAL_COOLDOWN_TICKS =
            makeValueKey(Integer.class, DataQuery.of("PortalCooldownTicks"), "lantern:portal_cooldown_ticks");
    public static final Key<Value<Integer>> SCORE =
            makeValueKey(Integer.class, DataQuery.of("Score"), "lantern:score");
    public static final Key<MutableBoundedValue<Double>> ABSORPTION_AMOUNT =
            makeMutableBoundedValueKey(Double.class, DataQuery.of("AbsorptionAmount"), "lantern:absorption_amount");
    public static final Key<Value<Boolean>> CAN_PICK_UP_LOOT =
            makeValueKey(Boolean.class, DataQuery.of("CanPickupLoot"), "lantern:can_pickup_loot");
    public static final Key<Value<Boolean>> IS_EFFECT =
            makeValueKey(Boolean.class, DataQuery.of("IsEffect"), "lantern:is_effect");
    public static final Key<Value<Boolean>> IS_BABY =
            makeValueKey(Boolean.class, DataQuery.of("IsBaby"), "lantern:is_baby");
    public static final Key<Value<Boolean>> ARE_HANDS_UP =
            makeValueKey(Boolean.class, DataQuery.of("AreHandsUp"), "lantern:are_hands_up");
    public static final Key<Value<Integer>> ARROWS_IN_ENTITY =
            makeValueKey(Integer.class, DataQuery.of("ArrowsInEntity"), "lantern:arrows_in_entity");
    public static final Key<Value<Boolean>> IS_CONVERTING =
            makeValueKey(Boolean.class, DataQuery.of("IsConverting"), "lantern:is_converting");
    public static final Key<SetValue<SkinPart>> DISPLAYED_SKIN_PARTS =
            makeSetKey(SkinPart.class, DataQuery.of("DisplayedSkinParts"), "lantern:displayed_skin_parts");
    public static final Key<Value<Double>> GRAVITY_FACTOR =
            makeValueKey(Double.class, DataQuery.of("GravityFactor"), "lantern:gravity_factor");
    public static final Key<Value<LanternDoorHalf>> DOOR_HALF =
            makeValueKey(LanternDoorHalf.class, DataQuery.of("DoorHalf"), "lantern:door_half");
    public static final Key<Value<Boolean>> CHECK_DECAY =
            makeValueKey(Boolean.class, DataQuery.of("CheckDecay"), "lantern:check_decay");
    public static final Key<Value<LanternBedPart>> BED_PART =
            makeValueKey(LanternBedPart.class, DataQuery.of("BedPart"), "lantern:bed_part");
    public static final Key<Value<Boolean>> ENABLED =
            makeValueKey(Boolean.class, DataQuery.of("Enabled"), "lantern:enabled");
    public static final Key<Value<InventorySnapshot>> INVENTORY =
            makeValueKey(InventorySnapshot.class, DataQuery.of("Inventory"), "lantern:inventory");

    private LanternKeys() {
    }
}
