package net.wolf_l1grotale.industriallogiccraft.item.battery;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class BatteryItem extends Item {
    public static final int MAX_ENERGY = 10000;

    public BatteryItem(Settings settings) {
        super(settings.maxDamage(MAX_ENERGY));
    }

    public int getEnergy(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) {
            return 0;
        }
        return nbtComponent.copyNbt().getInt("Energy").orElse(0);
    }

    public void setEnergy(ItemStack stack, int energy) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        nbt.putInt("Energy", Math.min(energy, MAX_ENERGY));
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        
        // Обновляем полоску прочности (инвертируем: больше энергии = меньше урона)
        stack.setDamage(MAX_ENERGY - Math.min(energy, MAX_ENERGY));
    }

    public int getMaxEnergy() {
        return MAX_ENERGY;
    }

    public int receiveEnergy(ItemStack stack, int amount) {
        int energy = getEnergy(stack);
        int energyReceived = Math.min(amount, MAX_ENERGY - energy);
        setEnergy(stack, energy + energyReceived);
        return energyReceived;
    }

    public int extractEnergy(ItemStack stack, int amount) {
        int energy = getEnergy(stack);
        int energyExtracted = Math.min(amount, energy);
        setEnergy(stack, energy - energyExtracted);
        return energyExtracted;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return getEnergy(stack) > 0;
    }

    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        int energy = getEnergy(stack);
        double percentage = (double) energy / MAX_ENERGY * 100;
        
        tooltip.add(Text.literal(String.format("Energy: %d / %d (%.1f%%)", energy, MAX_ENERGY, percentage))
                .formatted(energy > 0 ? Formatting.GREEN : Formatting.RED));
    }
}