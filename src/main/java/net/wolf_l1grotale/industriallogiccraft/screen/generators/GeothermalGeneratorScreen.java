package net.wolf_l1grotale.industriallogiccraft.screen.generators;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;

public class GeothermalGeneratorScreen extends HandledScreen<GeothermalGeneratorScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of(IndustrialLogicCraft.MOD_ID,
            "textures/gui/tgui_template.png");

    public GeothermalGeneratorScreen(GeothermalGeneratorScreenHandler handler,
                                     PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // Правильный вызов с RenderLayer и размерами текстуры
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0,
                backgroundWidth, backgroundHeight, 256, 256);
    }
}