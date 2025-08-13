package net.wolf_l1grotale.industriallogiccraft.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.block.ModBlocks;
import net.wolf_l1grotale.industriallogiccraft.block.entity.custom.GrowthChamberBlockEntity;
import net.wolf_l1grotale.industriallogiccraft.block.entity.custom.PedestalBlockEntity;
import net.wolf_l1grotale.industriallogiccraft.block.entity.generators.SolidFuelGeneratorBlockEntity;
import net.wolf_l1grotale.industriallogiccraft.block.entity.storage.BlockBatteryBoxEntity;

public class ModBlockEntities {
    public static final BlockEntityType<PedestalBlockEntity> PEDESTAL_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(IndustrialLogicCraft.MOD_ID, "pedestal_be"),
                    FabricBlockEntityTypeBuilder.create(PedestalBlockEntity::new, new Block[]{ModBlocks.PEDESTAL}).build());

    public static final BlockEntityType<GrowthChamberBlockEntity> GROWTH_CHAMBER_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(IndustrialLogicCraft.MOD_ID, "growth_chamber_be"),
                    FabricBlockEntityTypeBuilder.create(GrowthChamberBlockEntity::new, new Block[]{ModBlocks.GROWTH_CHAMBER}).build());

    public static final BlockEntityType<SolidFuelGeneratorBlockEntity> SOLID_FUEL_GENERATOR_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(IndustrialLogicCraft.MOD_ID, "solid_fuel_generator_be"),
                    FabricBlockEntityTypeBuilder.create(SolidFuelGeneratorBlockEntity::new, new Block[]{ModBlocks.SOLID_FUEL_GENERATOR}).build());

    public static final BlockEntityType<BlockBatteryBoxEntity> BLOCK_BATTERY_BOX_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(IndustrialLogicCraft.MOD_ID, "block_battery_box_be"),
                    FabricBlockEntityTypeBuilder.create(BlockBatteryBoxEntity::new, new Block[]{ModBlocks.BLOCK_BATTERY_BOX}).build());

    public static void registerBlockEntities() {
        IndustrialLogicCraft.LOGGER.info("Register Mod Entitys " + IndustrialLogicCraft.MOD_ID);
    }
}
