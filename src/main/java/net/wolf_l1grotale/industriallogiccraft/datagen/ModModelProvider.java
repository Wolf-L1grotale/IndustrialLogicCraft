package net.wolf_l1grotale.industriallogiccraft.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.*;
import net.minecraft.util.Identifier;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.block.ModBlocks;
import net.wolf_l1grotale.industriallogiccraft.item.ModItems;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        //blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.COPPER_ORE_BLOCK);
        final Identifier COB = Models.CUBE_ALL.upload(Identifier.of(IndustrialLogicCraft.MOD_ID,"block/resource/mcopper_ore_block"), TextureMap.all(Identifier.of(IndustrialLogicCraft.MOD_ID,"block/resource/tcopper_ore_block")), blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.registerParentedItemModel(ModBlocks.COPPER_ORE_BLOCK, COB);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        //itemModelGenerator.register(ModItems.COPPER_NUGGET, Models.GENERATED);
        //itemModelGenerator.register(ModItems.CHISEL_TOOLS, Models.GENERATED);

        final Identifier CN = Models.GENERATED.upload(ModItems.COPPER_NUGGET, TextureMap.layer0(Identifier.of(IndustrialLogicCraft.MOD_ID,"item/resource/tcopper_nugget")), itemModelGenerator.modelCollector);
        itemModelGenerator.output.accept(ModItems.COPPER_NUGGET, ItemModels.basic(CN));

        final Identifier CT = Models.GENERATED.upload(ModItems.CHISEL_TOOLS, TextureMap.layer0(Identifier.of(IndustrialLogicCraft.MOD_ID,"item/tools/tchisel")), itemModelGenerator.modelCollector);
        itemModelGenerator.output.accept(ModItems.CHISEL_TOOLS, ItemModels.basic(CT));
    }
}
