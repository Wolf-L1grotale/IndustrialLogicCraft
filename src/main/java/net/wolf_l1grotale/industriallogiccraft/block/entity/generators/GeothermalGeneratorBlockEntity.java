package net.wolf_l1grotale.industriallogiccraft.block.entity.generators;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;
import net.wolf_l1grotale.industriallogiccraft.block.entity.base.BaseEnergyBlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * BlockEntity для генератора на жидком топливе
 * Сжигает топливо и производит энергию
 */


public class GeothermalGeneratorBlockEntity extends BaseEnergyBlockEntity {

    private static final int INPUT_SLOT = 0;   // Слот для топлива
    private static final int OUTPUT_SLOT = 1;  // Слот для батарей

    public GeothermalGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, BlockEntityConfiguration config) {
        super(type, pos, state, config);
    }

    public GeothermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(
                ModBlockEntities.GEOTHERMAL_GENERATOR_BE,
                pos,
                state,
                BlockEntityConfiguration.builder()
                        .inventorySize(2)              // 2 слота: топливо + батарея
                        .fuelSlot(INPUT_SLOT)          // Топливо в первом слоте
                        .batterySlot(OUTPUT_SLOT)      // Батарея во втором слоте
                        .maxEnergy(10000)              // Максимум 10000 EU
                        .energyPerTick(10)      // Производит 10 EU/тик
                        .energyTransferRate(100)       // Передаёт до 100 EU/тик
                        .canExtractEnergy()            // Может отдавать энергию
                        .batteryChargeRate(50)         // Заряжает батареи по 50 EU/тик
                        .withFuelBurning()             // Сжигает топливо
                        .build()
        );
    }

    @Override
    public Text getDisplayName() {
        return null;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;
    }

    @Override
    protected void processMainLogic(World world, BlockPos pos, BlockState state) {

    }
}
