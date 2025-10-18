package net.wolf_l1grotale.industriallogiccraft.screen.generators;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.wolf_l1grotale.industriallogiccraft.block.entity.generators.SolidFuelGeneratorBlockEntity;
import net.wolf_l1grotale.industriallogiccraft.item.battery.BatteryItem;
import net.wolf_l1grotale.industriallogiccraft.screen.ModScreenHandlers;
import net.wolf_l1grotale.industriallogiccraft.screen.base.BaseScreenHandler;

public class SolidFuelGeneratorScreenHandler extends BaseScreenHandler {

    // Индексы свойств
    private static final int PROPERTY_PROGRESS = 0;
    private static final int PROPERTY_MAX_PROGRESS = 1;
    private static final int PROPERTY_ENERGY = 2;
    private static final int PROPERTY_MAX_ENERGY = 3;
    private static final int PROPERTY_BURN_TIME = 4;
    private static final int PROPERTY_FUEL_TIME = 5;

    // Конфигурация экрана
    private static final ScreenConfiguration CONFIG = ScreenConfiguration.builder()
            .playerInventory(8, 84)
            .playerHotbar(8, 142)
            .properties(6)
            .build();

    public final SolidFuelGeneratorBlockEntity blockEntity;

    // Конструктор для клиента
    public SolidFuelGeneratorScreenHandler(int syncId, PlayerInventory inventory, BlockPos pos) {
        super(ModScreenHandlers.SOLID_FUEL_GENERATOR_SCREEN_HANDLER, syncId, inventory, pos, CONFIG);
        this.blockEntity = (SolidFuelGeneratorBlockEntity) super.blockEntity;
    }

    // Конструктор для сервера
    public SolidFuelGeneratorScreenHandler(int syncId, PlayerInventory playerInventory,
                                           BlockEntity blockEntity, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.SOLID_FUEL_GENERATOR_SCREEN_HANDLER, syncId, playerInventory,
                blockEntity, propertyDelegate, CONFIG);
        this.blockEntity = (SolidFuelGeneratorBlockEntity) blockEntity;
    }

    @Override
    protected void addMachineSlots() {
        // Слот для топлива (индекс 0, позиция 65, 53)
        addFilteredSlot(0, 65, 53, stack -> {
            // Принимаем только топливо
            return stack.getItem() == Items.COAL ||
                    stack.getItem() == Items.CHARCOAL ||
                    stack.getItem() == Items.COAL_BLOCK ||
                    stack.getItem() == Items.LAVA_BUCKET ||
                    stack.getItem() == Items.BLAZE_ROD;
        });

        // Слот для батареи (индекс 1, позиция 65, 17)
        addFilteredSlot(1, 65, 17, stack -> stack.getItem() instanceof BatteryItem);
    }

    // ===== МЕТОДЫ ДЛЯ GUI (для совместимости с SolidFuelGeneratorScreen) =====

    /**
     * Проверяет, горит ли генератор
     */
    public boolean isBurning() {
        return blockEntity.isBurning() && !blockEntity.isEnergyFull();
    }

    /**
     * Возвращает прогресс энергии от 0.0 до 1.0
     */
    public float getEnergyProgress() {
        return getProgress(PROPERTY_ENERGY, PROPERTY_MAX_ENERGY);
    }

    /**
     * Возвращает масштабированный прогресс стрелки для GUI
     * Используется для отрисовки стрелки прогресса
     */
    public int getScaledArrowProgress() {
        int energy = getProperty(PROPERTY_ENERGY);
        int maxEnergy = getProperty(PROPERTY_MAX_ENERGY);
        int arrowWidth = 25; // ширина текстуры стрелки в пикселях

        if (maxEnergy == 0) return 0;
        return (int) ((float) energy / maxEnergy * arrowWidth);
    }

    /**
     * Возвращает масштабированный прогресс топлива для GUI
     * Используется для отрисовки индикатора топлива (0-14 пикселей)
     */
    public int getScaledFuelProgress() {
        int burnTime = getProperty(PROPERTY_BURN_TIME);
        int fuelTime = getProperty(PROPERTY_FUEL_TIME);

        if (fuelTime == 0 || burnTime == 0) return 0;
        return burnTime * 14 / fuelTime; // 14 - высота индикатора огня
    }

    /**
     * Проверяет, крафтится ли что-то (для совместимости)
     */
    public boolean isCrafting() {
        return getProperty(PROPERTY_PROGRESS) > 0;
    }

    // ===== ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ДЛЯ ИНФОРМАЦИИ =====

    /**
     * Возвращает текущую энергию
     */
    public int getEnergy() {
        return getProperty(PROPERTY_ENERGY);
    }

    /**
     * Возвращает максимальную энергию
     */
    public int getMaxEnergy() {
        return getProperty(PROPERTY_MAX_ENERGY);
    }

    /**
     * Возвращает оставшееся время горения
     */
    public int getBurnTime() {
        return getProperty(PROPERTY_BURN_TIME);
    }

    /**
     * Возвращает полное время горения текущего топлива
     */
    public int getFuelTime() {
        return getProperty(PROPERTY_FUEL_TIME);
    }

    /**
     * Проверяет, полна ли энергия
     */
    public boolean isEnergyFull() {
        return getEnergy() >= getMaxEnergy();
    }
}