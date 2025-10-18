package net.wolf_l1grotale.industriallogiccraft.screen.base;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Базовый класс для всех экранов GUI
 * Предоставляет систему виджетов и общую функциональность
 */
public abstract class BaseScreen<T extends ScreenHandler> extends HandledScreen<T> {

    protected final List<Widget> widgets = new ArrayList<>();
    protected final List<TooltipArea> tooltipAreas = new ArrayList<>();

    // Основная текстура GUI
    protected final Identifier guiTexture;

    protected BaseScreen(T handler, PlayerInventory inventory, Text title, String texturePath) {
        super(handler, inventory, title);
        this.guiTexture = Identifier.of(IndustrialLogicCraft.MOD_ID, texturePath);
    }

    @Override
    protected void init() {
        super.init();
        widgets.clear();
        tooltipAreas.clear();
        setupWidgets();
    }

    /**
     * Настройка виджетов. Переопределите этот метод для добавления виджетов
     */
    protected abstract void setupWidgets();

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // Рисуем основной фон
        context.drawTexture(RenderLayer::getGuiTextured, guiTexture, x, y, 0, 0,
                backgroundWidth, backgroundHeight, 256, 256);

        // Рисуем все виджеты
        for (Widget widget : widgets) {
            widget.render(context, x, y, mouseX, mouseY, delta);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);

