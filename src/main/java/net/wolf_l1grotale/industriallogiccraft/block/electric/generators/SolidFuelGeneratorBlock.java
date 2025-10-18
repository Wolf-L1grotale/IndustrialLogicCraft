package net.wolf_l1grotale.industriallogiccraft.block.electric.generators;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wolf_l1grotale.industriallogiccraft.block.base.BaseBlockWithEntity;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;
import net.wolf_l1grotale.industriallogiccraft.block.entity.generators.SolidFuelGeneratorBlockEntity;
import org.jetbrains.annotations.Nullable;

public class SolidFuelGeneratorBlock extends BaseBlockWithEntity {

    public static final MapCodec<SolidFuelGeneratorBlock> CODEC =
            SolidFuelGeneratorBlock.createCodec(SolidFuelGeneratorBlock::new);

    public SolidFuelGeneratorBlock(Settings settings) {
        super(settings);
    }

    // ===== КОНФИГУРАЦИЯ =====

    @Override
    protected boolean hasRotation() {
        return true;
    }

    @Override
    protected boolean hasLitState() {
        return true;
    }

    @Override
    protected boolean hasGui() {
        return true;
    }

    @Override
    protected boolean shouldDropItems() {
        return true;
    }

    // ===== БЛОК ENTITY =====

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SolidFuelGeneratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    protected <T extends BlockEntity> BlockEntityTicker<T> createServerTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.SOLID_FUEL_GENERATOR_BE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}