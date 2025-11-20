package net.wolf_l1grotale.industriallogiccraft.block.base;

/**
 * Класс конфигурации для упрощенного создания блоков
 */
public class BlockConfiguration {
    private final boolean hasRotation;
    private final boolean allowVerticalRotation;
    private final boolean hasLitState;
    private final boolean hasPoweredState;
    private final boolean hasGui;
    private final boolean hasItemDrops;
    private final boolean shouldKeepCustomName;
    private final boolean hasServerTicker;
    private final boolean hasClientTicker;

    private BlockConfiguration(Builder builder) {
        this.hasRotation = builder.hasRotation;
        this.allowVerticalRotation = builder.allowVerticalRotation;
        this.hasLitState = builder.hasLitState;
        this.hasPoweredState = builder.hasPoweredState;
        this.hasGui = builder.hasGui;
        this.hasItemDrops = builder.hasItemDrops;
        this.shouldKeepCustomName = builder.shouldKeepCustomName;
        this.hasServerTicker = builder.hasServerTicker;
        this.hasClientTicker = builder.hasClientTicker;
    }

    // Геттеры
    public boolean hasRotation() {
        return hasRotation;
    }

    public boolean allowVerticalRotation() {
        return allowVerticalRotation;
    }

    public boolean hasLitState() {
        return hasLitState;
    }

    public boolean hasPoweredState() {
        return hasPoweredState;
    }

    public boolean hasGui() {
        return hasGui;
    }

    public boolean hasItemDrops() {
        return hasItemDrops;
    }

    public boolean shouldKeepCustomName() {
        return shouldKeepCustomName;
    }

    public boolean hasServerTicker() {
        return hasServerTicker;
    }

    public boolean hasClientTicker() {
        return hasClientTicker;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean hasRotation = false;
        private boolean allowVerticalRotation = false;
        private boolean hasLitState = false;
        private boolean hasPoweredState = false;
        private boolean hasGui = false;
        private boolean hasItemDrops = false;
        private boolean shouldKeepCustomName = false;
        private boolean hasServerTicker = false;
        private boolean hasClientTicker = false;

        public Builder withRotation() {
            this.hasRotation = true;
            return this;
        }

        public Builder withVerticalRotation() {
            this.hasRotation = true;
            this.allowVerticalRotation = true;
            return this;
        }

        public Builder withLitState() {
            this.hasLitState = true;
            return this;
        }

        public Builder withPoweredState() {
            this.hasPoweredState = true;
            return this;
        }

        public Builder withGui() {
            this.hasGui = true;
            return this;
        }

        public Builder withItemDrops() {
            this.hasItemDrops = true;
            return this;
        }

        public Builder withCustomName() {
            this.shouldKeepCustomName = true;
            return this;
        }

        public Builder withServerTicker() {
            this.hasServerTicker = true;
            return this;
        }

        public Builder withClientTicker() {
            this.hasClientTicker = true;
            return this;
        }

        public BlockConfiguration build() {
            return new BlockConfiguration(this);
        }
    }
}