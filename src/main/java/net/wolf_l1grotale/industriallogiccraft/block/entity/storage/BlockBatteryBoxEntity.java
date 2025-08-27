package net.wolf_l1grotale.industriallogiccraft.block.entity.storage;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.wolf_l1grotale.industriallogiccraft.block.electric.wire.CustomWireBlock;
import net.wolf_l1grotale.industriallogiccraft.block.electric.wire.EnergyStorage;
import net.wolf_l1grotale.industriallogiccraft.block.entity.wire.CustomWireBlockEntity;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ImplementedInventory;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;
import net.wolf_l1grotale.industriallogiccraft.item.battery.BatteryItem;
import net.wolf_l1grotale.industriallogiccraft.screen.electric.BlockBatteryBoxScreenHandler;
import org.jetbrains.annotations.Nullable;

public class BlockBatteryBoxEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory, EnergyStorage {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    private static final int CHARGING_SLOT = 0;
    private static final int DISCHARGE_SLOT = 1;
    private static final int PER_SIDE_PULL = 50; //лимит за тик с одной стороны

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

        int oldEnergy = energy; // Запоминаем старое значение

        //System.out.println("BatteryBox at " + pos + " ticking, energy: " + energy + "/" + MAX_ENERGY);

        // Получаем энергию от соседей (включая провода)
        pullEnergyFromNeighbors(world, pos);

        // Заряжаем батарейку
        chargeBattery();

        if (oldEnergy != energy) {
            syncEnergyToClient();
        }
    }

    private void pullEnergyFromNeighbors(World world, BlockPos pos) {
        if (world.isClient) return;
        if (isFull()) return;

        //System.out.println("BatteryBox trying to pull energy from neighbors...");

        for (Direction dir : Direction.values()) {
            BlockEntity be = world.getBlockEntity(pos.offset(dir));
            if (be == null) continue;

            //System.out.println("Found neighbor: " + be.getClass().getSimpleName() + " at " + pos.offset(dir));

            // 1) Универсальный путь: любой сосед, который умеет энергию и разрешает извлечение
            if (be instanceof EnergyStorage storage && storage.supportsExtraction()) {
                //System.out.println("Neighbor supports extraction, has " + storage.getAmount() + " energy");

                int freeSpace = MAX_ENERGY - energy;
                int available = storage.getAmount();
                int toPull = Math.min(PER_SIDE_PULL, Math.min(freeSpace, available));

                //System.out.println("Trying to pull " + toPull + " energy");

                if (toPull > 0) {
                    long extracted = storage.extract(toPull); // у соседнего BE убираем энергию
                    if (extracted > 0) {
                        this.insert(extracted);               // в себя добавляем (учтёт кап и markDirty)
                        //System.out.println("Successfully pulled " + extracted + " energy");
                        if (isFull()) break;                  // можно выйти раньше, если заполнились
                    }
                }
                continue;
            }

            // 2) Опционально: «наследие» для проводов, если они НЕ реализуют EnergyStorage
            if (be instanceof CustomWireBlockEntity wireBE) {
                int freeSpace = MAX_ENERGY - energy;
                int toReceive = Math.min(PER_SIDE_PULL, Math.min(freeSpace, wireBE.getAmount()));
                if (toReceive > 0) {
                    int extracted = wireBE.extractEnergy(toReceive, false);
                    if (extracted > 0) {
                        energy += extracted;
                        markDirty();
                        if (isFull()) break;
                    }
                }
            }
        }
    }

    private boolean hasConnection(World world, BlockPos pos, Direction dir) {
        BlockEntity be = world.getBlockEntity(pos.offset(dir));
        return be instanceof CustomWireBlockEntity;   // проверяем наличие BE, а не только блока
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

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0 || isFull()) return 0;
        int toInsert = Math.min(maxReceive, MAX_ENERGY - energy);
        if (!simulate) {
            energy += toInsert;
            markDirty();
            syncEnergyToClient();
            System.out.println("BatteryBox принял " + toInsert + " энергии. Теперь внутри: " + energy);
        }
        return toInsert;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    private boolean isFull() { return energy >= MAX_ENERGY; }

    @Override
    public long insert(long amount) {
        if (amount <= 0 || isFull()) return 0;
        long toInsert = Math.min(amount, MAX_ENERGY - energy);
        energy += toInsert;
        markDirty();
        syncEnergyToClient();
        return toInsert;
    }

    @Override
    public long extract(long amount) {
        // Нет необходимости в симуляции – сразу выполняем извлечение.
        if (amount <= 0 || energy == 0) return 0;

        // Сколько реально можно вытащить, не опустев при этом слоты.
        long toExtract = Math.min(amount, energy);

        // Убираем энергию и отмечаем изменение.
        energy -= toExtract;
        markDirty();

        return toExtract;      // реальное количество извлечённой энергии
    }
    @Override public int getAmount() { return energy; }

    @Override
    public void setAmount(int amount) {

    }

    @Override public int getCapacity() { return MAX_ENERGY; }
    @Override
    public boolean supportsExtraction() { return false; }

    @Override
    public boolean supportsInsertion() {
        return true;
    }

    private void syncEnergyToClient() {
        if (world != null && !world.isClient) {
            // Принудительно отправляем пакет обновления
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);

            // Также можно добавить прямую отправку пакета
            if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                var packet = toUpdatePacket();
                if (packet != null) {
                    serverWorld.getChunkManager().markForUpdate(pos);
                }
            }
        }
    }


}
