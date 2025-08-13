package net.wolf_l1grotale.industriallogiccraft.screen.electric;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;

public class BlockBatteryBoxScreen extends HandledScreen<BlockBatteryBoxScreenHandler> {
    private static final Identifier GUI_TEXTURE = Identifier.of(IndustrialLogicCraft.MOD_ID, "textures/gui/electric/storage/tgui_block_battery_box.png");
    private static final Identifier CHARGE_LEVEL = Identifier.of(IndustrialLogicCraft.MOD_ID, "textures/gui/electric/storage/progress.png");

    public BlockBatteryBoxScreen(BlockBatteryBoxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(RenderLayer::getGuiTextured, GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 176, 166);

        renderProgressArrow(context, x, y);
        //renderFireIndicator(context, x, y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        /*if(handler.isCrafting()) {*/
        // Указывается начальная движения прогресса
        context.drawTexture(RenderLayer::getGuiTextured, CHARGE_LEVEL, x + 94, y + 35, 0, 0,
                handler.getScaledArrowProgressEnegryCharge(), 16, 25, 17);
        /*}*/
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);

        // Координаты GUI
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // Координаты и размеры стрелки
        int arrowX = x + 94;
        int arrowY = y + 35;
        int arrowWidth = 25; // максимальная ширина стрелки
        int arrowHeight = 16;

        // Проверка наведения мыши
        if (mouseX >= arrowX && mouseX < arrowX + arrowWidth &&
                mouseY >= arrowY && mouseY < arrowY + arrowHeight) {
            // Текст подсказки
            context.drawTooltip(
                    this.textRenderer,
                    Text.literal("Внутренний заряд: " +
                            (int)(handler.getEnergyProgress() * 100) + "%"),
                    mouseX, mouseY
            );
        }
    }

}
