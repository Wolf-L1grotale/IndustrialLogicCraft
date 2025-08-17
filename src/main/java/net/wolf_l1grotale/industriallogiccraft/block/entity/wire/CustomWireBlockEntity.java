package net.wolf_l1grotale.industriallogiccraft.block.entity.wire;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.wolf_l1grotale.industriallogiccraft.block.electric.wire.CustomWireBlock;
import net.wolf_l1grotale.industriallogiccraft.block.electric.wire.EnergyStorage;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;

import java.util.Map;


public class CustomWireBlockEntity extends BlockEntity implements EnergyStorage {
    // Параметры провода (можно менять)
    public static final int CAPACITY = 1000;      // максимальная энергия в проводе
    public static final int MAX_TRANSFER = 50;    // сколько можно передать за тик

    private int energy;

    private static final Map<Direction, BooleanProperty> DIR_TO_PROP = Map.of(
            Direction.NORTH, CustomWireBlock.NORTH,
            Direction.EAST , CustomWireBlock.EAST ,
            Direction.SOUTH, CustomWireBlock.SOUTH,
            Direction.WEST , CustomWireBlock.WEST
    );

    public CustomWireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CUSTOM_WIRE_BE, pos, state);
    }


    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public int getAmount() {
        return energy;
    }

    @Override
    public void setAmount(int amount) {
        this.energy = Math.max(0, Math.min(amount, CAPACITY));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0 || isFull()) return 0;
        int received = Math.min(maxReceive, CAPACITY - energy);
        if (!simulate) energy += received;
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0 || isEmpty()) return 0;
        int extracted = Math.min(maxExtract, energy);
        if (!simulate) energy -= extracted;
        return extracted;
    }

    @Override
    public long insert(long amount) {
        return 0;
    }

    @Override
    public long extract(long amount) {
        return 0;
    }

    @Override
    public boolean supportsExtraction() {
        return false;
    }

    /* ---------- Тик‑логика провода ---------- */
    public void tick(World world, BlockPos pos, BlockState state) {
        // 1. Сохраняем энергию от соседей (если они тоже проводы)
        for (Direction dir : Direction.values()) {
            if (!hasConnection(state, dir.getOpposite())) continue;   // если нет соединения – пропускаем
            BlockEntity neighborBE = world.getBlockEntity(pos.offset(dir));
            if (!(neighborBE instanceof CustomWireBlockEntity)) continue;
            CustomWireBlockEntity neighbor = (CustomWireBlockEntity) neighborBE;

            int toTransfer = Math.min(MAX_TRANSFER, Math.min(neighbor.energy, this.CAPACITY - this.energy));
            if (toTransfer > 0) {
                // реальный перенос
                neighbor.extractEnergy(toTransfer, false);
                this.receiveEnergy(toTransfer, false);
            }
        }

        // 2. Можно добавить логику генерации/поглощения энергии здесь.
    }

    /* ---------- Утилиты ---------- */
    private boolean isFull()   { return energy >= CAPACITY; }
    private boolean isEmpty()  { return energy <= 0;     }
    private boolean hasConnection(BlockState state, Direction dir) {
        BooleanProperty prop = DIR_TO_PROP.get(dir);
        return prop != null && state.get(prop);
    }

}
