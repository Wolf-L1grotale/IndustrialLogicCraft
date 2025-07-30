package net.wolf_l1grotale.industriallogiccraft.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.item.Item;
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

        Identifier sfgModel1 = createOrientableModel(
                blockStateModelGenerator,
                "block/generators/electric/solid_fuel_generator/mgenerator_on",
                "block/generators/electric/solid_fuel_generator/generator_front_active",
                "block/generators/electric/solid_fuel_generator/generator_leftrightback",
                "block/generators/electric/solid_fuel_generator/generator_top"
        );

        registerHorizontalFacingBlock(blockStateModelGenerator, ModBlocks.SOLID_FUEL_GENERATOR, sfgModel, sfgModel1);

        // Для других подобных блоков просто вызывайте registerHorizontalFacingBlock с нужными параметрами

        registerSimpleBlock(blockStateModelGenerator, ModBlocks.GROWTH_CHAMBER, "block/entity/mgrowth_chamber", "block/entity/tgrowth_chamber");
    }

    //Генерация моделей предметов
    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        /* Пример добавления нового Item
        *
        * registerSimpleItemModel(
        * itemModelGenerator,
        * "Указать Item",
        * "Путь до модели",
        * "Путь до текступы");
        *
        * */

        registerSimpleItemModel(itemModelGenerator, ModItems.COPPER_NUGGET, "item/resource/mcopper_nugget", "item/resource/tcopper_nugget");
        registerSimpleItemModel(itemModelGenerator, ModItems.CHISEL_TOOLS, "item/tools/mchisel", "item/tools/tchisel");
        registerSimpleItemModel(itemModelGenerator, ModItems.BATTERY_ITEM, "item/battery/mbattery_item", "item/battery/tbattery_item");
    }

    // Прописываем пути к текстурам на каждой стороне и кастомный путь до модели
    private Identifier createOrientableModel(BlockStateModelGenerator generator, String modelPath, String front, String side, String top, String front_on) {

        // Создаём обычную модель
        Identifier normalModel = Models.ORIENTABLE.upload(
                Identifier.of(IndustrialLogicCraft.MOD_ID, modelPath),
                new TextureMap()
                        .put(TextureKey.FRONT, Identifier.of(IndustrialLogicCraft.MOD_ID, front))
                        .put(TextureKey.SIDE, Identifier.of(IndustrialLogicCraft.MOD_ID, side))
                        .put(TextureKey.TOP, Identifier.of(IndustrialLogicCraft.MOD_ID, top)),
                generator.modelCollector
        );

        // Если front_on не задан — возвращаем только обычную модель
        if (front_on == null) {
            return normalModel;
        }

        // Создаём активную модель
        Models.ORIENTABLE.upload(
                Identifier.of(IndustrialLogicCraft.MOD_ID, modelPath + "_on"),
                new TextureMap()
                        .put(TextureKey.FRONT, Identifier.of(IndustrialLogicCraft.MOD_ID, front_on))
                        .put(TextureKey.SIDE, Identifier.of(IndustrialLogicCraft.MOD_ID, side))
                        .put(TextureKey.TOP, Identifier.of(IndustrialLogicCraft.MOD_ID, top)),
                generator.modelCollector
        );

        return normalModel; // возвращаем обычную модель как основную
    }

    // Перегрузка для одного фронта (front_on == null)
    private Identifier createOrientableModel(BlockStateModelGenerator generator, String modelPath, String front, String side, String top) {
        return createOrientableModel(generator, modelPath, front, side, top, null);
    }

    //Генерируем blockState на основе кастомной модели
    private void registerHorizontalFacingBlock(BlockStateModelGenerator blockStateModelGenerator, Block block, Identifier modelId, Identifier modelId2) {
        WeightedVariant baseVariant = createWeightedVariant(modelId);
        WeightedVariant baseVariantOn = modelId2 != null ? createWeightedVariant(modelId2) : baseVariant;

        blockStateModelGenerator.blockStateCollector.accept(
            VariantsBlockModelDefinitionCreator.of(block)
                .with(
                    BlockStateVariantMap.models(Properties.HORIZONTAL_FACING, Properties.LIT)
                        .register(Direction.NORTH, false, baseVariant)
                        .register(Direction.NORTH, true, baseVariantOn)
                        .register(Direction.EAST,false, baseVariant.apply(ROTATE_Y_90))
                        .register(Direction.EAST,true, baseVariantOn.apply(ROTATE_Y_90))
                        .register(Direction.SOUTH,false, baseVariant.apply(ROTATE_Y_180))
                        .register(Direction.SOUTH,true, baseVariantOn.apply(ROTATE_Y_180))
                        .register(Direction.WEST,false, baseVariant.apply(ROTATE_Y_270))
                        .register(Direction.WEST,true, baseVariantOn.apply(ROTATE_Y_270))
                )
        );
        blockStateModelGenerator.registerParentedItemModel(block, modelId);
    }

    // Перегрузка для одного modelId (активная и неактивная одинаковые)
    private void registerHorizontalFacingBlock(BlockStateModelGenerator blockStateModelGenerator, Block block, Identifier modelId) {
        registerHorizontalFacingBlock(blockStateModelGenerator, block, modelId, null);
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

    private void registerSimpleItemModel(
            ItemModelGenerator itemModelGenerator,
            Item item,
            String modelPath,
            String texturePath
    ) {
        final Identifier modelId = Models.GENERATED.upload(
            Identifier.of(IndustrialLogicCraft.MOD_ID, modelPath),
            TextureMap.layer0(Identifier.of(IndustrialLogicCraft.MOD_ID, texturePath)),
            itemModelGenerator.modelCollector
        );
        itemModelGenerator.output.accept(item, ItemModels.basic(modelId));
    }
}
