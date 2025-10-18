package net.wolf_l1grotale.industriallogiccraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.block.ModBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Регистрация вкладок в креативном инвентаре
 */
public class ModItemGroups {

    // ===== ВКЛАДКА С БЛОКАМИ =====
    public static final ItemGroup INDUSTRIAL_BLOCK_GROUP = ItemGroupBuilder
            .create("industrial_block_group")
            .icon(ModBlocks.COPPER_ORE_BLOCK)
            .displayName("itemgroup.industriallogiccraft.industrial_block_group")
            // Руды
            .add(ModBlocks.COPPER_ORE_BLOCK)
            // Генераторы
            .add(ModBlocks.SOLID_FUEL_GENERATOR)
            //.add(ModBlocks.GEOTHERMAL_GENERATOR)
            // Хранилища энергии
            .add(ModBlocks.BLOCK_BATTERY_BOX)
            // Провода
            .add(ModBlocks.CUSTOM_WIRE_BLOCK)
            // Машины
            .add(ModBlocks.GROWTH_CHAMBER)
            // Магические блоки
            .add(ModBlocks.MAGIC_BLOCK)
            .add(ModBlocks.PEDESTAL)
            .build();

    // ===== ВКЛАДКА С ПРЕДМЕТАМИ =====
    public static final ItemGroup INDUSTRIAL_ITEM_GROUP = ItemGroupBuilder
            .create("industrial_item_group")
            .icon(ModItems.COPPER_NUGGET)
            .displayName("itemgroup.industriallogiccraft.industrial_items_group")
            .add(ModItems.COPPER_NUGGET)
            .add(ModItems.CHISEL_TOOLS)
            .add(ModItems.BATTERY_ITEM)
            .build();

    // ===== BUILDER КЛАСС =====
    private static class ItemGroupBuilder {
        private final String name;
        private Supplier<ItemStack> iconSupplier;
        private Text displayName;
        private final List<ItemConvertible> items = new ArrayList<>();

        private ItemGroupBuilder(String name) {
            this.name = name;
        }

        /**
         * Создаёт новый билдер для вкладки
         * @param name идентификатор вкладки (только lowercase и подчёркивания)
         * @return новый билдер
         */
        public static ItemGroupBuilder create(String name) {
            return new ItemGroupBuilder(name);
        }

        /**
         * Устанавливает иконку вкладки из ItemStack
         * @param stack ItemStack для иконки
         * @return this для цепочки вызовов
         */
        public ItemGroupBuilder icon(ItemStack stack) {
            this.iconSupplier = () -> stack;
            return this;
        }

        /**
         * Устанавливает иконку вкладки из Block
         * @param block блок для иконки
         * @return this для цепочки вызовов
         */
        public ItemGroupBuilder icon(Block block) {
            this.iconSupplier = () -> new ItemStack(block);
            return this;
        }

        /**
         * Устанавливает иконку вкладки из Item
         * @param item предмет для иконки
         * @return this для цепочки вызовов
         */
        public ItemGroupBuilder icon(Item item) {
            this.iconSupplier = () -> new ItemStack(item);
            return this;
        }

        /**
         * Устанавливает иконку вкладки через Supplier
         * @param supplier поставщик ItemStack
         * @return this для цепочки вызовов
         */
        public ItemGroupBuilder icon(Supplier<ItemStack> supplier) {
            this.iconSupplier = supplier;
            return this;
        }

        /**
         * Устанавливает отображаемое имя через ключ перевода
         * @param translationKey ключ для перевода (например "itemgroup.modid.name")
         * @return this для цепочки вызовов
         */
        public ItemGroupBuilder displayName(String translationKey) {
            this.displayName = Text.translatable(translationKey);
            return this;
        }

        /**
         * Устанавливает отображаемое имя через Text объект
         * @param text готовый Text объект
         * @return this для цепочки вызовов
         */
        public ItemGroupBuilder displayName(Text text) {
            this.displayName = text;
            return this;
        }

        /**
         * Добавляет один предмет во вкладку
         * @param item предмет или блок для добавления
         * @return this для цепочки вызовов
         */
        public ItemGroupBuilder add(ItemConvertible item) {
            this.items.add(item);
            return this;
        }

        /**
         * Добавляет несколько предметов во вкладку
         * @param items предметы/блоки для добавления
         * @return this для цепочки вызовов
         */
        public ItemGroupBuilder addAll(ItemConvertible... items) {
            for (ItemConvertible item : items) {
                this.items.add(item);
            }
            return this;
        }

        /**
         * Добавляет список предметов
         * @param items список предметов/блоков
         * @return this для цепочки вызовов
         */
        public ItemGroupBuilder addAll(List<? extends ItemConvertible> items) {
            this.items.addAll(items);
            return this;
        }

        /**
         * Добавляет разделитель (пустой слот) для визуального разделения
         * @return this для цепочки вызовов
         */
        public ItemGroupBuilder addSeparator() {
            // В Minecraft нет встроенных разделителей, но можно добавить комментарий
            // или реализовать через пустые слоты если нужно
            return this;
        }

        /**
         * Финализирует создание и регистрирует вкладку
         * @return зарегистрированная ItemGroup
         */
        public ItemGroup build() {
            if (iconSupplier == null) {
                throw new IllegalStateException("Icon must be set for ItemGroup: " + name);
            }
            if (displayName == null) {
                // Если имя не задано, используем стандартное
                displayName = Text.translatable("itemgroup." + IndustrialLogicCraft.MOD_ID + "." + name);
            }

            Identifier id = Identifier.of(IndustrialLogicCraft.MOD_ID, name);
            RegistryKey<ItemGroup> key = RegistryKey.of(RegistryKeys.ITEM_GROUP, id);

            return Registry.register(
                    Registries.ITEM_GROUP,
                    id,
                    FabricItemGroup.builder()
                            .icon(iconSupplier)
                            .displayName(displayName)
                            .entries((displayContext, entries) -> {
                                for (ItemConvertible item : items) {
                                    entries.add(item);
                                }
                            })
                            .build()
            );
        }
    }

    // ===== МЕТОД ИНИЦИАЛИЗАЦИИ =====
    public static void registerItemGroups() {
        IndustrialLogicCraft.LOGGER.info("Register Mod Item Groups " + IndustrialLogicCraft.MOD_ID);
    }
}