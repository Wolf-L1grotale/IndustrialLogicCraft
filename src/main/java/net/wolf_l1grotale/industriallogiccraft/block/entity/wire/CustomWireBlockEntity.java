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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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
        return receiveEnergy((int) Math.min(amount, Integer.MAX_VALUE), false);
    }

    @Override
    public long extract(long amount) {
        return extractEnergy((int) Math.min(amount, Integer.MAX_VALUE), false);
    }

    @Override
    public boolean supportsExtraction() {
        return true;
    }

    @Override
    public boolean supportsInsertion() {
        return true;  // Провод должен и принимать, и отдавать энергию
    }

    /* ---------- Тик‑логика провода ---------- */
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        Set<BlockPos> extractedFrom = new HashSet<>();

        // --- Подсасываем энергию ---
        for (Direction dir : Direction.values()) {
            if (isFull()) break;

            // Проверяем, можем ли взаимодействовать в этом направлении
            if (!canInteract(world, pos, state, dir)) continue;

            BlockPos neighborPos = pos.offset(dir);
            BlockEntity be = world.getBlockEntity(neighborPos);
            if (!(be instanceof EnergyStorage source)) continue;
            if (!source.supportsExtraction()) continue;

            int free = CAPACITY - energy;
            int toExtract = Math.min(MAX_TRANSFER, Math.min(free, source.getAmount()));
            if (toExtract > 0) {
                int extracted = source.extractEnergy(toExtract, false);
                if (extracted > 0) {
                    energy += extracted;
                    extractedFrom.add(neighborPos);
                    markDirty();
                }
            }
        }

        // --- Отдаём энергию ---
        if (energy > 0) {
            for (Direction dir : Direction.values()) {
                if (energy <= 0) break;

                // Проверяем, можем ли взаимодействовать в этом направлении
                if (!canInteract(world, pos, state, dir)) continue;

                BlockPos neighborPos = pos.offset(dir);
                if (extractedFrom.contains(neighborPos)) continue;

                BlockEntity be = world.getBlockEntity(neighborPos);
                if (!(be instanceof EnergyStorage sink)) continue;
                if (!sink.supportsInsertion()) continue;

                int toSend = Math.min(MAX_TRANSFER, energy);
                int accepted = sink.receiveEnergy(toSend, true);

                if (accepted > 0) {
                    int sent = sink.receiveEnergy(accepted, false);
                    energy -= sent;
                    if (sent > 0) {
                        markDirty();
                        //System.out.println("Wire at " + pos + " sent " + sent + " energy to " + neighborPos);
                    }
                }
            }
        }
    }

    private boolean canInteract(World world, BlockPos pos, BlockState state, Direction dir) {
        // Если это провод — проверяем property
        if (state.getBlock() instanceof CustomWireBlock && hasConnection(state, dir)) {
            return true;
        }
        // Если сосед EnergyStorage — разрешаем
        BlockEntity be = world.getBlockEntity(pos.offset(dir));
        return be instanceof EnergyStorage;
    }

    /* ---------- Утилиты ---------- */
    private boolean isFull()   { return energy >= CAPACITY; }
    private boolean isEmpty()  { return energy <= 0;     }
    private boolean hasConnection(BlockState state, Direction dir) {
        BooleanProperty prop = DIR_TO_PROP.get(dir);
        return prop != null && state.get(prop);
    }

}
