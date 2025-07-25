package net.wolf_l1grotale.industriallogiccraft;

import net.fabricmc.api.ModInitializer;

import net.wolf_l1grotale.industriallogiccraft.block.ModBlocks;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;
import net.wolf_l1grotale.industriallogiccraft.item.ModItems;
import net.wolf_l1grotale.industriallogiccraft.item.ModItemGroups;
import net.wolf_l1grotale.industriallogiccraft.recipe.ModRecipes;
import net.wolf_l1grotale.industriallogiccraft.screen.ModScreenHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndustrialLogicCraft implements ModInitializer {
	public static final String MOD_ID = "industriallogiccraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();

		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();

		ModRecipes.registerRecipes();


		ModItemGroups.registerItemGroups();
	}
}