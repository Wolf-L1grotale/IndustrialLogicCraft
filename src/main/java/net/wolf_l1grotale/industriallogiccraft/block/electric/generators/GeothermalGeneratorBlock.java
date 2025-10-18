package net.wolf_l1grotale.industriallogiccraft.block.electric.generators;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.wolf_l1grotale.industriallogiccraft.block.base.BaseBlockWithEntity;
import org.jetbrains.annotations.Nullable;

public class GeothermalGeneratorBlock extends BaseBlockWithEntity {

    public static final IntProperty PROGRESS = IntProperty.of("progress", 0, 100);

    public GeothermalGeneratorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
