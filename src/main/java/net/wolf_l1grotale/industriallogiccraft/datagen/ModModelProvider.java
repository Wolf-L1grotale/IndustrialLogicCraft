package net.wolf_l1grotale.industriallogiccraft.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
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
        final Identifier COB = Models.CUBE_ALL.upload(Identifier.of(IndustrialLogicCraft.MOD_ID, "block/resource/mcopper_ore_block"),
                TextureMap.all(Identifier.of(IndustrialLogicCraft.MOD_ID, "block/resource/tcopper_ore_block")), blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(createSingletonBlockState(ModBlocks.COPPER_ORE_BLOCK, createWeightedVariant(COB)));
        blockStateModelGenerator.registerParentedItemModel(ModBlocks.COPPER_ORE_BLOCK, COB);

        final Identifier SFG = Models.CUBE.upload(
            Identifier.of(IndustrialLogicCraft.MOD_ID, "block/machine/solid_fuel_generator"),
            new TextureMap()
                .put(TextureKey.PARTICLE, Identifier.of(IndustrialLogicCraft.MOD_ID, "block/machine/generator_front"))
                .put(TextureKey.UP, Identifier.of(IndustrialLogicCraft.MOD_ID, "block/machine/generator_top"))
                .put(TextureKey.DOWN, Identifier.of(IndustrialLogicCraft.MOD_ID, "block/machine/generator_bottom"))
                .put(TextureKey.NORTH, Identifier.of(IndustrialLogicCraft.MOD_ID, "block/machine/generator_front"))
                .put(TextureKey.SOUTH, Identifier.of(IndustrialLogicCraft.MOD_ID, "block/machine/generator_leftrightback"))
                .put(TextureKey.EAST, Identifier.of(IndustrialLogicCraft.MOD_ID, "block/machine/generator_leftrightback"))
                .put(TextureKey.WEST, Identifier.of(IndustrialLogicCraft.MOD_ID, "block/machine/generator_leftrightback")),
            blockStateModelGenerator.modelCollector
        );

        blockStateModelGenerator.blockStateCollector.accept(
            createSingletonBlockState(ModBlocks.SOLID_FUEL_GENERATOR, createWeightedVariant(SFG))
        );
        blockStateModelGenerator.registerParentedItemModel(ModBlocks.SOLID_FUEL_GENERATOR, SFG);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        final Identifier CN = Models.GENERATED.upload(ModItems.COPPER_NUGGET, TextureMap.layer0(Identifier.of(IndustrialLogicCraft.MOD_ID,"item/resource/tcopper_nugget")), itemModelGenerator.modelCollector);
        itemModelGenerator.output.accept(ModItems.COPPER_NUGGET, ItemModels.basic(CN));

        final Identifier CT = Models.GENERATED.upload(ModItems.CHISEL_TOOLS, TextureMap.layer0(Identifier.of(IndustrialLogicCraft.MOD_ID,"item/tools/tchisel")), itemModelGenerator.modelCollector);
        itemModelGenerator.output.accept(ModItems.CHISEL_TOOLS, ItemModels.basic(CT));
    }
}
