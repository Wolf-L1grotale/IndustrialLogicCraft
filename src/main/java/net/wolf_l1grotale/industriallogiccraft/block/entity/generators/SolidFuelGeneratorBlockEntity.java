package net.wolf_l1grotale.industriallogiccraft.block.entity.generators;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wolf_l1grotale.industriallogiccraft.block.electric.generators.SolidFuelGeneratorBlock;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;
import net.wolf_l1grotale.industriallogiccraft.block.entity.base.BaseEnergyBlockEntity;
import net.wolf_l1grotale.industriallogiccraft.screen.generators.SolidFuelGeneratorScreenHandler;
import org.jetbrains.annotations.Nullable;

/**
 * BlockEntity для генератора на твёрдом топливе
 * Сжигает топливо и производит энергию
 */
public class SolidFuelGeneratorBlockEntity extends BaseEnergyBlockEntity {

    private static final int INPUT_SLOT = 0;   // Слот для топлива
    private static final int OUTPUT_SLOT = 1;  // Слот для батарей

    public SolidFuelGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(
                ModBlockEntities.SOLID_FUEL_GENERATOR_BE,
                pos,
                state,
                BlockEntityConfiguration.builder()
                        .inventorySize(2)              // 2 слота: топливо + батарея
                        .fuelSlot(INPUT_SLOT)          // Топливо в первом слоте
                        .batterySlot(OUTPUT_SLOT)      // Батарея во втором слоте
                        .maxEnergy(10000)              // Максимум 10000 EU
                        .energyPerTick(10)             // Производит 10 EU/тик
                        .energyTransferRate(100)       // Передаёт до 100 EU/тик
                        .canExtractEnergy()            // Может отдавать энергию
                        .batteryChargeRate(50)         // Заряжает батареи по 50 EU/тик
                        .withFuelBurning()             // Сжигает топливо
                        .build()
        );
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.industriallogiccraft.solid_fuel_generator");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SolidFuelGeneratorScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    protected void processMainLogic(World world, BlockPos pos, BlockState state) {
        // Вся логика уже в базовом классе
        // Можно добавить дополнительную логику здесь если нужно
    }

    @Override
    protected void updateBlockState(World world, BlockPos pos, BlockState state, boolean active) {
        // Обновляем состояние LIT
        boolean currentLit = state.get(SolidFuelGeneratorBlock.LIT);
        if (currentLit != active) {
            world.setBlockState(pos, state.with(SolidFuelGeneratorBlock.LIT, active), 3);
        }
    }
}