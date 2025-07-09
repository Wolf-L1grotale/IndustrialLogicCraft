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

import java.util.function.BiFunction;
import java.util.function.Function;

public class ModBlocks {


    public static final Block COPPER_ORE_BLOCK = registerBlockItem("copper_ore_block",
            ExperienceDroppingBlock::new,
            UniformIntProvider.create(2, 5),
            AbstractBlock.Settings.create()
                    .strength(3f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE));

    public static final Block SOLID_FUEL_GENERATOR = registerBlockItem("solid_fuel_generator",
            ExperienceDroppingBlock::new,
            UniformIntProvider.create(2, 5),
            AbstractBlock.Settings.create()
                    .strength(3f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.METAL));

    private static Block registerBlockItem(String name, BiFunction<UniformIntProvider, AbstractBlock.Settings, Block> blockFactory, UniformIntProvider experience, AbstractBlock.Settings settings) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        Block block = Registry.register(Registries.BLOCK, blockKey, blockFactory.apply(experience, settings.registryKey(blockKey)));
        RegistryKey<Item> itemKey = keyOfItem(name);
        BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, blockItem);
        return block;
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(IndustrialLogicCraft.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(IndustrialLogicCraft.MOD_ID, name));
    }

    public static void registerModBlocks() {

        IndustrialLogicCraft.LOGGER.info("Register Mod Blocks " + IndustrialLogicCraft.MOD_ID);
    }
}
