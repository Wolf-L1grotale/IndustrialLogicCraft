package net.wolf_l1grotale.industriallogiccraft.block.electric.generators;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.wolf_l1grotale.industriallogiccraft.block.base.BaseBlockWithEntity;
import net.wolf_l1grotale.industriallogiccraft.block.base.BlockConfiguration;
import org.jetbrains.annotations.Nullable;

public class LVSolarPanelBlock extends BaseBlockWithEntity {
    public static final MapCodec<LVSolarPanelBlock> CODEC =
            LVSolarPanelBlock.createCodec(LVSolarPanelBlock::new);


    protected LVSolarPanelBlock(Settings settings) {
        super(settings, BlockConfiguration.builder()
                .withRotation()        // Может вращаться
                .withLitState()        // Имеет состояние горения
                .withGui()             // Имеет GUI
                .withItemDrops()       // Выбрасывает предметы при разрушении
                .withServerTicker()    // Тикает на сервере
                .build());
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
