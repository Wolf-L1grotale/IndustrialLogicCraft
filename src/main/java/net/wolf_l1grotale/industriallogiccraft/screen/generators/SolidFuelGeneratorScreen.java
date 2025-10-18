package net.wolf_l1grotale.industriallogiccraft.screen.generators;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.screen.base.BaseScreen;

public class SolidFuelGeneratorScreen extends BaseScreen<SolidFuelGeneratorScreenHandler> {

    // Текстуры виджетов
    private static final Identifier ARROW_TEXTURE =
            Identifier.of(IndustrialLogicCraft.MOD_ID, "textures/gui/electric/generators/progress.png");
    private static final Identifier FIRE_TEXTURE =
            Identifier.of(IndustrialLogicCraft.MOD_ID, "textures/gui/electric/generators/fire_active.png");
    private static final Identifier FUEL_TEXTURE =
            Identifier.of(IndustrialLogicCraft.MOD_ID, "textures/gui/electric/generators/fuel_progress.png");

    public SolidFuelGeneratorScreen(SolidFuelGeneratorScreenHandler handler,
                                    PlayerInventory inventory, Text title) {
        super(handler, inventory, title, "textures/gui/electric/generators/tgui_solid_fuel_generator.png");
    }

    @Override
    protected void setupWidgets() {
        // Добавляем прогресс-стрелку
        addProgressArrow(94, 35, ARROW_TEXTURE,
                () -> handler.getScaledArrowProgress(), 25, 16);

        // Добавляем индикатор огня
        addFireIndicator(67, 36, FIRE_TEXTURE,
                () -> handler.isBurning());

        // Добавляем индикатор топлива
        addVerticalBar(58, 54, FUEL_TEXTURE,
                () -> handler.getScaledFuelProgress(), 3, 15);

        // Добавляем подсказки
        addTooltip(94, 35, 25, 16,
                () -> Text.literal("Внутренний заряд: " +
                        (int)(handler.getEnergyProgress() * 100) + "%"));

        addTooltip(58, 54, 3, 15,
                () -> Text.literal("Топливо: " +
                        (int)(handler.getScaledFuelProgress() * 100 / 14) + "%"));
    }
}