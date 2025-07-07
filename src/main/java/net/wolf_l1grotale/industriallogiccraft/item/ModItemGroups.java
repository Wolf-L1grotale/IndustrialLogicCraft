package net.wolf_l1grotale.industriallogiccraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.block.ModBlocks;

public class ModItemGroups {

    public static final ItemGroup INDUSTRIAL_BLOCK_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(IndustrialLogicCraft.MOD_ID, "pink_garnet_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.COPPER_ORE_BLOCK))
                    .displayName(Text.translatable("itemgroup.industriallogiccraft.industrial_block_group"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.COPPER_ORE_BLOCK);
                    }).build());

    public static void registerItemGroups() {
        IndustrialLogicCraft.LOGGER.info("Register Mod Groups Item " + IndustrialLogicCraft.MOD_ID);
    }
}
