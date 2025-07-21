package net.wolf_l1grotale.industriallogiccraft.api.energy;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wolf_l1grotale.industriallogiccraft.api.energy.tile.IEnergyTile;

import java.util.List;

public interface IEnergyNet {

    //Получает энергетический блок (IEnergyTile) по координатам в мире.
    IEnergyTile getTile(World var1, BlockPos var2);

    //???
    IEnergyTile getSubTile(World var1, BlockPos var2);

    //Возвращает объект GridTile, который содержит основную и подплитку по координатам.
    IEnergyNet.GridTile getTiles(World var1, BlockPos var2);

    //Добавляет энергетическую плитку в сеть (например, когда блок установлен в мире).
    void addTile(IEnergyTile var1);

    //Удаляет плитку из сети (например, когда блок ломается).
    void removeTile(IEnergyTile var1);

    //Обновляет состояние плитки в сети (например, если её параметры изменились).
    void updateTile(IEnergyTile var1);

    //Возвращает количество энергии, соответствующее определённому "уровню" (tier).
    int getPowerFromTier(int var1);

    //Возвращает уровень (tier) по количеству энергии.
    int getTierFromPower(int var1);

    //Возвращает строковое представление уровня (например, "LV", "MV", "HV").
    String getDisplayTier(int var1);

    //Возвращает статистику передачи энергии для плитки.
    TransferStats getStats(IEnergyTile var1);

    //Возвращает список статистики по энергетическим пакетам для плитки.
    List<PacketStats> getPacketStats(IEnergyTile var1);

    //хранит основную и подплитку, а также предоставляет методы для получения их позиции и мира.
    public static class GridTile {

        //возвращает основную плитку
        IEnergyTile mainTile;

        //возвращает подплитку
        IEnergyTile subTile;


        public GridTile(IEnergyTile mainTile, IEnergyTile subTile) {
            this.mainTile = mainTile;
            this.subTile = subTile;
        }

        public IEnergyTile getMainTile() {
            return this.mainTile;
        }

        public IEnergyTile getSubTile() {
            return this.subTile;
        }

        //возвращает позицию подплитки.
        public BlockPos getPos() {
            return this.subTile.getPosition();
        }

        //возвращает мир подплитки.
        public World getWorld() {
            return this.subTile.getWorldObj();
        }
    }
}
