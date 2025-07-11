package net.wolf_l1grotale.industriallogiccraft;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;
import net.wolf_l1grotale.industriallogiccraft.block.entity.renderer.PedestalBlockEntityRenderer;
import net.wolf_l1grotale.industriallogiccraft.screen.ModScreenHandlers;
import net.wolf_l1grotale.industriallogiccraft.screen.custom.GrowthChamberScreen;
import net.wolf_l1grotale.industriallogiccraft.screen.custom.PedestalScreen;

public class IndustrialLogicCraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        BlockEntityRendererFactories.register(ModBlockEntities.PEDESTAL_BE, PedestalBlockEntityRenderer::new);
        HandledScreens.register(ModScreenHandlers.PEDESTAL_SCREEN_HANDLER, PedestalScreen::new);
        HandledScreens.register(ModScreenHandlers.GROWTH_CHAMBER_SCREEN_HANDLER, GrowthChamberScreen::new);

    }
}
