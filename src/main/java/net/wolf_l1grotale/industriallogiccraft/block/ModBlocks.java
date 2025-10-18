package net.wolf_l1grotale.industriallogiccraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.block.base.BaseBlockWithEntity;
import net.wolf_l1grotale.industriallogiccraft.block.custom.GrowthChamberBlock;
import net.wolf_l1grotale.industriallogiccraft.block.custom.MagicBlock;
import net.wolf_l1grotale.industriallogiccraft.block.custom.PedestalBlock;
import net.wolf_l1grotale.industriallogiccraft.block.electric.generators.GeothermalGeneratorBlock;
import net.wolf_l1grotale.industriallogiccraft.block.electric.generators.SolidFuelGeneratorBlock;
import net.wolf_l1grotale.industriallogiccraft.block.electric.storage.BlockBatteryBox;
import net.wolf_l1grotale.industriallogiccraft.block.electric.wire.CustomWireBlock;

import java.util.function.Function;
import java.util.function.ToIntFunction;

public class ModBlocks {
    /*
     * Интенсивность свечения блока в Minecraft регулируется значением от 0 до 15:
     * 0 - не светится вообще
     * 1-5 - тусклое свечение
     * 6-10 - среднее свечение
     * 11-14 - яркое свечение
     * 15 - максимальная яркость (как у глоустоуна)
     */

    // ===== РУДЫ =====
    public static final Block COPPER_ORE_BLOCK = BlockBuilder.create("copper_ore_block")
            .strength(3.0f)
            .requiresTool()
            .sounds(BlockSoundGroup.STONE)
            .factory(settings -> new ExperienceDroppingBlock(UniformIntProvider.create(2, 5), settings))
            .build();

    // ===== ПРОВОДА =====
    public static final Block CUSTOM_WIRE_BLOCK = BlockBuilder.create("custom_wire_block")
            .strength(1.0f)
            .requiresTool()
            .sounds(BlockSoundGroup.METAL)
            .factory(CustomWireBlock::new)
            .build();

    // ===== МАГИЧЕСКИЕ БЛОКИ =====
    public static final Block MAGIC_BLOCK = BlockBuilder.create("magic_block")
            .strength(1.0f)
            .requiresTool()
            .factory(MagicBlock::new)
            .build();

    // ===== ДЕКОРАТИВНЫЕ БЛОКИ =====
    public static final Block PEDESTAL = BlockBuilder.create("pedestal")
            .nonOpaque()
            .factory(PedestalBlock::new)
            .build();

    // ===== МАШИНЫ =====
    public static final Block GROWTH_CHAMBER = BlockBuilder.create("growth_chamber")
            .factory(GrowthChamberBlock::new)
            .build();

    public static final Block SOLID_FUEL_GENERATOR = BlockBuilder.create("solid_fuel_generator")
            .strength(4.0f)
            .requiresTool()
            .luminance(state -> state.get(SolidFuelGeneratorBlock.LIT) ? 13 : 0)
            .factory(SolidFuelGeneratorBlock::new)
            .build();

    public static final Block GEOTHERMAL_GENERATOR = BlockBuilder.create("geothermal_generator")
            .strength(4.0f)
            .requiresTool()
            .luminance(13)
            .factory(GeothermalGeneratorBlock::new)
            .build();

    // ===== ХРАНИЛИЩА ЭНЕРГИИ =====
    public static final Block BLOCK_BATTERY_BOX = BlockBuilder.create("block_battery_box")
            .strength(4.0f)
            .requiresTool()
            .luminance(state -> state.get(BlockBatteryBox.LIT) ? 13 : 0)
            .factory(BlockBatteryBox::new)
            .build();

    // ===== BUILDER КЛАСС =====
    private static class BlockBuilder {
        private final String name;
        private AbstractBlock.Settings settings;
        private Function<AbstractBlock.Settings, Block> blockFactory;
        private boolean createItem = true;

