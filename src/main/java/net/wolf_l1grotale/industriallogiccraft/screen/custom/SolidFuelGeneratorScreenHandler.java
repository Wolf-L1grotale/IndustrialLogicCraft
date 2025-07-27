package net.wolf_l1grotale.industriallogiccraft.screen.custom;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.wolf_l1grotale.industriallogiccraft.block.entity.custom.SolidFuelGeneratorBlockEntity;
import net.wolf_l1grotale.industriallogiccraft.screen.ModScreenHandlers;

public class SolidFuelGeneratorScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final SolidFuelGeneratorBlockEntity blockEntity;

    //Конструктор
    public SolidFuelGeneratorScreenHandler(int syncId, PlayerInventory inventory, BlockPos pos) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(pos), new ArrayPropertyDelegate(6));
    }

    public SolidFuelGeneratorScreenHandler(int syncId, PlayerInventory playerInventory,
                                      BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.SOLID_FUEL_GENERATOR_SCREEN_HANDLER, syncId);
        this.inventory = ((Inventory) blockEntity);
        this.blockEntity = ((SolidFuelGeneratorBlockEntity) blockEntity);
        this.propertyDelegate = arrayPropertyDelegate;


        //Указывается самый левый верхний пиксель начала слота
        this.addSlot(new Slot(inventory, 0, 65, 53));
        this.addSlot(new Slot(inventory, 1, 65, 17));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(arrayPropertyDelegate);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public boolean isCrafting() {
        return propertyDelegate.get(0) > 0;
    }

    public int getScaledArrowProgress() {
        /*
         * int progress = this.propertyDelegate.get(0);
        int maxProgress = this.propertyDelegate.get(1); // Max Progress
        int arrowPixelSize = 24; // This is the width in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
         */
        int energy = this.propertyDelegate.get(2);
        int maxEnergy = this.propertyDelegate.get(3);
        int arrowWidth = 25; // ширина текстуры стрелки в пикселях

        if (maxEnergy == 0) return 0;
        return (int) ((float) energy / maxEnergy * arrowWidth);
    }

    public boolean isBurning() {
        return blockEntity.isBurning();
    }

    //Как правило, не изменны. Место расположение пользовательского интерфейса
    // инверторя
    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    //место расположение пользовательского хотбара (1..0)
    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    //Сведения о энергии
    public float getEnergyProgress() {
        int energy = this.propertyDelegate.get(2);
        int maxEnergy = this.propertyDelegate.get(3);

        if (maxEnergy == 0) return 0;
        return (float) energy / maxEnergy;
    }

    public int getScaledFuelProgress() {
        int burnTime = propertyDelegate.get(4); // Индекс из PropertyDelegate для burnTime
        int fuelTime = propertyDelegate.get(5); // Индекс из PropertyDelegate для fuelTime

        if (fuelTime == 0 || burnTime == 0) return 0;

        // Возвращает значение от 0 до 14 (высота индикатора)
        return burnTime * 14 / fuelTime;
    }
}
