package net.wolf_l1grotale.industriallogiccraft.block.entity.base;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.wolf_l1grotale.industriallogiccraft.block.electric.wire.CustomWireBlock;
import net.wolf_l1grotale.industriallogiccraft.block.electric.wire.EnergyStorage;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ImplementedInventory;
import net.wolf_l1grotale.industriallogiccraft.block.entity.wire.CustomWireBlockEntity;
import net.wolf_l1grotale.industriallogiccraft.item.battery.BatteryItem;
import org.jetbrains.annotations.Nullable;

/**
 * Базовый класс для всех BlockEntity с энергией и инвентарём
 * Предоставляет модульную систему для создания машин
 */
public abstract class BaseEnergyBlockEntity extends BlockEntity
        implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory, EnergyStorage {

    // ===== КОНФИГУРАЦИЯ =====
    protected final BlockEntityConfiguration config;

    // ===== ИНВЕНТАРЬ =====
    protected final DefaultedList<ItemStack> inventory;

    // ===== ЭНЕРГИЯ =====
    protected int energy = 0;
    protected final int maxEnergy;

    // ===== ТОПЛИВО (опционально) =====
    protected int burnTime = 0;
    protected int fuelTime = 0;

    // ===== ПРОГРЕСС (опционально) =====
    protected int progress = 0;
    protected int maxProgress = 72;

    // ===== PROPERTY DELEGATE =====
    protected final PropertyDelegate propertyDelegate;

    public BaseEnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,
                                 BlockEntityConfiguration config) {
        super(type, pos, state);
        this.config = config;
        this.maxEnergy = config.maxEnergy;
        this.inventory = DefaultedList.ofSize(config.inventorySize, ItemStack.EMPTY);
        this.propertyDelegate = createPropertyDelegate();
    }

    /**
     * Создаёт PropertyDelegate для синхронизации данных с GUI
     * Переопределите для добавления кастомных свойств
     */
    protected PropertyDelegate createPropertyDelegate() {
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> energy;
                    case 3 -> maxEnergy;
                    case 4 -> burnTime;
                    case 5 -> fuelTime;
                    default -> getCustomProperty(index);
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                    case 2 -> energy = value;
                    case 4 -> burnTime = value;
                    case 5 -> fuelTime = value;
                    default -> setCustomProperty(index, value);
                }
            }

            @Override
            public int size() {
                return 6 + getCustomPropertyCount();
            }
        };
    }

    /**
     * Переопределите для добавления кастомных свойств
     */
    protected int getCustomProperty(int index) {
        return 0;
    }

    /**
     * Переопределите для добавления кастомных свойств
     */
    protected void setCustomProperty(int index, int value) {
    }

    /**
     * Переопределите для указания количества кастомных свойств
     */
    protected int getCustomPropertyCount() {
        return 0;
    }

    // ===== SCREEN HANDLER =====
    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    public abstract Text getDisplayName();

    @Override
    public abstract @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player);

    // ===== INVENTORY =====
    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    // ===== TICK LOGIC =====
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        boolean wasActive = isActive();

        // 1. Обработка топлива (если включено)
        if (config.hasFuelBurning) {
            processFuel(world, pos, state);
        }

        // 2. Основная логика машины
        processMainLogic(world, pos, state);

        // 3. Распределение энергии (если включено)
        if (config.canExtractEnergy) {
            distributeEnergy(world, pos);
        }

        // 4. Зарядка батарей (если включено)
        if (config.canChargeBatteries && config.batterySlot >= 0) {
            chargeBattery();
        }

        // 5. Обновление состояния блока
        boolean isActive = isActive();
        if (wasActive != isActive) {
            updateBlockState(world, pos, state, isActive);
        }

        markDirty();
    }

    /**
     * Обрабатывает сжигание топлива
     */
    protected void processFuel(World world, BlockPos pos, BlockState state) {
        if (burnTime > 0 && energy < maxEnergy) {
            burnTime--;
            energy += config.energyPerTick;
            if (energy > maxEnergy) energy = maxEnergy;
        } else if (burnTime <= 0 && energy < maxEnergy && config.fuelSlot >= 0) {
            ItemStack fuelStack = getStack(config.fuelSlot);
            if (!fuelStack.isEmpty()) {
                int fuelValue = getFuelTime(world, fuelStack);
                if (fuelValue > 0) {
                    burnTime = fuelValue;
                    fuelTime = fuelValue;
                    fuelStack.decrement(1);
                }
            }
        }
    }

    /**
     * Получает время горения топлива
     * Переопределите для кастомной логики
     */
    protected int getFuelTime(World world, ItemStack stack) {
        return world.getFuelRegistry().getFuelTicks(stack);
    }

    /**
     * Основная логика работы машины
     * Переопределите для реализации функционала
     */
    protected abstract void processMainLogic(World world, BlockPos pos, BlockState state);

    /**
     * Проверяет, активна ли машина
     */
    protected boolean isActive() {
        if (config.hasFuelBurning) {
            return burnTime > 0 && energy < maxEnergy;
        }
        return energy > 0;
    }

    /**
     * Обновляет состояние блока (например, LIT)
     */
    protected void updateBlockState(World world, BlockPos pos, BlockState state, boolean active) {
        // Переопределите в наследнике если нужно
    }

    /**
     * Распределяет энергию по проводам
     */
    protected void distributeEnergy(World world, BlockPos pos) {
        if (world.isClient() || energy <= 0) return;

        for (Direction dir : Direction.values()) {
            if (!hasEnergyConnection(world, pos, dir)) continue;

            BlockEntity be = world.getBlockEntity(pos.offset(dir));
            if (!(be instanceof CustomWireBlockEntity wireBE)) continue;

            int toSend = Math.min(energy, config.energyTransferRate);
            int transferred = wireBE.receiveEnergy(toSend, false);
            energy -= transferred;

            if (energy <= 0) break;
        }
    }

    /**
     * Проверяет наличие энергетического соединения
     */
    protected boolean hasEnergyConnection(World world, BlockPos pos, Direction dir) {
        BlockPos neighborPos = pos.offset(dir);
        BlockState neighborState = world.getBlockState(neighborPos);
        return neighborState.getBlock() instanceof CustomWireBlock;
    }

    /**
     * Заряжает батарею в указанном слоте
     */
    protected void chargeBattery() {
        if (config.batterySlot < 0 || config.batterySlot >= inventory.size()) return;

        ItemStack batteryStack = getStack(config.batterySlot);
        if (batteryStack.getItem() instanceof BatteryItem batteryItem) {
            int currentBatteryEnergy = batteryItem.getEnergy(batteryStack);
            int maxBatteryEnergy = batteryItem.getMaxEnergy();

            if (currentBatteryEnergy < maxBatteryEnergy && energy > 0) {
                int transferAmount = Math.min(energy,
                        Math.min(config.batteryChargeRate, maxBatteryEnergy - currentBatteryEnergy));
                int actualTransferred = batteryItem.receiveEnergy(batteryStack, transferAmount);
                energy -= actualTransferred;
            }
        }
    }

    // ===== PROGRESS =====
    protected void resetProgress() {
        this.progress = 0;
    }

    protected boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    protected void increaseCraftingProgress() {
        this.progress++;
    }

    // ===== NBT =====
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);

        nbt.putInt("Energy", energy);

        if (config.hasFuelBurning) {
            nbt.putInt("BurnTime", burnTime);
            nbt.putInt("FuelTime", fuelTime);
        }

        if (config.hasProgress) {
            nbt.putInt("Progress", progress);
            nbt.putInt("MaxProgress", maxProgress);
        }

        writeCustomNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);

        energy = nbt.getInt("Energy").orElse(0);

        if (config.hasFuelBurning) {
            burnTime = nbt.getInt("BurnTime").orElse(0);
            fuelTime = nbt.getInt("FuelTime").orElse(0);
        }

        if (config.hasProgress) {
            progress = nbt.getInt("Progress").orElse(0);
            maxProgress = nbt.getInt("MaxProgress").orElse(72);
        }

        readCustomNbt(nbt, registryLookup);
    }

    /**
     * Переопределите для сохранения кастомных данных
     */
    protected void writeCustomNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    }

    /**
     * Переопределите для загрузки кастомных данных
     */
    protected void readCustomNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    }

    // ===== NETWORK =====
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    // ===== ENERGY STORAGE IMPLEMENTATION =====
    @Override
    public int getCapacity() {
        return maxEnergy;
    }

    @Override
    public int getAmount() {
        return energy;
    }

    @Override
    public void setAmount(int amount) {
        this.energy = Math.max(0, Math.min(amount, maxEnergy));
        markDirty();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!config.canReceiveEnergy || maxReceive <= 0 || energy >= maxEnergy) return 0;

        int received = Math.min(maxReceive, maxEnergy - energy);
        if (!simulate) {
            energy += received;
            markDirty();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!config.canExtractEnergy || maxExtract <= 0 || energy <= 0) return 0;

        int extracted = Math.min(maxExtract, energy);
        if (!simulate) {
            energy -= extracted;
            markDirty();
        }
        return extracted;
    }

    @Override
    public long insert(long amount) {
        return receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), false);
    }

    @Override
    public long extract(long amount) {
        return extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), false);
    }

    @Override
    public boolean supportsExtraction() {
        return config.canExtractEnergy;
    }

    @Override
    public boolean supportsInsertion() {
        return config.canReceiveEnergy;
    }

    // ===== UTILITY =====
    public boolean isEnergyFull() {
        return energy >= maxEnergy;
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    // ===== КОНФИГУРАЦИЯ =====
    /**
     * Конфигурация для BlockEntity
     */
    public static class BlockEntityConfiguration {
        // Инвентарь
        public final int inventorySize;
        public final int fuelSlot;
        public final int batterySlot;

        // Энергия
        public final int maxEnergy;
        public final int energyPerTick;
        public final int energyTransferRate;
        public final boolean canReceiveEnergy;
        public final boolean canExtractEnergy;

        // Функционал
        public final boolean hasFuelBurning;
        public final boolean hasProgress;
        public final boolean canChargeBatteries;
        public final int batteryChargeRate;

        private BlockEntityConfiguration(Builder builder) {
            this.inventorySize = builder.inventorySize;
            this.fuelSlot = builder.fuelSlot;
            this.batterySlot = builder.batterySlot;
            this.maxEnergy = builder.maxEnergy;
            this.energyPerTick = builder.energyPerTick;
            this.energyTransferRate = builder.energyTransferRate;
            this.canReceiveEnergy = builder.canReceiveEnergy;
            this.canExtractEnergy = builder.canExtractEnergy;
            this.hasFuelBurning = builder.hasFuelBurning;
            this.hasProgress = builder.hasProgress;
            this.canChargeBatteries = builder.canChargeBatteries;
            this.batteryChargeRate = builder.batteryChargeRate;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private int inventorySize = 1;
            private int fuelSlot = -1;
            private int batterySlot = -1;
            private int maxEnergy = 10000;
            private int energyPerTick = 10;
            private int energyTransferRate = 100;
            private boolean canReceiveEnergy = false;
            private boolean canExtractEnergy = true;
            private boolean hasFuelBurning = false;
            private boolean hasProgress = false;
            private boolean canChargeBatteries = false;
            private int batteryChargeRate = 50;

            /**
             * Устанавливает размер инвентаря
             * @param size количество слотов (1-36)
             */
            public Builder inventorySize(int size) {
                this.inventorySize = size;
                return this;
            }

            /**
             * Добавляет слот для топлива
             * @param slot индекс слота для топлива
             */
            public Builder fuelSlot(int slot) {
                this.fuelSlot = slot;
                this.hasFuelBurning = true;
                return this;
            }

            /**
             * Добавляет слот для батареи
             * @param slot индекс слота для батареи
             */
            public Builder batterySlot(int slot) {
                this.batterySlot = slot;
                this.canChargeBatteries = true;
                return this;
            }

            /**
             * Устанавливает максимальную энергию
             * @param max максимальная ёмкость (в RF/FE)
             */
            public Builder maxEnergy(int max) {
                this.maxEnergy = max;
                return this;
            }

            /**
             * Устанавливает производство энергии за тик
             * @param amount количество энергии за тик
             */
            public Builder energyPerTick(int amount) {
                this.energyPerTick = amount;
                return this;
            }

            /**
             * Устанавливает скорость передачи энергии
             * @param rate максимальная передача за тик
             */
            public Builder energyTransferRate(int rate) {
                this.energyTransferRate = rate;
                return this;
            }

            /**
             * Машина может принимать энергию
             */
            public Builder canReceiveEnergy() {
                this.canReceiveEnergy = true;
                return this;
            }

            /**
             * Машина может отдавать энергию
             */
            public Builder canExtractEnergy() {
                this.canExtractEnergy = true;
                return this;
            }

            /**
             * Добавляет поддержку сжигания топлива
             */
            public Builder withFuelBurning() {
                this.hasFuelBurning = true;
                return this;
            }

            /**
             * Добавляет систему прогресса
             */
            public Builder withProgress() {
                this.hasProgress = true;
                return this;
            }

            /**
             * Устанавливает скорость зарядки батарей
             * @param rate RF за тик
             */
            public Builder batteryChargeRate(int rate) {
                this.batteryChargeRate = rate;
                return this;
            }

            public BlockEntityConfiguration build() {
                return new BlockEntityConfiguration(this);
            }
        }
    }
}