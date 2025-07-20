package net.wolf_l1grotale.industriallogiccraft.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.block.ModBlocks;
import net.wolf_l1grotale.industriallogiccraft.item.ModItems;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.Properties;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.client.data.BlockStateVariantMap;
import net.minecraft.client.data.VariantsBlockModelDefinitionCreator;
import net.minecraft.client.data.BlockStateModelGenerator;

import static net.minecraft.client.data.BlockStateModelGenerator.*;

public class ModModelProvider extends FabricModelProvider {

    public static final ModelVariantOperator ROTATE_Y_90 = ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90);
    public static final ModelVariantOperator ROTATE_Y_180 = ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180);
    public static final ModelVariantOperator ROTATE_Y_270 = ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270);

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        registerSimpleBlock(blockStateModelGenerator, ModBlocks.COPPER_ORE_BLOCK, "block/resource/mcopper_ore_block", "block/resource/tcopper_ore_block");
        registerSimpleBlock(blockStateModelGenerator, ModBlocks.MAGIC_BLOCK, "block/util/mmagic_block", "block/util/tmagic_block");

        // Для solid_fuel_generator
        Identifier sfgModel = createOrientableModel(
            blockStateModelGenerator,
            "block/generators/electric/solid_fuel_generator/mgenerator",
            "block/generators/electric/solid_fuel_generator/generator_front",
            "block/generators/electric/solid_fuel_generator/generator_leftrightback",
            "block/generators/electric/solid_fuel_generator/generator_top"
        );
        registerHorizontalFacingBlock(blockStateModelGenerator, ModBlocks.SOLID_FUEL_GENERATOR, sfgModel);

        // Для других подобных блоков просто вызывайте registerHorizontalFacingBlock с нужными параметрами

        registerSimpleBlock(blockStateModelGenerator, ModBlocks.GROWTH_CHAMBER, "block/entity/mgrowth_chamber", "block/entity/tgrowth_chamber");
    }

    private Identifier createOrientableModel(BlockStateModelGenerator generator, String modelPath, String front, String side, String top) {
        return Models.ORIENTABLE.upload(
            Identifier.of(IndustrialLogicCraft.MOD_ID, modelPath),
            new TextureMap()
                .put(TextureKey.FRONT, Identifier.of(IndustrialLogicCraft.MOD_ID, front))
                .put(TextureKey.SIDE, Identifier.of(IndustrialLogicCraft.MOD_ID, side))
                .put(TextureKey.TOP, Identifier.of(IndustrialLogicCraft.MOD_ID, top)),
            generator.modelCollector
        );
    }

    private void registerHorizontalFacingBlock(BlockStateModelGenerator blockStateModelGenerator, Block block, Identifier modelId) {
        WeightedVariant baseVariant = createWeightedVariant(modelId);
        blockStateModelGenerator.blockStateCollector.accept(
            VariantsBlockModelDefinitionCreator.of(block)
                .with(
                    BlockStateVariantMap.models(Properties.HORIZONTAL_FACING)
                        .register(Direction.NORTH, baseVariant)
                        .register(Direction.EAST, baseVariant.apply(ROTATE_Y_90))
                        .register(Direction.SOUTH, baseVariant.apply(ROTATE_Y_180))
                        .register(Direction.WEST, baseVariant.apply(ROTATE_Y_270))
                )
        );
        blockStateModelGenerator.registerParentedItemModel(block, modelId);
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
        final Identifier CN = Models.GENERATED.upload(Identifier.of(IndustrialLogicCraft.MOD_ID, "item/resource/mcopper_nugget"), TextureMap.layer0(Identifier.of(IndustrialLogicCraft.MOD_ID,"item/resource/tcopper_nugget")), itemModelGenerator.modelCollector);
        itemModelGenerator.output.accept(ModItems.COPPER_NUGGET, ItemModels.basic(CN));

        final Identifier CT = Models.GENERATED.upload(Identifier.of(IndustrialLogicCraft.MOD_ID, "item/tools/mchisel"), TextureMap.layer0(Identifier.of(IndustrialLogicCraft.MOD_ID,"item/tools/tchisel")), itemModelGenerator.modelCollector);
        itemModelGenerator.output.accept(ModItems.CHISEL_TOOLS, ItemModels.basic(CT));
    }
}