        private BlockBuilder(String name) {
            this.name = name;
            this.settings = AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK,
                            Identifier.of(IndustrialLogicCraft.MOD_ID, name)));
            // По умолчанию создаём обычный блок
            this.blockFactory = Block::new;
        }

        public static BlockBuilder create(String name) {
            return new BlockBuilder(name);
        }

        // ===== ОСНОВНЫЕ НАСТРОЙКИ =====
        public BlockBuilder strength(float hardness) {
            this.settings = settings.strength(hardness);
            return this;
        }

        public BlockBuilder strength(float hardness, float resistance) {
            this.settings = settings.strength(hardness, resistance);
            return this;
        }

        public BlockBuilder requiresTool() {
            this.settings = settings.requiresTool();
            return this;
        }

        public BlockBuilder sounds(BlockSoundGroup soundGroup) {
            this.settings = settings.sounds(soundGroup);
            return this;
        }

        // ===== ВИЗУАЛЬНЫЕ НАСТРОЙКИ =====
        public BlockBuilder nonOpaque() {
            this.settings = settings.nonOpaque();
            return this;
        }

        public BlockBuilder luminance(ToIntFunction<BlockState> luminanceProvider) {
            this.settings = settings.luminance(luminanceProvider);
            return this;
        }

        public BlockBuilder luminance(int luminance) {
            this.settings = settings.luminance(state -> luminance);
            return this;
        }

        // ===== ФИЗИЧЕСКИЕ СВОЙСТВА =====
        public BlockBuilder slipperiness(float slipperiness) {
            this.settings = settings.slipperiness(slipperiness);
            return this;
        }

        public BlockBuilder velocityMultiplier(float velocityMultiplier) {
            this.settings = settings.velocityMultiplier(velocityMultiplier);
            return this;
        }

        public BlockBuilder jumpVelocityMultiplier(float jumpVelocityMultiplier) {
            this.settings = settings.jumpVelocityMultiplier(jumpVelocityMultiplier);
            return this;
        }

        // ===== СПЕЦИАЛЬНЫЕ СВОЙСТВА =====
        public BlockBuilder noCollision() {
            this.settings = settings.noCollision();
            return this;
        }

        public BlockBuilder dropsNothing() {
            this.settings = settings.dropsNothing();
            return this;
        }

        public BlockBuilder ticksRandomly() {
            this.settings = settings.ticksRandomly();
            return this;
        }

        public BlockBuilder breakInstantly() {
            this.settings = settings.breakInstantly();
            return this;
        }

        public BlockBuilder noBlockBreakParticles() {
            this.settings = settings.noBlockBreakParticles();
            return this;
        }

        // ===== ФАБРИКА И НАСТРОЙКИ ПРЕДМЕТА =====
        public BlockBuilder factory(Function<AbstractBlock.Settings, Block> factory) {
            this.blockFactory = factory;
            return this;
        }

        public BlockBuilder noItem() {
            this.createItem = false;
            return this;
        }

        // ===== МЕТОД ДЛЯ ПРИМЕНЕНИЯ КАСТОМНЫХ НАСТРОЕК =====
        public BlockBuilder customSettings(Function<AbstractBlock.Settings, AbstractBlock.Settings> customizer) {
            this.settings = customizer.apply(this.settings);
            return this;
        }

        // ===== ФИНАЛЬНАЯ СБОРКА =====
        public Block build() {
            Block block = blockFactory.apply(settings);

            if (createItem) {
                registerBlockItem(name, block);
            }

            return Registry.register(Registries.BLOCK,
                    Identifier.of(IndustrialLogicCraft.MOD_ID, name), block);
        }

        private static void registerBlockItem(String name, Block block) {
            Registry.register(Registries.ITEM,
                    Identifier.of(IndustrialLogicCraft.MOD_ID, name),
                    new BlockItem(block, new Item.Settings()
                            .useBlockPrefixedTranslationKey()
                            .registryKey(RegistryKey.of(RegistryKeys.ITEM,
                                    Identifier.of(IndustrialLogicCraft.MOD_ID, name)))));
        }
    }

    // Метод инициализации
    public static void registerModBlocks() {
        IndustrialLogicCraft.LOGGER.info("Register Mod Blocks " + IndustrialLogicCraft.MOD_ID);
    }
}