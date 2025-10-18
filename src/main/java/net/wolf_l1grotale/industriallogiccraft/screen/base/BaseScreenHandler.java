package net.wolf_l1grotale.industriallogiccraft.screen.base;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Базовый класс для всех ScreenHandler'ов
 * Предоставляет общую функциональность и упрощает создание GUI
 */
public abstract class BaseScreenHandler extends ScreenHandler {

    protected final Inventory inventory;
    protected final PropertyDelegate propertyDelegate;
    protected final BlockEntity blockEntity;
    protected final PlayerInventory playerInventory;

    // Конфигурация слотов
    private final ScreenConfiguration config;
    private int machineSlotCount = 0;

    /**
     * Конструктор для клиента (с позицией)
     */
    protected BaseScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory,
                                BlockPos pos, ScreenConfiguration config) {
        this(type, syncId, playerInventory,
                playerInventory.player.getWorld().getBlockEntity(pos),
                new ArrayPropertyDelegate(config.propertyCount),
                config);
    }

    /**
     * Конструктор для сервера (с BlockEntity)
     */
    protected BaseScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory,
                                BlockEntity blockEntity, PropertyDelegate propertyDelegate,
                                ScreenConfiguration config) {
        super(type, syncId);
        this.inventory = (Inventory) blockEntity;
        this.blockEntity = blockEntity;
        this.propertyDelegate = propertyDelegate;
        this.playerInventory = playerInventory;
        this.config = config;

        // Добавляем слоты машины
        addMachineSlots();

        // Добавляем слоты игрока
        if (config.hasPlayerInventory) {
            addPlayerInventory(playerInventory);
            addPlayerHotbar(playerInventory);
        }

        // Добавляем свойства для синхронизации
        addProperties(propertyDelegate);
    }

    /**
     * Добавляет слоты машины. Переопределите этот метод
     */
    protected abstract void addMachineSlots();

    /**
     * Добавляет слот машины с автоматическим подсчетом
     */
    protected void addMachineSlot(int x, int y) {
        this.addSlot(new Slot(inventory, machineSlotCount++, x, y));
    }

    /**
     * Добавляет слот машины с кастомной логикой
     */
    protected void addMachineSlot(Slot slot) {
        this.addSlot(slot);
        machineSlotCount++;
    }

    /**
     * Добавляет слот с фильтром предметов
     */
    protected void addFilteredSlot(int index, int x, int y, Predicate<ItemStack> filter) {
        this.addSlot(new FilteredSlot(inventory, index, x, y, filter));
        machineSlotCount++;
    }

    /**
     * Добавляет выходной слот (только забирать)
     */
    protected void addOutputSlot(int index, int x, int y) {
        this.addSlot(new OutputSlot(inventory, index, x, y));
        machineSlotCount++;
    }

    // ===== ИНВЕНТАРЬ ИГРОКА =====

    private void addPlayerInventory(PlayerInventory playerInventory) {
        int startX = config.playerInventoryX;
        int startY = config.playerInventoryY;

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        startX + col * 18,
                        startY + row * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        int startX = config.playerHotbarX;
        int startY = config.playerHotbarY;

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, startX + i * 18, startY));
        }
    }

    // ===== SHIFT-CLICK ЛОГИКА =====

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();

            // Из слотов машины в инвентарь игрока
            if (slotIndex < machineSlotCount) {
                if (!this.insertItem(slotStack, machineSlotCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // Из инвентаря игрока в слоты машины
            else {
                if (!this.insertItem(slotStack, 0, machineSlotCount, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return result;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ СВОЙСТВ =====

    /**
     * Получает значение свойства
     */
    protected int getProperty(int index) {
        return propertyDelegate.get(index);
    }

    /**
     * Устанавливает значение свойства (если нужно из наследника)
     * Обычно это делается автоматически через PropertyDelegate
     */
    protected void setPropertyValue(int index, int value) {
        propertyDelegate.set(index, value);
    }

    /**
     * Возвращает прогресс в процентах (0.0 - 1.0)
     */
    protected float getProgress(int currentIndex, int maxIndex) {
        int current = getProperty(currentIndex);
        int max = getProperty(maxIndex);
        return max == 0 ? 0 : (float) current / max;
    }

    /**
     * Возвращает масштабированный прогресс для GUI
     */
    protected int getScaledProgress(int currentIndex, int maxIndex, int pixels) {
        int current = getProperty(currentIndex);
        int max = getProperty(maxIndex);
        return max != 0 && current != 0 ? current * pixels / max : 0;
    }

    // ===== КАСТОМНЫЕ СЛОТЫ =====

    /**
     * Слот с фильтром предметов
     */
    public static class FilteredSlot extends Slot {
        private final Predicate<ItemStack> filter;

        public FilteredSlot(Inventory inventory, int index, int x, int y, Predicate<ItemStack> filter) {
            super(inventory, index, x, y);
            this.filter = filter;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return filter.test(stack);
        }
    }

    /**
     * Выходной слот (только забирать)
     */
    public static class OutputSlot extends Slot {
        public OutputSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }
    }

    // ===== КОНФИГУРАЦИЯ =====

    /**
     * Конфигурация экрана
     */
    public static class ScreenConfiguration {
        public final int playerInventoryX;
        public final int playerInventoryY;
        public final int playerHotbarX;
        public final int playerHotbarY;
        public final boolean hasPlayerInventory;
        public final int propertyCount;

        private ScreenConfiguration(Builder builder) {
            this.playerInventoryX = builder.playerInventoryX;
            this.playerInventoryY = builder.playerInventoryY;
            this.playerHotbarX = builder.playerHotbarX;
            this.playerHotbarY = builder.playerHotbarY;
            this.hasPlayerInventory = builder.hasPlayerInventory;
            this.propertyCount = builder.propertyCount;
        }

        public static Builder builder() {
            return new Builder();
        }

        /**
         * Стандартная конфигурация для обычного GUI
         */
        public static ScreenConfiguration standard(int propertyCount) {
            return builder()
                    .playerInventory(8, 84)
                    .playerHotbar(8, 142)
                    .properties(propertyCount)
                    .build();
        }

        public static class Builder {
            private int playerInventoryX = 8;
            private int playerInventoryY = 84;
            private int playerHotbarX = 8;
            private int playerHotbarY = 142;
            private boolean hasPlayerInventory = true;
            private int propertyCount = 0;

            /**
             * Устанавливает позицию инвентаря игрока
             */
            public Builder playerInventory(int x, int y) {
                this.playerInventoryX = x;
                this.playerInventoryY = y;
                this.hasPlayerInventory = true;
                return this;
            }

            /**
             * Устанавливает позицию хотбара
             */
            public Builder playerHotbar(int x, int y) {
                this.playerHotbarX = x;
                this.playerHotbarY = y;
                return this;
            }

            /**
             * Отключает инвентарь игрока
             */
            public Builder noPlayerInventory() {
                this.hasPlayerInventory = false;
                return this;
            }

            /**
             * Устанавливает количество свойств для синхронизации
             */
            public Builder properties(int count) {
                this.propertyCount = count;
                return this;
            }

            public ScreenConfiguration build() {
                return new ScreenConfiguration(this);
            }
        }
    }
}