package net.wolf_l1grotale.industriallogiccraft.datagen;

import net.minecraft.block.Block;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилита для упрощённой генерации моделей и блок-стейтов
 */
public class ModelGeneratorHelper {

    // Стандартные повороты
    public static final ModelVariantOperator ROTATE_Y_90 = ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R90);
    public static final ModelVariantOperator ROTATE_Y_180 = ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R180);
    public static final ModelVariantOperator ROTATE_Y_270 = ModelVariantOperator.ROTATION_Y.withValue(AxisRotation.R270);

    private final BlockStateModelGenerator blockGenerator;
    private final ItemModelGenerator itemGenerator;
    private final List<Runnable> tasks = new ArrayList<>();

    public ModelGeneratorHelper(BlockStateModelGenerator blockGenerator, ItemModelGenerator itemGenerator) {
        this.blockGenerator = blockGenerator;
        this.itemGenerator = itemGenerator;
    }

    /**
     * Builder для блоков
     */
    public BlockModelBuilder block(Block block) {
        return new BlockModelBuilder(block);
    }

    /**
     * Builder для предметов
     */
    public ItemModelBuilder item(Item item) {
        return new ItemModelBuilder(item);
    }

    /**
     * Выполняет все зарегистрированные задачи
     */
    public void generate() {
        tasks.forEach(Runnable::run);
    }

    // ===== BLOCK MODEL BUILDER =====

    public class BlockModelBuilder {
        private final Block block;
        private String modelPath;
        private String texturePath;
        private ModelType modelType = ModelType.SIMPLE;

        // Для orientable блоков
        private String frontTexture;
        private String sideTexture;
        private String topTexture;
        private String bottomTexture;
        private String frontActiveTexture;

        // Состояния
        private boolean hasLit = false;
        private boolean hasFacing = false;

        private BlockModelBuilder(Block block) {
            this.block = block;
        }

        /**
         * Простой куб со всеми одинаковыми текстурами
         */
        public BlockModelBuilder simpleCube(String modelPath, String texturePath) {
            this.modelType = ModelType.SIMPLE;
            this.modelPath = modelPath;
            this.texturePath = texturePath;
            return this;
        }

        /**
         * Быстрая настройка простого куба
         */
        public BlockModelBuilder simpleCube(String name) {
            return simpleCube("block/" + name, "block/" + name);
        }

        /**
         * Блок с разными текстурами на сторонах и развание модели
         */
        public BlockModelBuilder orientable(String modelPath) {
            this.modelType = ModelType.ORIENTABLE;
            this.modelPath = modelPath;
            return this;
        }

        /**
         * Устанавливает одну текстуру в круг для orientable блока
         */
        public BlockModelBuilder textures(String textire) {
            this.frontTexture = textire;
            this.sideTexture = textire;
            this.topTexture = textire;
            this.bottomTexture = textire;
            return this;
        }

        /**
         * Устанавливает текстуры для orientable блока
         */
        public BlockModelBuilder textures(String front, String side, String top) {
            this.frontTexture = front;
            this.sideTexture = side;
            this.topTexture = top;
            this.bottomTexture = side; // По умолчанию низ как боковая сторона
            return this;
        }

        /**
         * Устанавливает все текстуры для orientable блока
         */
        public BlockModelBuilder textures(String front, String side, String top, String bottom) {
            this.frontTexture = front;
            this.sideTexture = side;
            this.topTexture = top;
            this.bottomTexture = bottom;
            return this;
        }

        /**
         * Добавляет активную текстуру для фронта (для печей и т.п.)
         */
        public BlockModelBuilder withActiveFront(String frontActive) {
            this.frontActiveTexture = frontActive;
            this.hasLit = true;
            return this;
        }

        /**
         * Блок может вращаться горизонтально
         */
        public BlockModelBuilder withHorizontalFacing() {
            this.hasFacing = true;
            return this;
        }

        /**
         * Блок имеет состояние LIT
         */
        public BlockModelBuilder withLitState() {
            this.hasLit = true;
            return this;
        }

        /**
         * Генератор-подобный блок (facing + lit)
         */
        public BlockModelBuilder generator(String basePath) {
            this.modelType = ModelType.ORIENTABLE;
            this.modelPath = "block/electric/generators/" + basePath + "/model";
            this.frontTexture = "block/electric/generators/" + basePath + "/front";
            this.frontActiveTexture = "block/electric/generators/" + basePath + "/front_active";
            this.sideTexture = "block/electric/generators/" + basePath + "/side";
            this.topTexture = "block/electric/generators/" + basePath + "/top";
            this.bottomTexture = this.sideTexture;
            this.hasFacing = true;
            this.hasLit = true;
            return this;
        }

        /**
         * Машина (facing без lit)
         */
        public BlockModelBuilder machine(String basePath) {
            this.modelType = ModelType.ORIENTABLE;
            this.modelPath = "block/machines/" + basePath + "/model";
            this.frontTexture = "block/machines/" + basePath + "/front";
            this.sideTexture = "block/machines/" + basePath + "/side";
            this.topTexture = "block/machines/" + basePath + "/top";
            this.bottomTexture = this.sideTexture;
            this.hasFacing = true;
            return this;
        }

        /**
         * Регистрирует блок
         */
        public ModelGeneratorHelper register() {
            tasks.add(() -> {
                switch (modelType) {
                    case SIMPLE -> registerSimpleBlock();
                    case ORIENTABLE -> registerOrientableBlock();
                }
            });
            return ModelGeneratorHelper.this;
        }

        private void registerSimpleBlock() {
            Identifier modelId = Models.CUBE_ALL.upload(
                    id(modelPath),
                    TextureMap.all(id(texturePath)),
                    blockGenerator.modelCollector
            );

            blockGenerator.blockStateCollector.accept(
                    BlockStateModelGenerator.createSingletonBlockState(block,
                            BlockStateModelGenerator.createWeightedVariant(modelId))
            );
            blockGenerator.registerParentedItemModel(block, modelId);
        }

        private void registerOrientableBlock() {
            // Создаём обычную модель
            Identifier normalModel = Models.ORIENTABLE.upload(
                    id(modelPath),
                    new TextureMap()
                            .put(TextureKey.FRONT, id(frontTexture))
                            .put(TextureKey.SIDE, id(sideTexture))
                            .put(TextureKey.TOP, id(topTexture)),
                    blockGenerator.modelCollector
            );

            Identifier activeModel = normalModel;

            // Создаём активную модель если нужно
            if (frontActiveTexture != null) {
                activeModel = Models.ORIENTABLE.upload(
                        id(modelPath + "_on"),
                        new TextureMap()
                                .put(TextureKey.FRONT, id(frontActiveTexture))
                                .put(TextureKey.SIDE, id(sideTexture))
                                .put(TextureKey.TOP, id(topTexture)),
                        blockGenerator.modelCollector
                );
            }

            // Генерируем блок-стейт
            if (hasFacing && hasLit) {
                registerFacingLitBlock(normalModel, activeModel);
            } else if (hasFacing) {
                registerFacingBlock(normalModel);
            } else {
                // Простой блок без состояний
                blockGenerator.blockStateCollector.accept(
                        BlockStateModelGenerator.createSingletonBlockState(block,
                                BlockStateModelGenerator.createWeightedVariant(normalModel))
                );
            }

            blockGenerator.registerParentedItemModel(block, normalModel);
        }

        private void registerFacingLitBlock(Identifier normalModel, Identifier activeModel) {
            WeightedVariant normal = BlockStateModelGenerator.createWeightedVariant(normalModel);
            WeightedVariant active = BlockStateModelGenerator.createWeightedVariant(activeModel);

            blockGenerator.blockStateCollector.accept(
                    VariantsBlockModelDefinitionCreator.of(block)
                            .with(
                                    BlockStateVariantMap.models(Properties.HORIZONTAL_FACING, Properties.LIT)
                                            .register(Direction.NORTH, false, normal)
                                            .register(Direction.NORTH, true, active)
                                            .register(Direction.EAST, false, normal.apply(ROTATE_Y_90))
                                            .register(Direction.EAST, true, active.apply(ROTATE_Y_90))
                                            .register(Direction.SOUTH, false, normal.apply(ROTATE_Y_180))
                                            .register(Direction.SOUTH, true, active.apply(ROTATE_Y_180))
                                            .register(Direction.WEST, false, normal.apply(ROTATE_Y_270))
                                            .register(Direction.WEST, true, active.apply(ROTATE_Y_270))
                            )
            );
        }

        private void registerFacingBlock(Identifier model) {
            WeightedVariant variant = BlockStateModelGenerator.createWeightedVariant(model);

            blockGenerator.blockStateCollector.accept(
                    VariantsBlockModelDefinitionCreator.of(block)
                            .with(
                                    BlockStateVariantMap.models(Properties.HORIZONTAL_FACING)
                                            .register(Direction.NORTH, variant)
                                            .register(Direction.EAST, variant.apply(ROTATE_Y_90))
                                            .register(Direction.SOUTH, variant.apply(ROTATE_Y_180))
                                            .register(Direction.WEST, variant.apply(ROTATE_Y_270))
                            )
            );
        }
    }

    // ===== ITEM MODEL BUILDER =====

    public class ItemModelBuilder {
        private final Item item;
        private String modelPath;
        private String texturePath;

        private ItemModelBuilder(Item item) {
            this.item = item;
        }

        /**
         * Простая модель предмета
         */
        public ItemModelBuilder simple(String modelPath, String texturePath) {
            this.modelPath = modelPath;
            this.texturePath = texturePath;
            return this;
        }

        /**
         * Быстрая настройка простого предмета
         */
        public ItemModelBuilder simple(String name) {
            return simple("item/" + name, "item/" + name);
        }

        /**
         * Регистрирует предмет
         */
        public ModelGeneratorHelper register() {
            tasks.add(() -> {
                if (itemGenerator != null) {
                    Identifier modelId = Models.GENERATED.upload(
                            id(modelPath),
                            TextureMap.layer0(id(texturePath)),
                            itemGenerator.modelCollector
                    );
                    itemGenerator.output.accept(item, ItemModels.basic(modelId));
                }
            });
            return ModelGeneratorHelper.this;
        }
    }

    // ===== УТИЛИТЫ =====

    private static Identifier id(String path) {
        return Identifier.of(IndustrialLogicCraft.MOD_ID, path);
    }

    private enum ModelType {
        SIMPLE,
        ORIENTABLE
    }
}