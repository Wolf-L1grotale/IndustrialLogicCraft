package net.wolf_l1grotale.industriallogiccraft.block.base;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

/**
 * Базовый класс для всех блоков с BlockEntity
 * Использует методы вместо конфигурации для избежания проблем с инициализацией
 */
public abstract class BaseBlockWithEntity extends BlockWithEntity implements BlockEntityProvider {

    // ===== СВОЙСТВА =====
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty POWERED = Properties.POWERED;

    protected BaseBlockWithEntity(Settings settings) {
        super(settings);

        // Инициализируем состояние по умолчанию
        BlockState defaultState = this.getStateManager().getDefaultState();

        if (hasRotation() && defaultState.contains(FACING)) {
            defaultState = defaultState.with(FACING, Direction.NORTH);
        }
        if (hasLitState() && defaultState.contains(LIT)) {
            defaultState = defaultState.with(LIT, false);
        }
        if (hasPoweredState() && defaultState.contains(POWERED)) {
            defaultState = defaultState.with(POWERED, false);
        }

        this.setDefaultState(defaultState);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        if (hasRotation()) {
            builder.add(FACING);
        }
        if (hasLitState()) {
            builder.add(LIT);
        }
        if (hasPoweredState()) {
            builder.add(POWERED);
        }

        appendCustomProperties(builder);
    }

    // ===== МЕТОДЫ КОНФИГУРАЦИИ (переопределите в наследниках) =====

    /**
     * @return true если блок может вращаться
     */
    protected boolean hasRotation() {
        return false;
    }

    /**
     * @return true если блок может вращаться вертикально (вверх/вниз)
     */
    protected boolean allowVerticalRotation() {
        return false;
    }

    /**
     * @return true если блок имеет состояние горения (LIT)
     */
    protected boolean hasLitState() {
        return false;
    }

    /**
     * @return true если блок имеет состояние питания (POWERED)
     */
    protected boolean hasPoweredState() {
        return false;
    }

    /**
     * @return true если блок имеет GUI
     */
    protected boolean hasGui() {
        return false;
    }

    /**
     * @return true если блок выбрасывает предметы при разрушении
     */
    protected boolean shouldDropItems() {
        return false;
    }

    /**
     * @return true если блок сохраняет кастомное имя
     */
    protected boolean shouldKeepCustomName() {
        return false;
    }

    /**
     * Переопределите для добавления кастомных свойств
     */
    protected void appendCustomProperties(StateManager.Builder<Block, BlockState> builder) {
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    // ===== РАЗМЕЩЕНИЕ БЛОКА =====
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState state = this.getDefaultState();

        if (hasRotation() && state.contains(FACING)) {
            Direction facing;
            if (allowVerticalRotation()) {
                facing = context.getPlayerLookDirection().getOpposite();
            } else {
                facing = context.getHorizontalPlayerFacing().getOpposite();
                if (facing.getAxis().isVertical()) {
                    facing = Direction.NORTH;
                }
            }
            state = state.with(FACING, facing);
        }

        return modifyPlacementState(state, context);
    }

    /**
     * Переопределите для модификации состояния при размещении
     */
    protected BlockState modifyPlacementState(BlockState state, ItemPlacementContext context) {
        return state;
    }

    // ===== ВЗАИМОДЕЙСТВИЕ =====
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!hasGui()) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    // Для обратной совместимости с onUseWithItem
    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                         PlayerEntity player, Hand hand, BlockHitResult hit) {
        return onUse(state, world, pos, player, hit);
    }

    // ===== ВЫБРОС ПРЕДМЕТОВ =====
    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!state.isOf(world.getBlockState(pos).getBlock())) {
            if (shouldDropItems()) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof InventoryProvider inventoryProvider) {
                    ItemScatterer.spawn(world, pos, inventoryProvider.getInventory(state, world, pos));
                    world.updateComparators(pos, this);
                }
            }
        }
        super.onStateReplaced(state, world, pos, moved);
    }

    // ===== РАЗМЕЩЕНИЕ С ДАННЫМИ =====
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (shouldKeepCustomName() && itemStack.contains(net.minecraft.component.DataComponentTypes.CUSTOM_NAME)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CustomNameable customNameable) {
                Text customName = itemStack.get(net.minecraft.component.DataComponentTypes.CUSTOM_NAME);
                if (customName != null) {
                    customNameable.setCustomName(customName);
                }
            }
        }
    }

    // ===== TICKER =====
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            return createClientTicker(world, state, type);
        } else {
            return createServerTicker(world, state, type);
        }
    }

    /**
     * Создаёт ticker для клиентской стороны
     */
    @Nullable
    protected <T extends BlockEntity> BlockEntityTicker<T> createClientTicker(World world, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    /**
     * Создаёт ticker для серверной стороны
     */
    @Nullable
    protected <T extends BlockEntity> BlockEntityTicker<T> createServerTicker(World world, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    // ===== ИНТЕРФЕЙСЫ =====

    /**
     * Интерфейс для блоков с инвентарём
     */
    public interface InventoryProvider {
        net.minecraft.inventory.Inventory getInventory(BlockState state, WorldAccess world, BlockPos pos);
    }

    /**
     * Интерфейс для блоков с кастомным именем
     */
    public interface CustomNameable {
        void setCustomName(Text name);
        @Nullable
        Text getCustomName();
    }
}