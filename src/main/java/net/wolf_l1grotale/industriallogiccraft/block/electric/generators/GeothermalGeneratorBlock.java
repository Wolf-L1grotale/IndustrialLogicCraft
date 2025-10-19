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
import net.wolf_l1grotale.industriallogiccraft.block.entity.generators.GeothermalGeneratorBlockEntity;
import org.jetbrains.annotations.Nullable;

public class GeothermalGeneratorBlock extends BaseBlockWithEntity {

    public static final MapCodec<GeothermalGeneratorBlock> CODEC =
            GeothermalGeneratorBlock.createCodec(GeothermalGeneratorBlock::new);

    public GeothermalGeneratorBlock(Settings settings) {
        super(settings);
    }

    // ===== КОНФИГУРАЦИЯ БЛОКА =====

    @Override
    protected boolean hasRotation() {
        return true; // ВАЖНО! Это добавляет свойство FACING
    }

    @Override
    protected boolean hasLitState() {
        return true; // Добавляет свойство LIT
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
        return new GeothermalGeneratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    protected <T extends BlockEntity> BlockEntityTicker<T> createServerTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.GEOTHERMAL_GENERATOR_BE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}