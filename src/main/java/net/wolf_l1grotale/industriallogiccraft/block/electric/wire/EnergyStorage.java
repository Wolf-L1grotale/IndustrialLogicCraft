package net.wolf_l1grotale.industriallogiccraft.block.electric.wire;

public interface EnergyStorage {
    int getCapacity();
    int getAmount();
    void setAmount(int amount);
    int receiveEnergy(int maxReceive, boolean simulate);
    int extractEnergy(int maxExtract, boolean simulate);
    long insert(long amount);
    long extract(long amount);

    boolean supportsExtraction();
}
