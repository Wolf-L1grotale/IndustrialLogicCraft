package net.wolf_l1grotale.industriallogiccraft.api.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;



public interface ILocation {
    World getWorldObj();
    BlockPos getPosition();
}
