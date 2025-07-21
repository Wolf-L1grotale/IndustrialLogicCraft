package net.wolf_l1grotale.industriallogiccraft.api.energy;


//класс для хранения статистики передачи энергии в энергетической системе

public class TransferStats {

    //сколько энергии поступило (вошло) в блок/устройство.
    final long energyIn;

    //сколько энергии вышло из блока/устройства.
    final long energyOut;

    //сколько энергии потеряно при входе (например, из-за сопротивления проводов).
    final long energyLossIn;

    //сколько энергии потеряно при выходе.
    final long energyLossOut;

    //В конструктор идут все поля описанные выше
    public TransferStats(long energyIn, long energyOut, long energyLossIn, long energyLossOut) {
        this.energyIn = energyIn;
        this.energyOut = energyOut;
        this.energyLossIn = energyLossIn;
        this.energyLossOut = energyLossOut;
    }

    //Все методы просто возвращают соответствующее поле.

    //возвращает количество вошедшей энергии.
    public long getEnergyIn() {
        return this.energyIn;
    }

    //возвращает количество вышедшей энергии.
    public long getEnergyOut() {
        return this.energyOut;
    }

    //возвращает потери энергии на входе.
    public long getEnergyLossIn() {
        return this.energyLossIn;
    }

    //возвращает потери энергии на выходе.
    public long getEnergyLossOut() {
        return this.energyLossOut;
    }


    //Переопределённый метод, который возвращает строку с краткой сводкой по всем значениям
    // TransferStats[In=1000, Out=800, LossIn=50, LossOut=30]
    public String toString() {
        return "TransferStats[In=" + this.energyIn + ", Out=" + this.energyOut + ", LossIn=" + this.energyLossIn + ", LossOut=" + this.energyLossOut + "]";
    }
}
