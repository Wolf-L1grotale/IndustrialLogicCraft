package net.wolf_l1grotale.industriallogiccraft.screen.custom;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;

public class SolidFuelGeneratorScreen extends HandledScreen<SolidFuelGeneratorScreenHandler> {
    private static final Identifier GUI_TEXTURE = Identifier.of(IndustrialLogicCraft.MOD_ID, "textures/gui/generators/electric/tgui_solid_fuel_generator.png");
    private static final Identifier ARROW_TEXTURE =
            Identifier.of(IndustrialLogicCraft.MOD_ID, "textures/gui/generators/electric/progress.png");


    public SolidFuelGeneratorScreen(SolidFuelGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(RenderLayer::getGuiTextured, GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        renderProgressArrow(context, x, y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if(handler.isCrafting()) {
            // Указывается начальная движения прогресса
            context.drawTexture(RenderLayer::getGuiTextured, ARROW_TEXTURE, x + 94, y + 35, 0, 0,
                    handler.getScaledArrowProgress(), 16, 25, 17);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
