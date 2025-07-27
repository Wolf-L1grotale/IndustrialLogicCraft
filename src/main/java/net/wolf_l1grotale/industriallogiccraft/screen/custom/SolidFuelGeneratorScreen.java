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
    private static final Identifier FIRE_TEXTURE =
            Identifier.of(IndustrialLogicCraft.MOD_ID, "textures/gui/generators/electric/fire_active.png");
    private static final Identifier FUEL_TEXTURE =
            Identifier.of(IndustrialLogicCraft.MOD_ID, "textures/gui/generators/electric/fuel_progress.png");


    public SolidFuelGeneratorScreen(SolidFuelGeneratorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(RenderLayer::getGuiTextured, GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 176, 166);

        renderProgressArrow(context, x, y);
        renderFireIndicator(context, x, y);
        renderFuelIndicator(context, x, y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        /*if(handler.isCrafting()) {*/
            // Указывается начальная движения прогресса
            context.drawTexture(RenderLayer::getGuiTextured, ARROW_TEXTURE, x + 94, y + 35, 0, 0,
                    handler.getScaledArrowProgress(), 16, 25, 17);
        /*}*/
    }

    private void renderFireIndicator(DrawContext context, int x, int y) {
        if (handler.isBurning()) {
            // Нарисовать огонь. Подбери координаты и размеры под свою текстуру!
            context.drawTexture(RenderLayer::getGuiTextured, FIRE_TEXTURE, x + 67, y + 36, 0, 0, 14, 14, 14, 14);
        }
    }

    private void renderFuelIndicator(DrawContext context, int x, int y) {
        // Получаем высоту индикатора в пикселях (0-15)
        int height = handler.getScaledFuelProgress();

        if (height > 0) {
            // Позиция слева от слота топлива (подстройте под ваш интерфейс)
            int fuelX = x + 58;  // примерно слева от слота
            int fuelY = y + 54 + (15 - height);  // снизу вверх

            // Рисуем только часть текстуры (снизу вверх)
            context.drawTexture(RenderLayer::getGuiTextured, FUEL_TEXTURE,
                    fuelX, fuelY,       // позиция на экране
                    0, 15 - height,     // позиция начала в текстуре (сверху)
                    3, height,          // размер отображаемой части (3×height)
                    3, 15);             // полный размер текстуры (3×15)
        }
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

        int fuelX = x + 58;
        int fuelY = y + 54;
        int fuelWidth = 3;
        int fuelHeight = 15;

        if (mouseX >= fuelX && mouseX < fuelX + fuelWidth &&
                mouseY >= fuelY && mouseY < fuelY + fuelHeight) {
            // Текст подсказки
            context.drawTooltip(
                    this.textRenderer,
                    Text.literal("Топливо: " +
                            (int)(handler.getScaledFuelProgress() * 100 / 14) + "%"),
                    mouseX, mouseY
            );
        }
    }
}
