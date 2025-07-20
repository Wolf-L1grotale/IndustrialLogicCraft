package net.wolf_l1grotale.industriallogiccraft.api.energy;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wolf_l1grotale.industriallogiccraft.api.energy.tile.IEnergyTile;

public interface IEnergyNet {

    //Получает энергетический блок (IEnergyTile) по координатам в мире.
    IEnergyTile getTile(World var1, BlockPos var2);
}
