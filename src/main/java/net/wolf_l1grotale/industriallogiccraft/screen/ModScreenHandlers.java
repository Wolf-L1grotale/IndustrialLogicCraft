package net.wolf_l1grotale.industriallogiccraft.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.screen.custom.GrowthChamberScreenHandler;
import net.wolf_l1grotale.industriallogiccraft.screen.custom.PedestalScreenHandler;
import net.wolf_l1grotale.industriallogiccraft.screen.generators.GeothermalGeneratorScreenHandler;
import net.wolf_l1grotale.industriallogiccraft.screen.generators.SolidFuelGeneratorScreenHandler;
import net.wolf_l1grotale.industriallogiccraft.screen.electric.BlockBatteryBoxScreenHandler;

/**
 * Регистрация всех ScreenHandler'ов для GUI
 */
public class ModScreenHandlers {

    // ===== ДЕКОРАТИВНЫЕ БЛОКИ =====
    public static final ScreenHandlerType<PedestalScreenHandler> PEDESTAL_SCREEN_HANDLER =
            ScreenHandlerBuilder.createForBlockPos("pedestal_screen_handler", PedestalScreenHandler::new)
                    .build();

    // ===== МАШИНЫ =====
    public static final ScreenHandlerType<GrowthChamberScreenHandler> GROWTH_CHAMBER_SCREEN_HANDLER =
            ScreenHandlerBuilder.createForBlockPos("growth_chamber_screen_handler", GrowthChamberScreenHandler::new)
                    .build();

    // ===== ГЕНЕРАТОРЫ =====
    public static final ScreenHandlerType<SolidFuelGeneratorScreenHandler> SOLID_FUEL_GENERATOR_SCREEN_HANDLER =
            ScreenHandlerBuilder.createForBlockPos("solid_fuel_generator_screen_handler", SolidFuelGeneratorScreenHandler::new)
                    .build();
    public static final ScreenHandlerType<GeothermalGeneratorScreenHandler> GEOTHERMAL_GENERATOR_SCREEN_HANDLER =
            ScreenHandlerBuilder.createForBlockPos("geothermal_generator_screen_handler", GeothermalGeneratorScreenHandler::new)
                    .build();

    // ===== ХРАНИЛИЩА ЭНЕРГИИ =====
    public static final ScreenHandlerType<BlockBatteryBoxScreenHandler> BLOCK_BATTERY_BOX_SCREEN_HANDLER =
            ScreenHandlerBuilder.createForBlockPos("block_battery_box_screen_handler", BlockBatteryBoxScreenHandler::new)
                    .build();

    // ===== BUILDER КЛАСС =====
    private static class ScreenHandlerBuilder<T extends ScreenHandler, D> {
        private final String name;
        private final ExtendedScreenHandlerType.ExtendedFactory<T, D> factory;
        private final PacketCodec<? super RegistryByteBuf, D> codec;

        private ScreenHandlerBuilder(String name,
                                     ExtendedScreenHandlerType.ExtendedFactory<T, D> factory,
                                     PacketCodec<? super RegistryByteBuf, D> codec) {
            this.name = name;
            this.factory = factory;
            this.codec = codec;
        }

        /**
         * Создаёт билдер для ScreenHandler с BlockPos данными
         * Это самый распространённый случай для блоков с GUI
         *
         * @param name имя хендлера
         * @param factory фабрика для создания экземпляров
         * @return новый билдер с BlockPos кодеком
         */
        public static <T extends ScreenHandler> ScreenHandlerBuilder<T, BlockPos> createForBlockPos(
                String name,
                ExtendedScreenHandlerType.ExtendedFactory<T, BlockPos> factory) {
            return new ScreenHandlerBuilder<>(name, factory, BlockPos.PACKET_CODEC);
        }

        /**
         * Создаёт билдер для ScreenHandler с кастомным кодеком
         *
         * @param name имя хендлера
         * @param factory фабрика для создания экземпляров
         * @param codec кодек для сериализации данных
         * @return новый билдер с указанным кодеком
         */
        public static <T extends ScreenHandler, D> ScreenHandlerBuilder<T, D> createWithCodec(
                String name,
                ExtendedScreenHandlerType.ExtendedFactory<T, D> factory,
                PacketCodec<? super RegistryByteBuf, D> codec) {
            return new ScreenHandlerBuilder<>(name, factory, codec);
        }

        /**
         * Регистрирует и возвращает ScreenHandlerType
         *
         * @return зарегистрированный ScreenHandlerType
         */
        public ScreenHandlerType<T> build() {
            return Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(IndustrialLogicCraft.MOD_ID, name),
                    new ExtendedScreenHandlerType<>(factory, codec)
            );
        }

        /**
         * Устанавливает кастомное имя (для дебага)
         * @param debugName имя для отладки
         * @return this для цепочки вызовов
         */
        public ScreenHandlerBuilder<T, D> withDebugName(String debugName) {
            // Можно использовать для логирования
            IndustrialLogicCraft.LOGGER.debug("Registering ScreenHandler: " + debugName);
            return this;
        }
    }

    // ===== УПРОЩЁННЫЕ МЕТОДЫ РЕГИСТРАЦИИ =====

    /**
     * Регистрирует ScreenHandler с BlockPos данными
     *
     * @param name имя хендлера
     * @param factory фабрика
     * @return зарегистрированный ScreenHandlerType
     */
    public static <T extends ScreenHandler> ScreenHandlerType<T> register(
            String name,
            ExtendedScreenHandlerType.ExtendedFactory<T, BlockPos> factory) {
        return ScreenHandlerBuilder.createForBlockPos(name, factory).build();
    }

    /**
     * Регистрирует ScreenHandler с кастомным кодеком
     *
     * @param name имя хендлера
     * @param factory фабрика
     * @param codec кодек данных
     * @return зарегистрированный ScreenHandlerType
     */
    public static <T extends ScreenHandler, D> ScreenHandlerType<T> registerWithCodec(
            String name,
            ExtendedScreenHandlerType.ExtendedFactory<T, D> factory,
            PacketCodec<? super RegistryByteBuf, D> codec) {
        return ScreenHandlerBuilder.createWithCodec(name, factory, codec).build();
    }

    // ===== ИНИЦИАЛИЗАЦИЯ =====
    public static void registerScreenHandlers() {
        IndustrialLogicCraft.LOGGER.info("Register Mod Screen Handlers " + IndustrialLogicCraft.MOD_ID);
    }
}