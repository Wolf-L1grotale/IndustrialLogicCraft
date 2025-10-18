package net.wolf_l1grotale.industriallogiccraft.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилита для упрощённой регистрации клиентских компонентов
 */
public class ClientRegistry {

    private static final List<Runnable> registrations = new ArrayList<>();

    /**
     * Регистрирует экран для ScreenHandler
     */
    public static <H extends ScreenHandler, S extends HandledScreen<H>> void registerScreen(
            ScreenHandlerType<? extends H> handlerType,
            HandledScreens.Provider<H, S> factory) {
        registrations.add(() -> HandledScreens.register(handlerType, factory));
    }

    /**
     * Регистрирует рендерер для BlockEntity
     */
    public static <T extends BlockEntity> void registerBlockEntityRenderer(
            BlockEntityType<? extends T> blockEntityType,
            BlockEntityRendererFactory<T> factory) {
        registrations.add(() -> BlockEntityRendererFactories.register(blockEntityType, factory));
    }

    /**
     * Выполняет все зарегистрированные действия
     */
    public static void initialize() {
        IndustrialLogicCraft.LOGGER.info("Initializing client components for " + IndustrialLogicCraft.MOD_ID);
        registrations.forEach(Runnable::run);
        registrations.clear();
    }

    /**
     * Builder для массовой регистрации
     */
    public static class Builder {
        private final List<Runnable> builderRegistrations = new ArrayList<>();

        /**
         * Добавляет экран
         */
        public <H extends ScreenHandler, S extends HandledScreen<H>> Builder screen(
                ScreenHandlerType<? extends H> handlerType,
                HandledScreens.Provider<H, S> factory) {
            builderRegistrations.add(() -> HandledScreens.register(handlerType, factory));
            return this;
        }

        /**
         * Добавляет рендерер BlockEntity
         */
        public <T extends BlockEntity> Builder blockEntityRenderer(
                BlockEntityType<? extends T> blockEntityType,
                BlockEntityRendererFactory<T> factory) {
            builderRegistrations.add(() -> BlockEntityRendererFactories.register(blockEntityType, factory));
            return this;
        }

        /**
         * Регистрирует все добавленные компоненты
         */
        public void register() {
            registrations.addAll(builderRegistrations);
        }
    }

    /**
     * Создаёт новый Builder
     */
    public static Builder builder() {
        return new Builder();
    }
}