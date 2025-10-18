package net.wolf_l1grotale.industriallogiccraft;

import net.fabricmc.api.ClientModInitializer;
import net.wolf_l1grotale.industriallogiccraft.client.ClientRegistry;
import net.wolf_l1grotale.industriallogiccraft.client.ModScreens;
import net.wolf_l1grotale.industriallogiccraft.client.ModBlockEntityRenderers;

public class IndustrialLogicCraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Регистрируем все экраны
        ModScreens.register();

        // Регистрируем все рендереры
        ModBlockEntityRenderers.register();

        // Инициализируем всё
        ClientRegistry.initialize();
    }
}