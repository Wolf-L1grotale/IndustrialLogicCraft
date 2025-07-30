package net.wolf_l1grotale.industriallogiccraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.item.battery.BatteryItem;
import net.wolf_l1grotale.industriallogiccraft.item.custom.ChiselItem;

import java.util.function.Function;

public class ModItems {
    public static final Item COPPER_NUGGET = registerItem("copper_nugget", Item::new, new Item.Settings());
    public static final Item CHISEL_TOOLS = registerItem("chisel_tools", ChiselItem::new, new Item.Settings().maxDamage(32));
    public static final Item BATTERY_ITEM = registerItem("battery_item", BatteryItem::new, new Item.Settings());

    public static Item registerItem(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(IndustrialLogicCraft.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static void  registerModItems(){
        IndustrialLogicCraft.LOGGER.info("Register Mod Items" + IndustrialLogicCraft.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries ->{
            entries.add(COPPER_NUGGET);
        });
    }
}
