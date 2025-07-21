package net.wolf_l1grotale.industriallogiccraft.api.energy;

import net.wolf_l1grotale.industriallogiccraft.api.energy.tile.IEnergyTile;

//вспомогательный класс для хранения статистики по "энергетическим пакетам" (energy packets), которые проходят через энергетическую плитку (energy tile) в системе передачи энергии
public class PacketStats {

    //ссылка на энергетическую плитку (блок/устройство), для которой собирается статистика.
    final IEnergyTile tile;

    //количество энергетических пакетов, прошедших через эту плитку за определённый период.
    final long packets;

    //общее количество энергии, переданное этими пакетами.
    final long power;

    //принимает ли эта плитка энергию (true), или только отдаёт (false).
    final boolean accepting;

    //В конструктор идут все поля описанные выше
    public PacketStats(IEnergyTile tile, long packets, long power, boolean accepting) {
        this.tile = tile;
        this.packets = packets;
        this.power = power;
        this.accepting = accepting;
    }

    //Все методы просто возвращают соответствующее поле.

    //возвращает плитку, для которой собирается статистика.
    public IEnergyTile getTile() {
        return this.tile;
    }

    //возвращает количество пакетов.
    public long getPackets() {
        return this.packets;
    }

    //возвращает общее количество энергии.
    public long getPower() {
        return this.power;
    }

    //возвращает, принимает ли плитка энергию.
    public boolean isAccepting() {
        return this.accepting;
    }
}
