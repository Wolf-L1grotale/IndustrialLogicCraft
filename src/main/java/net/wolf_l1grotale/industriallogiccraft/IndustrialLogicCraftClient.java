package net.wolf_l1grotale.industriallogiccraft;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;
import net.wolf_l1grotale.industriallogiccraft.block.entity.renderer.PedestalBlockEntityRenderer;

public class IndustrialLogicCraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        BlockEntityRendererFactories.register(ModBlockEntities.PEDESTAL_BE, PedestalBlockEntityRenderer::new);

    }
}
