package net.wolf_l1grotale.industriallogiccraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
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
import net.wolf_l1grotale.industriallogiccraft.block.custom.GrowthChamberBlock;
import net.wolf_l1grotale.industriallogiccraft.block.custom.MagicBlock;
import net.wolf_l1grotale.industriallogiccraft.block.custom.PedestalBlock;

import java.util.function.Function;

public class ModBlocks {


    public static final Block COPPER_ORE_BLOCK = registerBlock("copper_ore_block",
            properties -> new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
                    properties.strength(3.0f).requiresTool().sounds(BlockSoundGroup.STONE)));




    public static final Block SOLID_FUEL_GENERATOR = registerBlock("solid_fuel_generator",
            properties -> new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
                    properties.strength(3.0f).requiresTool().sounds(BlockSoundGroup.METAL)));

    public static final Block MAGIC_BLOCK = registerBlock("magic_block",
            properties -> new MagicBlock(properties.strength(1f).requiresTool()));

    public static final Block PEDESTAL = registerBlock("pedestal",
            properties -> new PedestalBlock(properties.nonOpaque()));

    public static final Block GROWTH_CHAMBER = registerBlock("growth_chamber", GrowthChamberBlock::new);


    //Этот метод регистрирует блок вместе с соответствующим предметом | Основной метод
    private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> function) {
        Block toRegister = function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IndustrialLogicCraft.MOD_ID, name))));
        registerBlockItem(name, toRegister);
        return Registry.register(Registries.BLOCK, Identifier.of(IndustrialLogicCraft.MOD_ID, name), toRegister);
    }

    //Похож на первый метод, но регистрирует только блок без создания предмета для него | Технический, вдруг пригодится
    private static Block registerBlockWithoutBlockItem(String name, Function<AbstractBlock.Settings, Block> function){
        return Registry.register(Registries.BLOCK, Identifier.of(IndustrialLogicCraft.MOD_ID, name),
                function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IndustrialLogicCraft.MOD_ID, name)))));
    }

    //Этот вспомогательный метод создаёт и регистрирует предмет для блока
    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(IndustrialLogicCraft.MOD_ID, name),
                new BlockItem(block, new Item.Settings().useBlockPrefixedTranslationKey()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(IndustrialLogicCraft.MOD_ID, name)))));
    }

    // Что-то типо main функции
    public static void registerModBlocks() {

        IndustrialLogicCraft.LOGGER.info("Register Mod Blocks " + IndustrialLogicCraft.MOD_ID);
    }
}