        // Проверяем подсказки
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        for (TooltipArea area : tooltipAreas) {
            if (area.isMouseOver(mouseX - x, mouseY - y)) {
                context.drawTooltip(this.textRenderer, area.getText(), mouseX, mouseY);
                break; // Показываем только одну подсказку
            }
        }
    }

    // ===== МЕТОДЫ ДЛЯ ДОБАВЛЕНИЯ ВИДЖЕТОВ =====

    /**
     * Добавляет прогресс-стрелку
     */
    protected void addProgressArrow(int x, int y, Identifier texture,
                                    Supplier<Integer> progressSupplier, int maxWidth, int height) {
        widgets.add(new ProgressArrow(x, y, texture, progressSupplier, maxWidth, height));
    }

    /**
     * Добавляет индикатор огня
     */
    protected void addFireIndicator(int x, int y, Identifier texture,
                                    Supplier<Boolean> activeSupplier) {
        widgets.add(new FireIndicator(x, y, texture, activeSupplier));
    }

    /**
     * Добавляет вертикальный прогресс-бар (для топлива, энергии и т.д.)
     */
    protected void addVerticalBar(int x, int y, Identifier texture,
                                  Supplier<Integer> progressSupplier, int width, int maxHeight) {
        widgets.add(new VerticalBar(x, y, texture, progressSupplier, width, maxHeight));
    }

    /**
     * Добавляет горизонтальный прогресс-бар
     */
    protected void addHorizontalBar(int x, int y, Identifier texture,
                                    Supplier<Integer> progressSupplier, int maxWidth, int height) {
        widgets.add(new HorizontalBar(x, y, texture, progressSupplier, maxWidth, height));
    }

    /**
     * Добавляет область с подсказкой
     */
    protected void addTooltip(int x, int y, int width, int height, Supplier<Text> textSupplier) {
        tooltipAreas.add(new TooltipArea(x, y, width, height, textSupplier));
    }

    /**
     * Добавляет энергетический индикатор
     */
    protected void addEnergyBar(int x, int y, Supplier<Float> energySupplier) {
        Identifier energyTexture = Identifier.of(IndustrialLogicCraft.MOD_ID,
                "textures/gui/widgets/energy_bar.png");
        widgets.add(new EnergyBar(x, y, energyTexture, energySupplier));
    }

    // ===== БАЗОВЫЕ ВИДЖЕТЫ =====

    /**
     * Базовый интерфейс для виджетов
     */
    protected interface Widget {
        void render(DrawContext context, int guiX, int guiY, int mouseX, int mouseY, float delta);
    }

    /**
     * Прогресс-стрелка
     */
    protected static class ProgressArrow implements Widget {
        private final int x, y;
        private final Identifier texture;
        private final Supplier<Integer> progressSupplier;
        private final int maxWidth, height;

        public ProgressArrow(int x, int y, Identifier texture,
                             Supplier<Integer> progressSupplier, int maxWidth, int height) {
            this.x = x;
            this.y = y;
            this.texture = texture;
            this.progressSupplier = progressSupplier;
            this.maxWidth = maxWidth;
            this.height = height;
        }

        @Override
        public void render(DrawContext context, int guiX, int guiY, int mouseX, int mouseY, float delta) {
            int progress = progressSupplier.get();
            if (progress > 0) {
                context.drawTexture(RenderLayer::getGuiTextured, texture,
                        guiX + x, guiY + y, 0, 0,
                        progress, height, maxWidth, height);
            }
        }
    }

    /**
     * Индикатор огня
     */
    protected static class FireIndicator implements Widget {
        private final int x, y;
        private final Identifier texture;
        private final Supplier<Boolean> activeSupplier;

        public FireIndicator(int x, int y, Identifier texture, Supplier<Boolean> activeSupplier) {
            this.x = x;
            this.y = y;
            this.texture = texture;
            this.activeSupplier = activeSupplier;
        }

        @Override
        public void render(DrawContext context, int guiX, int guiY, int mouseX, int mouseY, float delta) {
            if (activeSupplier.get()) {
                context.drawTexture(RenderLayer::getGuiTextured, texture,
                        guiX + x, guiY + y, 0, 0, 14, 14, 14, 14);
            }
        }
    }

    /**
     * Вертикальный прогресс-бар
     */
    protected static class VerticalBar implements Widget {
        private final int x, y;
        private final Identifier texture;
        private final Supplier<Integer> progressSupplier;
        private final int width, maxHeight;

        public VerticalBar(int x, int y, Identifier texture,
                           Supplier<Integer> progressSupplier, int width, int maxHeight) {
            this.x = x;
            this.y = y;
            this.texture = texture;
            this.progressSupplier = progressSupplier;
            this.width = width;
            this.maxHeight = maxHeight;
        }

        @Override
        public void render(DrawContext context, int guiX, int guiY, int mouseX, int mouseY, float delta) {
            int height = progressSupplier.get();
            if (height > 0) {
                int renderY = guiY + y + (maxHeight - height);
                context.drawTexture(RenderLayer::getGuiTextured, texture,
                        guiX + x, renderY,
                        0, maxHeight - height,
                        width, height,
                        width, maxHeight);
            }
        }
    }

    /**
     * Горизонтальный прогресс-бар
     */
    protected static class HorizontalBar implements Widget {
        private final int x, y;
        private final Identifier texture;
        private final Supplier<Integer> progressSupplier;
        private final int maxWidth, height;

        public HorizontalBar(int x, int y, Identifier texture,
                             Supplier<Integer> progressSupplier, int maxWidth, int height) {
            this.x = x;
            this.y = y;
            this.texture = texture;
            this.progressSupplier = progressSupplier;
            this.maxWidth = maxWidth;
            this.height = height;
        }

        @Override
        public void render(DrawContext context, int guiX, int guiY, int mouseX, int mouseY, float delta) {
            int width = progressSupplier.get();
            if (width > 0) {
                context.drawTexture(RenderLayer::getGuiTextured, texture,
                        guiX + x, guiY + y, 0, 0,
                        width, height, maxWidth, height);
            }
        }
    }

    /**
     * Энергетический индикатор
     */
    protected static class EnergyBar implements Widget {
        private final int x, y;
        private final Identifier texture;
        private final Supplier<Float> energySupplier;

        public EnergyBar(int x, int y, Identifier texture, Supplier<Float> energySupplier) {
            this.x = x;
            this.y = y;
            this.texture = texture;
            this.energySupplier = energySupplier;
        }

        @Override
        public void render(DrawContext context, int guiX, int guiY, int mouseX, int mouseY, float delta) {
            float energy = energySupplier.get();
            int height = (int)(52 * energy); // 52 - стандартная высота энергобара

            if (height > 0) {
                context.drawTexture(RenderLayer::getGuiTextured, texture,
                        guiX + x, guiY + y + (52 - height),
                        0, 52 - height,
                        16, height,
                        16, 52);
            }
        }
    }

    /**
     * Область с подсказкой
     */
    protected static class TooltipArea {
        private final int x, y, width, height;
        private final Supplier<Text> textSupplier;

        public TooltipArea(int x, int y, int width, int height, Supplier<Text> textSupplier) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.textSupplier = textSupplier;
        }

        public boolean isMouseOver(int mouseX, int mouseY) {
            return mouseX >= x && mouseX < x + width &&
                    mouseY >= y && mouseY < y + height;
        }

        public Text getText() {
            return textSupplier.get();
        }
    }
}