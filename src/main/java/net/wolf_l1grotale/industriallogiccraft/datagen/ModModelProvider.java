package net.wolf_l1grotale.industriallogiccraft.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.client.data.*;
import net.minecraft.util.Identifier;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.block.ModBlocks;
import net.wolf_l1grotale.industriallogiccraft.item.ModItems;

import static net.minecraft.client.data.BlockStateModelGenerator.createSingletonBlockState;
import static net.minecraft.client.data.BlockStateModelGenerator.createWeightedVariant;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        registerSimpleBlock(blockStateModelGenerator, ModBlocks.COPPER_ORE_BLOCK, "block/resource/mcopper_ore_block", "block/resource/tcopper_ore_block");
        registerSimpleBlock(blockStateModelGenerator, ModBlocks.MAGIC_BLOCK, "block/util/mmagic_block", "block/util/tmagic_block");

        final Identifier SFG = Models.ORIENTABLE.upload(
            Identifier.of(IndustrialLogicCraft.MOD_ID, "block/generators/electric/solid_fuel_generator/mgenerator"),
            new TextureMap()
                .put(TextureKey.FRONT, Identifier.of(IndustrialLogicCraft.MOD_ID, "block/generators/electric/solid_fuel_generator/generator_front"))
                .put(TextureKey.SIDE, Identifier.of(IndustrialLogicCraft.MOD_ID, "block/generators/electric/solid_fuel_generator/generator_leftrightback"))
                .put(TextureKey.TOP, Identifier.of(IndustrialLogicCraft.MOD_ID, "block/generators/electric/solid_fuel_generator/generator_top")),
            blockStateModelGenerator.modelCollector
        );

        blockStateModelGenerator.blockStateCollector.accept(
            createSingletonBlockState(ModBlocks.SOLID_FUEL_GENERATOR, createWeightedVariant(SFG))
        );
        blockStateModelGenerator.registerParentedItemModel(ModBlocks.SOLID_FUEL_GENERATOR, SFG);
    }

    private void registerSimpleBlock(BlockStateModelGenerator generator, Block block, String modelPath, String texturePath) {
        final Identifier identifier = Models.CUBE_ALL.upload(
            Identifier.of(IndustrialLogicCraft.MOD_ID, modelPath),
            TextureMap.all(Identifier.of(IndustrialLogicCraft.MOD_ID, texturePath)),
            generator.modelCollector
        );
        generator.blockStateCollector.accept(createSingletonBlockState(block, createWeightedVariant(identifier)));
        generator.registerParentedItemModel(block, identifier);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        final Identifier CN = Models.GENERATED.upload(ModItems.COPPER_NUGGET, TextureMap.layer0(Identifier.of(IndustrialLogicCraft.MOD_ID,"item/resource/tcopper_nugget")), itemModelGenerator.modelCollector);
        itemModelGenerator.output.accept(ModItems.COPPER_NUGGET, ItemModels.basic(CN));

        final Identifier CT = Models.GENERATED.upload(ModItems.CHISEL_TOOLS, TextureMap.layer0(Identifier.of(IndustrialLogicCraft.MOD_ID,"item/tools/tchisel")), itemModelGenerator.modelCollector);
        itemModelGenerator.output.accept(ModItems.CHISEL_TOOLS, ItemModels.basic(CT));
    }
}
