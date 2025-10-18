package net.wolf_l1grotale.industriallogiccraft.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.block.ModBlocks;
import net.wolf_l1grotale.industriallogiccraft.block.entity.custom.GrowthChamberBlockEntity;
import net.wolf_l1grotale.industriallogiccraft.block.entity.custom.PedestalBlockEntity;
import net.wolf_l1grotale.industriallogiccraft.block.entity.generators.GeothermalGeneratorBlockEntity;
import net.wolf_l1grotale.industriallogiccraft.block.entity.generators.SolidFuelGeneratorBlockEntity;
import net.wolf_l1grotale.industriallogiccraft.block.entity.storage.BlockBatteryBoxEntity;
import net.wolf_l1grotale.industriallogiccraft.block.entity.wire.CustomWireBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class ModBlockEntities {

    // ===== ДЕКОРАТИВНЫЕ БЛОКИ =====
    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL_BE =
            BlockEntityBuilder.create("pedestal_be", PedestalBlockEntity::new)
                    .addBlock(ModBlocks.PEDESTAL)
                    .build();

    // ===== МАШИНЫ =====
    public static final BlockEntityType<GrowthChamberBlockEntity> GROWTH_CHAMBER_BE =
            BlockEntityBuilder.create("growth_chamber_be", GrowthChamberBlockEntity::new)
                    .addBlock(ModBlocks.GROWTH_CHAMBER)
                    .build();

    // ===== ГЕНЕРАТОРЫ =====
    public static final BlockEntityType<SolidFuelGeneratorBlockEntity> SOLID_FUEL_GENERATOR_BE =
            BlockEntityBuilder.create("solid_fuel_generator_be", SolidFuelGeneratorBlockEntity::new)
                    .addBlock(ModBlocks.SOLID_FUEL_GENERATOR)
                    .build();

    /*public static final BlockEntityType<GeothermalGeneratorBlockEntity> GEOTHERMAL_GENERATOR_BE =
            BlockEntityBuilder.create("geothermal_generator_be", GeothermalGeneratorBlockEntity::new)
                    .addBlock(ModBlocks.GEOTHERMAL_GENERATOR)
                    .build();*/

    // ===== ХРАНИЛИЩА ЭНЕРГИИ =====
    public static final BlockEntityType<BlockBatteryBoxEntity> BLOCK_BATTERY_BOX_BE =
            BlockEntityBuilder.create("block_battery_box_be", BlockBatteryBoxEntity::new)
                    .addBlock(ModBlocks.BLOCK_BATTERY_BOX)
                    .build();

    // ===== ПРОВОДА =====
    public static final BlockEntityType<CustomWireBlockEntity> CUSTOM_WIRE_BE =
            BlockEntityBuilder.create("custom_wire_be", CustomWireBlockEntity::new)
                    .addBlock(ModBlocks.CUSTOM_WIRE_BLOCK)
                    .build();

    // ===== BUILDER КЛАСС =====
    private static class BlockEntityBuilder<T extends BlockEntity> {
        private final String name;
        private final FabricBlockEntityTypeBuilder.Factory<T> factory;
        private final List<Block> blocks = new ArrayList<>();

        private BlockEntityBuilder(String name, FabricBlockEntityTypeBuilder.Factory<T> factory) {
            this.name = name;
            this.factory = factory;
        }

        /**
         * Создаёт новый билдер для BlockEntity
         * @param name имя BlockEntity (будет использоваться в идентификаторе)
         * @param factory фабрика для создания экземпляров BlockEntity
         */
        public static <T extends BlockEntity> BlockEntityBuilder<T> create(String name,
                                                                           FabricBlockEntityTypeBuilder.Factory<T> factory) {
            return new BlockEntityBuilder<>(name, factory);
        }

        /**
         * Создаёт билдер с автоматическим именем на основе класса BlockEntity
         * @param factory фабрика для создания экземпляров BlockEntity
         */
        public static <T extends BlockEntity> BlockEntityBuilder<T> create(
                FabricBlockEntityTypeBuilder.Factory<T> factory) {
            // Автоматически генерируем имя из класса
            String className = factory.getClass().getSimpleName()
                    .replace("BlockEntity", "")
                    .replaceAll("([A-Z])", "_$1")
                    .toLowerCase()
                    .substring(1) + "_be";
            return new BlockEntityBuilder<>(className, factory);
        }

        /**
         * Добавляет блок, который будет использовать этот BlockEntity
         * @param block блок для связи с BlockEntity
         */
        public BlockEntityBuilder<T> addBlock(Block block) {
            this.blocks.add(block);
            return this;
        }

        /**
         * Добавляет несколько блоков, которые будут использовать этот BlockEntity
         * @param blocks массив блоков для связи с BlockEntity
         */
        public BlockEntityBuilder<T> addBlocks(Block... blocks) {
            for (Block block : blocks) {
                this.blocks.add(block);
            }
            return this;
        }

        /**
         * Финализирует создание и регистрирует BlockEntity
         */
        public BlockEntityType<T> build() {
            if (blocks.isEmpty()) {
                throw new IllegalStateException("BlockEntity must have at least one associated block!");
            }

            Block[] blockArray = blocks.toArray(new Block[0]);
            BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder
                    .create(factory, blockArray)
                    .build();

            return Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(IndustrialLogicCraft.MOD_ID, name),
                    blockEntityType
            );
        }
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ СОЗДАНИЯ =====

    /**
     * Упрощённый метод для создания BlockEntity с одним блоком
     */
    public static <T extends BlockEntity> BlockEntityType<T> register(String name,
                                                                      FabricBlockEntityTypeBuilder.Factory<T> factory, Block block) {
        return BlockEntityBuilder.create(name, factory)
                .addBlock(block)
                .build();
    }

    /**
     * Упрощённый метод для создания BlockEntity с несколькими блоками
     */
    public static <T extends BlockEntity> BlockEntityType<T> register(String name,
                                                                      FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        return BlockEntityBuilder.create(name, factory)
                .addBlocks(blocks)
                .build();
    }

    // Метод инициализации
    public static void registerBlockEntities() {
        IndustrialLogicCraft.LOGGER.info("Register Mod Block Entities " + IndustrialLogicCraft.MOD_ID);
    }
}