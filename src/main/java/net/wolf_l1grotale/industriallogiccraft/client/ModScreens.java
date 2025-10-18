package net.wolf_l1grotale.industriallogiccraft.client;

import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;
import net.wolf_l1grotale.industriallogiccraft.block.entity.renderer.PedestalBlockEntityRenderer;
import net.wolf_l1grotale.industriallogiccraft.screen.ModScreenHandlers;
import net.wolf_l1grotale.industriallogiccraft.screen.custom.GrowthChamberScreen;
import net.wolf_l1grotale.industriallogiccraft.screen.custom.PedestalScreen;
import net.wolf_l1grotale.industriallogiccraft.screen.generators.GeothermalGeneratorScreen;
import net.wolf_l1grotale.industriallogiccraft.screen.generators.SolidFuelGeneratorScreen;
import net.wolf_l1grotale.industriallogiccraft.screen.electric.BlockBatteryBoxScreen;

/**
 * Регистрация всех экранов GUI
 */
public class ModScreens {

    public static void register() {
        ClientRegistry.builder()
                // ===== ДЕКОРАТИВНЫЕ БЛОКИ =====
                .screen(ModScreenHandlers.PEDESTAL_SCREEN_HANDLER, PedestalScreen::new)

                // ===== МАШИНЫ =====
                .screen(ModScreenHandlers.GROWTH_CHAMBER_SCREEN_HANDLER, GrowthChamberScreen::new)

                // ===== ГЕНЕРАТОРЫ =====
                .screen(ModScreenHandlers.SOLID_FUEL_GENERATOR_SCREEN_HANDLER, SolidFuelGeneratorScreen::new)
                .screen(ModScreenHandlers.GEOTHERMAL_GENERATOR_SCREEN_HANDLER, GeothermalGeneratorScreen::new)

                // ===== ХРАНИЛИЩА ЭНЕРГИИ =====
                .screen(ModScreenHandlers.BLOCK_BATTERY_BOX_SCREEN_HANDLER, BlockBatteryBoxScreen::new)

                .register();
    }
}