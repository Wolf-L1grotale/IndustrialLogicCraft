package net.wolf_l1grotale.industriallogiccraft.client;

import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;
import net.wolf_l1grotale.industriallogiccraft.block.entity.renderer.PedestalBlockEntityRenderer;

/**
 * Регистрация всех рендереров BlockEntity
 */
public class ModBlockEntityRenderers {

    public static void register() {
        ClientRegistry.builder()
                // ===== ДЕКОРАТИВНЫЕ БЛОКИ =====
                .blockEntityRenderer(ModBlockEntities.PEDESTAL_BE, PedestalBlockEntityRenderer::new)

                // Добавьте другие рендереры здесь, когда они появятся
                // .blockEntityRenderer(ModBlockEntities.ANIMATED_BLOCK_BE, AnimatedBlockRenderer::new)

                .register();
    }
}