package org.lanternpowered.server.game;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.api.GameProfile;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.service.persistence.DataBuilder;
import org.spongepowered.api.service.persistence.InvalidDataException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class LanternGameProfile implements GameProfile {

    private static final DataQuery NAME = DataQuery.of("Name");
    private static final DataQuery UNIQUE_ID = DataQuery.of("UniqueId");
    private static final DataQuery PROPERTIES = DataQuery.of("Properties");
    private static final DataQuery VALUE = DataQuery.of("Value");
    private static final DataQuery SIGNATURE = DataQuery.of("Signature");

    private final List<Property> properties;
    private final UUID uniqueId;
    private final String name;

    public LanternGameProfile(UUID uniqueId, String name) {
        this(uniqueId, name, Lists.newArrayList());
    }

    public LanternGameProfile(UUID uniqueId, String name, List<Property> properties) {
        this.properties = Lists.newArrayList(checkNotNull(properties, "properties"));
        this.uniqueId = checkNotNull(uniqueId, "uniqueId");
        this.name = checkNotNullOrEmpty(name, "name");
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer container = new MemoryDataContainer()
                .set(NAME, this.name)
                .set(UNIQUE_ID, this.uniqueId.toString());
        if (!this.properties.isEmpty()) {
            List<DataContainer> list = Lists.newArrayListWithCapacity(this.properties.size());
            for (Property property : this.properties) {
                DataContainer entry = new MemoryDataContainer()
                       .set(NAME, property.name)
                       .set(VALUE, property.value);
                if (property.signature != null) {
                    entry.set(SIGNATURE, property.signature);
                }
                list.add(entry);
            }
            container.set(PROPERTIES, list);
        }
        return container;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public List<Property> getProperties() {
        return ImmutableList.copyOf(this.properties);
    }

    public static class LanternDataBuilder implements DataBuilder<GameProfile> {

        // Why use both the optional and data exception?
        @Override
        public Optional<GameProfile> build(DataView container) throws InvalidDataException {
            String uniqueId = container.getString(UNIQUE_ID).orElse(null);
            if (uniqueId == null) {
                throw new InvalidDataException("UniqueId is missing!");
            }
            UUID uniqueId0;
            try {
                uniqueId0 = UUID.fromString(uniqueId);
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException("Unknown uniqueId format!", e);
            }
            String name = container.getString(NAME).orElse(null);
            if (name == null) {
                throw new InvalidDataException("Name is missing!");
            }
            List<DataView> views = container.getViewList(PROPERTIES).orElse(null);
            List<Property> properties;
            if (views != null && !views.isEmpty()) {
                properties = Lists.newArrayListWithCapacity(views.size());
                for (DataView view : views) {
                    if (view.contains(NAME) && view.contains(VALUE)) {
                        properties.add(new Property(view.getString(NAME).get(), view.getString(VALUE).get(),
                                view.getString(SIGNATURE).orElse(null)));
                    }
                }
            } else {
                properties = Lists.newArrayList();
            }
            return Optional.<GameProfile>of(new LanternGameProfile(uniqueId0, name, properties));
        }
    }

    public static final class Property {

        private final String name;
        private final String value;

        @Nullable
        private final String signature;

        public Property(String name, String value, @Nullable String signature) {
            this.signature = signature;
            this.value = value;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        @Nullable
        public String getSignature() {
            return this.signature;
        }
    }
}
