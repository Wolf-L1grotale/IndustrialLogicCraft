package net.wolf_l1grotale.industriallogiccraft.screen.generators;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.wolf_l1grotale.industriallogiccraft.screen.ModScreenHandlers;
import net.wolf_l1grotale.industriallogiccraft.screen.base.BaseScreenHandler;

public class GeothermalGeneratorScreenHandler extends BaseScreenHandler {

    private static final ScreenConfiguration CONFIG = ScreenConfiguration.builder()
            .properties(4) // energy, maxEnergy, heat, maxHeat
            .build();

    public GeothermalGeneratorScreenHandler(int syncId, PlayerInventory inventory, BlockPos pos) {
        super(ModScreenHandlers.GEOTHERMAL_GENERATOR_SCREEN_HANDLER, syncId, inventory, pos, CONFIG);
    }

    @Override
    protected void addMachineSlots() {
        // Слот для ведра с лавой
        addFilteredSlot(0, 80, 35, stack -> stack.getItem() == Items.LAVA_BUCKET);

        // Слот для пустого ведра (выход)
        addOutputSlot(1, 80, 55);
    }

    // Методы для GUI
    public int getHeatLevel() {
        return getScaledProgress(2, 3, 52); // heat/maxHeat * 52 пикселя
    }

    public int getEnergyLevel() {
        return getScaledProgress(0, 1, 52); // energy/maxEnergy * 52 пикселя
    }
}
