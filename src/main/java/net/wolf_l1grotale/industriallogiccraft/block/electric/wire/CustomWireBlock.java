package net.wolf_l1grotale.industriallogiccraft.block.electric.wire;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.block.entity.wire.CustomWireBlockEntity;

public class CustomWireBlock extends BlockWithEntity {
    // Свойства соединения
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty EAST  = BooleanProperty.of("east");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty WEST  = BooleanProperty.of("west");

    // Тег «redstone_wirings» в namespace вашего мода (пример: mymod)
    private static final TagKey<Block> REDSTONE_WIRINGS =
            TagKey.of(RegistryKeys.BLOCK, Identifier.of(IndustrialLogicCraft.MOD_ID, "redstone_wirings"));


    // Минимальная форма – тонкая линия в центре блока
    private static final VoxelShape SHAPE = createVoxelShape();

    //Конструктор
    public CustomWireBlock(Settings settings) {
        super(settings);
        // Инициализируем состояние по умолчанию (без соединений)
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, false).with(EAST, false)
                .with(SOUTH, false).with(WEST, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CustomWireBlockEntity(pos, state);
    }

    /* ---------- Свойства состояния блока ---------- */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(NORTH, EAST, SOUTH, WEST);

    }

    /* ---------- Определяем форму для коллизий ---------- */
    private static VoxelShape createVoxelShape() {
        // 0.4-0.6 по X и Z, 0-1 по Y (тонкая линия)
        return VoxelShapes.cuboid(0.4D, 0.0D, 0.4D, 0.6D, 1.0D, 0.6D);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state,
                                      BlockView world,
                                      BlockPos pos,
                                      ShapeContext context) {
        return SHAPE;
    }

    /* ---------- Логика соединения с соседними блоками ---------- */
    // При размещении блока считаем, подключен ли он к соседям
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        var world = ctx.getWorld();

        boolean northConnected = isConnected(world, pos.north());
        boolean eastConnected  = isConnected(world, pos.east());
        boolean southConnected = isConnected(world, pos.south());
        boolean westConnected  = isConnected(world, pos.west());

        return this.getDefaultState()
                .with(NORTH, northConnected)
                .with(EAST,  eastConnected)
                .with(SOUTH, southConnected)
                .with(WEST,  westConnected);
    }

    // Проверяем, может ли соседний блок «подключиться» к проводу
    private boolean isConnected(BlockView world, BlockPos pos) {
        return world.getBlockState(pos).isIn(REDSTONE_WIRINGS);
    }


}
