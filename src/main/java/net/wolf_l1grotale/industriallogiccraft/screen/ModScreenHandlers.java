package net.wolf_l1grotale.industriallogiccraft.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.screen.custom.GrowthChamberScreenHandler;
import net.wolf_l1grotale.industriallogiccraft.screen.custom.PedestalScreenHandler;
import net.wolf_l1grotale.industriallogiccraft.screen.custom.SolidFuelGeneratorScreenHandler;

public class ModScreenHandlers {
    public static final ScreenHandlerType<PedestalScreenHandler> PEDESTAL_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(IndustrialLogicCraft.MOD_ID, "pedestal_screen_handler"),
                    new ExtendedScreenHandlerType<>(PedestalScreenHandler::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<GrowthChamberScreenHandler> GROWTH_CHAMBER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(IndustrialLogicCraft.MOD_ID, "growth_chamber_screen_handler"),
                    new ExtendedScreenHandlerType<>(GrowthChamberScreenHandler::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<SolidFuelGeneratorScreenHandler> SOLID_FUEL_GENERATOR_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(IndustrialLogicCraft.MOD_ID, "solid_fuel_generator_screen_handler"),
                    new ExtendedScreenHandlerType<>(SolidFuelGeneratorScreenHandler::new, BlockPos.PACKET_CODEC));
    public static void registerScreenHandlers(){
        IndustrialLogicCraft.LOGGER.info("Register Mod Screen " + IndustrialLogicCraft.MOD_ID);

    }
}
