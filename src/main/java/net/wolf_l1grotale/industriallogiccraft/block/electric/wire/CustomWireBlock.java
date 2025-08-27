package net.wolf_l1grotale.industriallogiccraft.block.electric.wire;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.wolf_l1grotale.industriallogiccraft.IndustrialLogicCraft;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;
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

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, ModBlockEntities.CUSTOM_WIRE_BE,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }


}
