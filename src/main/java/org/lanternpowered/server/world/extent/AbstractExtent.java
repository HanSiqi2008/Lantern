/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.world.extent;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.lanternpowered.server.util.gen.ShortArrayImmutableBiomeBuffer;
import org.lanternpowered.server.util.gen.ShortArrayImmutableBlockBuffer;
import org.lanternpowered.server.util.gen.ShortArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.ShortArrayMutableBlockBuffer;
import org.lanternpowered.server.util.gen.concurrent.AtomicShortArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.concurrent.AtomicShortArrayMutableBlockBuffer;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.ScheduledBlockUpdate;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.service.persistence.InvalidDataException;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public abstract class AbstractExtent implements Extent {

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Vector3i position, Direction direction, Class<T> propertyClass) {
        return this.getProperty(position.getX(), position.getY(), position.getZ(), direction, propertyClass);
    }

    @Override
    public void setBlock(Vector3i position, BlockState block, boolean notifyNeighbors) {
        this.setBlock(position.getX(), position.getY(), position.getZ(), block, notifyNeighbors);
    }

    @Override
    public void setBlockType(Vector3i position, BlockType type, boolean notifyNeighbors) {
        this.setBlockType(position.getX(), position.getY(), position.getZ(), type, notifyNeighbors);
    }

    @Override
    public void setBlockType(int x, int y, int z, BlockType type, boolean notifyNeighbors) {
        this.setBlock(x, y, z, type.getDefaultState(), notifyNeighbors);
    }

    @Override
    public Location<? extends Extent> getLocation(Vector3i position) {
        return this.getLocation(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public Location<? extends Extent> getLocation(Vector3d position) {
        return this.getLocation(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public BiomeType getBiome(Vector2i position) {
        return this.getBiome(position.getX(), position.getY());
    }

    @Override
    public void setBiome(Vector2i position, BiomeType biome) {
        this.setBiome(position.getX(), position.getY(), biome);
    }

    @Override
    public BlockState getBlock(Vector3i position) {
        return this.getBlock(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public void setBlock(Vector3i position, BlockState block) {
        this.setBlock(position.getX(), position.getY(), position.getZ(), block);
    }

    @Override
    public BlockType getBlockType(Vector3i position) {
        return this.getBlockType(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public void setBlockType(Vector3i position, BlockType type) {
        this.setBlockType(position.getX(), position.getY(), position.getZ(), type);
    }

    @Override
    public void setBlockType(int x, int y, int z, BlockType type) {
        this.setBlock(x, y, z, type.getDefaultState());
    }

    @Override
    public BlockSnapshot createSnapshot(Vector3i position) {
        return this.createSnapshot(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public boolean restoreSnapshot(BlockSnapshot snapshot, boolean force, boolean notifyNeighbors) {
        Location<World> location = checkNotNull(snapshot, "snapshot").getLocation().orElse(null);
        checkArgument(location != null, "location is not present in snapshot");
        return this.restoreSnapshot(location.getBlockPosition(), snapshot, force, notifyNeighbors);
    }

    @Override
    public boolean restoreSnapshot(Vector3i position, BlockSnapshot snapshot, boolean force, boolean notifyNeighbors) {
        return this.restoreSnapshot(position.getX(), position.getY(), position.getZ(), snapshot, force, notifyNeighbors);
    }

    @Override
    public Optional<TileEntity> getTileEntity(Vector3i position) {
        return this.getTileEntity(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public boolean containsBiome(Vector2i position) {
        return this.containsBiome(position.getX(), position.getY());
    }

    @Override
    public boolean containsBlock(Vector3i position) {
        return this.containsBlock(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public Collection<ScheduledBlockUpdate> getScheduledUpdates(Vector3i position) {
        return this.getScheduledUpdates(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public ScheduledBlockUpdate addScheduledUpdate(Vector3i position, int priority, int ticks) {
        return this.addScheduledUpdate(position.getX(), position.getY(), position.getZ(), priority, ticks);
    }

    @Override
    public void removeScheduledUpdate(Vector3i position, ScheduledBlockUpdate update) {
        this.removeScheduledUpdate(position.getX(), position.getY(), position.getZ(), update);
    }

    @Override
    public MutableBiomeArea getBiomeView(Vector2i newMin, Vector2i newMax) {
        if (!this.containsBiome(newMin.getX(), newMin.getY())) {
            throw new PositionOutOfBoundsException(newMin, this.getBiomeMin(), this.getBiomeMax());
        }
        if (!this.containsBiome(newMax.getX(), newMax.getY())) {
            throw new PositionOutOfBoundsException(newMax, this.getBiomeMin(), this.getBiomeMax());
        }
        return new MutableBiomeViewDownsize(this, newMin, newMax);
    }

    @Override
    public MutableBiomeArea getBiomeView(DiscreteTransform2 transform) {
        return new MutableBiomeViewTransform(this, transform);
    }

    @Override
    public MutableBiomeArea getRelativeBiomeView() {
        return this.getBiomeView(DiscreteTransform2.fromTranslation(this.getBiomeMin().negate()));
    }

    @Override
    public UnmodifiableBiomeArea getUnmodifiableBiomeView() {
        return new UnmodifiableBiomeAreaWrapper(this);
    }

    @Override
    public MutableBiomeArea getBiomeCopy() {
        return this.getBiomeCopy(StorageType.STANDARD);
    }

    @Override
    public MutableBiomeArea getBiomeCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ShortArrayMutableBiomeBuffer(ExtentBufferUtil.copyToArray(this, this.getBiomeMin(),
                        this.getBiomeMax(), this.getBiomeSize()), this.getBiomeMin(), this.getBiomeSize());
            case THREAD_SAFE:
                return new AtomicShortArrayMutableBiomeBuffer(ExtentBufferUtil.copyToArray(this, this.getBiomeMin(),
                        this.getBiomeMax(), this.getBiomeSize()), this.getBiomeMin(), this.getBiomeSize());
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBiomeArea getImmutableBiomeCopy() {
        return ShortArrayImmutableBiomeBuffer.newWithoutArrayClone(ExtentBufferUtil.copyToArray(this, this.getBiomeMin(),
                this.getBiomeMax(), this.getBiomeSize()), this.getBiomeMin(), this.getBiomeSize());
    }

    @Override
    public MutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        if (!this.containsBlock(newMin.getX(), newMin.getY(), newMin.getZ())) {
            throw new PositionOutOfBoundsException(newMin, this.getBlockMin(), this.getBlockMax());
        }
        if (!this.containsBlock(newMax.getX(), newMax.getY(), newMax.getZ())) {
            throw new PositionOutOfBoundsException(newMax, this.getBlockMin(), this.getBlockMax());
        }
        return new MutableBlockViewDownsize(this, newMin, newMax);
    }

    @Override
    public MutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new MutableBlockViewTransform(this, transform);
    }

    @Override
    public MutableBlockVolume getRelativeBlockView() {
        return this.getBlockView(DiscreteTransform3.fromTranslation(this.getBlockMin().negate()));
    }

    @Override
    public UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return new UnmodifiableBlockVolumeWrapper(this);
    }

    @Override
    public MutableBlockVolume getBlockCopy() {
        return this.getBlockCopy(StorageType.STANDARD);
    }

    @Override
    public MutableBlockVolume getBlockCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ShortArrayMutableBlockBuffer(ExtentBufferUtil.copyToArray(this, this.getBlockMin(),
                        this.getBlockMax(), this.getBlockSize()), this.getBlockMin(), this.getBlockSize());
            case THREAD_SAFE:
                return new AtomicShortArrayMutableBlockBuffer(ExtentBufferUtil.copyToArray(this, this.getBlockMin(),
                        this.getBlockMax(), this.getBlockSize()), this.getBlockMin(), this.getBlockSize());
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBlockVolume getImmutableBlockCopy() {
        return ShortArrayImmutableBlockBuffer.newWithoutArrayClone(ExtentBufferUtil.copyToArray(this, this.getBlockMin(),
                this.getBlockMax(), this.getBlockSize()), this.getBlockMin(), this.getBlockSize());
    }

    @Override
    public void setRawData(Vector3i position, DataView container) throws InvalidDataException {
        this.setRawData(position.getX(), position.getY(), position.getZ(), container);
    }

    @Override
    public boolean validateRawData(Vector3i position, DataView container) {
        return this.validateRawData(position.getX(), position.getY(), position.getZ(), container);
    }

    @Override
    public Collection<DataManipulator<?, ?>> getManipulators(Vector3i coordinates) {
        return this.getManipulators(coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }

    @Override
    public <E> Optional<E> get(Vector3i coordinates, Key<? extends BaseValue<E>> key) {
        return this.get(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(Vector3i coordinates, Class<T> manipulatorClass) {
        return this.get(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulatorClass);
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(Vector3i coordinates, Class<T> manipulatorClass) {
        return this.getOrCreate(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulatorClass);
    }

    @Override
    public <E> E getOrNull(Vector3i coordinates, Key<? extends BaseValue<E>> key) {
        return this.getOrNull(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key);
    }

    @Override
    public <E> E getOrElse(Vector3i coordinates, Key<? extends BaseValue<E>> key, E defaultValue) {
        return this.getOrElse(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key, defaultValue);
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Vector3i coordinates, Key<V> key) {
        return this.getValue(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key);
    }

    @Override
    public boolean supports(Vector3i coordinates, Key<?> key) {
        return this.supports(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key);
    }

    @Override
    public boolean supports(Vector3i coordinates, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        return this.supports(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulatorClass);
    }

    @Override
    public boolean supports(Vector3i coordinates, DataManipulator<?, ?> manipulator) {
        return this.supports(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulator);
    }

    @Override
    public Set<Key<?>> getKeys(Vector3i coordinates) {
        return this.getKeys(coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }

    @Override
    public Set<ImmutableValue<?>> getValues(Vector3i coordinates) {
        return this.getValues(coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }

    @Override
    public <E> DataTransactionResult offer(Vector3i coordinates, BaseValue<E> value) {
        return this.offer(coordinates.getX(), coordinates.getY(), coordinates.getZ(), value);
    }

    @Override
    public DataTransactionResult offer(Vector3i coordinates, DataManipulator<?, ?> manipulator) {
        return this.offer(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulator);
    }

    @Override
    public DataTransactionResult offer(Vector3i coordinates, Iterable<DataManipulator<?, ?>> manipulators) {
        return this.offer(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulators);
    }

    @Override
    public DataTransactionResult remove(Vector3i coordinates, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        return this.remove(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulatorClass);
    }

    @Override
    public DataTransactionResult remove(Vector3i coordinates, Key<?> key) {
        return this.remove(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key);
    }

    @Override
    public DataTransactionResult undo(Vector3i coordinates, DataTransactionResult result) {
        return this.undo(coordinates.getX(), coordinates.getY(), coordinates.getZ(), result);
    }

    @Override
    public DataTransactionResult copyFrom(Vector3i coordinatesTo, Vector3i coordinatesFrom) {
        return this.copyFrom(coordinatesTo.getX(), coordinatesTo.getY(), coordinatesTo.getZ(), coordinatesFrom.getX(),
            coordinatesFrom.getY(), coordinatesFrom.getZ());
    }

    @Override
    public DataTransactionResult copyFrom(Vector3i coordinatesTo, Vector3i coordinatesFrom, MergeFunction function) {
        return this.copyFrom(coordinatesTo.getX(), coordinatesTo.getY(), coordinatesTo.getZ(), coordinatesFrom.getX(),
            coordinatesFrom.getY(), coordinatesFrom.getZ(), function);
    }

    @Override
    public DataTransactionResult copyFrom(Vector3i to, DataHolder from, MergeFunction function) {
        return this.copyFrom(to.getX(), to.getY(), to.getZ(), from, function);
    }

    @Override
    public DataTransactionResult copyFrom(Vector3i to, DataHolder from) {
        return this.copyFrom(to.getX(), to.getY(), to.getZ(), from);
    }

    @Override
    public DataTransactionResult offer(Vector3i coordinates, DataManipulator<?, ?> manipulator, MergeFunction function) {
        return this.offer(coordinates.getX(), coordinates.getY(), coordinates.getZ(), manipulator, function);
    }

    @Override
    public <E> DataTransactionResult transform(Vector3i coordinates, Key<? extends BaseValue<E>> key, Function<E, E> function) {
        return this.transform(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key, function);
    }

    @Override
    public <E> DataTransactionResult offer(Vector3i coordinates, Key<? extends BaseValue<E>> key, E value) {
        return this.offer(coordinates.getX(), coordinates.getY(), coordinates.getZ(), key, value);
    }

    @Override
    public boolean supports(Vector3i coordinates, BaseValue<?> value) {
        return this.supports(coordinates.getX(), coordinates.getY(), coordinates.getZ(), value);
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Vector3i coordinates, Class<T> propertyClass) {
        return this.getProperty(coordinates.getX(), coordinates.getY(), coordinates.getZ(), propertyClass);
    }

    @Override
    public Collection<Property<?, ?>> getProperties(Vector3i coordinates) {
        return this.getProperties(coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }
}
