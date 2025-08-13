package net.wolf_l1grotale.industriallogiccraft.block.entity.storage;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ImplementedInventory;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;
import net.wolf_l1grotale.industriallogiccraft.item.battery.BatteryItem;
import net.wolf_l1grotale.industriallogiccraft.screen.electric.BlockBatteryBoxScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BlockBatteryBoxEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    private static final int CHARGING_SLOT = 0;
    private static final int DISCHARGE_SLOT = 1;

    private int energy = 0;
    private static final int MAX_ENERGY = 30000;

    protected final PropertyDelegate propertyDelegate;


    public BlockBatteryBoxEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOCK_BATTERY_BOX_BE, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> BlockBatteryBoxEntity.this.energy;
                    case 1 -> BlockBatteryBoxEntity.this.MAX_ENERGY;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> BlockBatteryBoxEntity.this.energy = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.industriallogiccraft.block_battery_box");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BlockBatteryBoxScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    public void tick(World world, BlockPos pos, BlockState state) {

        if (world.isClient) return;

        // Заряжаем батарейку в выходном слоте
        chargeBattery();


    }

    private void chargeBattery() {
        ItemStack outputStack = getStack(CHARGING_SLOT);
        if (outputStack.getItem() instanceof BatteryItem batteryItem) {
            int currentBatteryEnergy = batteryItem.getEnergy(outputStack);
            int maxBatteryEnergy = batteryItem.getMaxEnergy();

            if (currentBatteryEnergy < maxBatteryEnergy && energy > 0) {
                int transferAmount = Math.min(energy, Math.min(50, maxBatteryEnergy - currentBatteryEnergy));
                int actualTransferred = batteryItem.receiveEnergy(outputStack, transferAmount);
                energy -= actualTransferred;
                markDirty();
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("Energy", energy);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);
        super.readNbt(nbt, registryLookup);
        energy = nbt.getInt("Energy").get();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
