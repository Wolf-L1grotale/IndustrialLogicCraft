package net.wolf_l1grotale.industriallogiccraft.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.wolf_l1grotale.industriallogiccraft.block.ModBlocks;
import net.wolf_l1grotale.industriallogiccraft.item.ModItems;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        ModelGeneratorHelper helper = new ModelGeneratorHelper(blockStateModelGenerator, null);

        // ===== ПРОСТЫЕ БЛОКИ (РУДЫ И РЕСУРСЫ) =====
        helper.block(ModBlocks.COPPER_ORE_BLOCK)
                .simpleCube("block/resource/mcopper_ore_block", "block/resource/tcopper_ore_block")
                .register();

        helper.block(ModBlocks.CUSTOM_WIRE_BLOCK)
                .simpleCube("block/resource/mcustom_wire_block", "block/resource/tcustom_wire_block")
                .register();

        helper.block(ModBlocks.MAGIC_BLOCK)
                .simpleCube("block/util/mmagic_block", "block/util/tmagic_block")
                .register();

        helper.block(ModBlocks.GROWTH_CHAMBER)
                .simpleCube("block/entity/mgrowth_chamber", "block/entity/tgrowth_chamber")
                .register();

        // ===== ГЕНЕРАТОРЫ С СОСТОЯНИЯМИ =====
        helper.block(ModBlocks.SOLID_FUEL_GENERATOR)
                .orientable("block/electric/generators/solid_fuel_generator/mgenerator")
                .textures(
                        "block/electric/generators/solid_fuel_generator/generator_front",
                        "block/electric/generators/solid_fuel_generator/generator_leftrightback",
                        "block/electric/generators/solid_fuel_generator/generator_top"
                )
                .withActiveFront("block/electric/generators/solid_fuel_generator/generator_front_active")
                .withHorizontalFacing()
                .withLitState()
                .register();

        helper.block(ModBlocks.GEOTHERMAL_GENERATOR)
                .orientable("block/electric/generators/geothermal_generator/mgenerator")
                .textures("block/template")
                .withHorizontalFacing()
                .withLitState().register();

        // ===== БАТАРЕЙНЫЕ БЛОКИ =====
        helper.block(ModBlocks.BLOCK_BATTERY_BOX)
                .orientable("block/electric/storage/block_battery_box/mbbb")
                .textures(
                        "block/electric/storage/lv/tblockbat_side",
                        "block/electric/storage/lv/tblockbat_down",
                        "block/electric/storage/lv/tblockbat_top_l"
                )
                .withActiveFront("block/electric/storage/lv/tblockbat_side") // Если есть активная текстура
                .withHorizontalFacing()
                .withLitState()
                .register();

        // Если у вас есть другие блоки, добавьте их здесь:
        /*
        helper.block(ModBlocks.GEOTHERMAL_GENERATOR)
            .generator("geothermal_generator")
            .register();
        */

        // Выполняем все зарегистрированные задачи
        helper.generate();
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        ModelGeneratorHelper helper = new ModelGeneratorHelper(null, itemModelGenerator);

        // ===== РЕСУРСЫ =====
        helper.item(ModItems.COPPER_NUGGET)
                .simple("item/resource/mcopper_nugget", "item/resource/tcopper_nugget")
                .register();

        // ===== ИНСТРУМЕНТЫ =====
        helper.item(ModItems.CHISEL_TOOLS)
                .simple("item/tools/mchisel", "item/tools/tchisel")
                .register();

        // ===== БАТАРЕИ =====
        helper.item(ModItems.BATTERY_ITEM)
                .simple("item/battery/mbattery_item", "item/battery/tbattery_item_0")
                .register();

        // Выполняем все зарегистрированные задачи
        helper.generate();
    }
}