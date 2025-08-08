package net.wolf_l1grotale.industriallogiccraft.block.entity.generators;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wolf_l1grotale.industriallogiccraft.block.electric.generators.SolidFuelGeneratorBlock;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ImplementedInventory;
import net.wolf_l1grotale.industriallogiccraft.block.entity.ModBlockEntities;
import net.wolf_l1grotale.industriallogiccraft.recipe.GrowthChamberRecipe;
import net.wolf_l1grotale.industriallogiccraft.recipe.GrowthChamberRecipeInput;
import net.wolf_l1grotale.industriallogiccraft.recipe.ModRecipes;
import net.wolf_l1grotale.industriallogiccraft.screen.custom.SolidFuelGeneratorScreenHandler;
import net.wolf_l1grotale.industriallogiccraft.item.battery.BatteryItem;
import org.jetbrains.annotations.Nullable;
import net.minecraft.item.FuelRegistry;

import java.util.Optional;

public class SolidFuelGeneratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private int energy = 0;
    private static final int MAX_ENERGY = 10000;
    private int burnTime = 0; // сколько тиков еще будет гореть текущее топливо
    private int fuelTime = 0; // сколько всего тиков горит текущее топливо

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 100;

    private boolean isActuallyBurning = false; // фактическое сжигание топлива




    public SolidFuelGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLID_FUEL_GENERATOR_BE, pos, state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SolidFuelGeneratorBlockEntity.this.progress;
                    case 1 -> SolidFuelGeneratorBlockEntity.this.maxProgress;
                    case 2 -> SolidFuelGeneratorBlockEntity.this.energy;
                    case 3 -> SolidFuelGeneratorBlockEntity.this.MAX_ENERGY;
                    case 4 -> SolidFuelGeneratorBlockEntity.this.burnTime;
                    case 5 -> SolidFuelGeneratorBlockEntity.this.fuelTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SolidFuelGeneratorBlockEntity.this.progress = value;
                    case 1 -> SolidFuelGeneratorBlockEntity.this.maxProgress = value;
                    case 2 -> SolidFuelGeneratorBlockEntity.this.energy = value;
                    case 4 -> SolidFuelGeneratorBlockEntity.this.burnTime = value;
                    case 5 -> SolidFuelGeneratorBlockEntity.this.fuelTime = value;
                }
            }

            @Override
            public int size() {
                return 6;
            }
        };
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.industriallogiccraft.solid_fuel_generator");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SolidFuelGeneratorScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 72;
    }

    private void craftItem() {
        Optional<RecipeEntry<GrowthChamberRecipe>> recipe = getCurrentRecipe();

        ItemStack output = recipe.get().value().output();
        this.removeStack(INPUT_SLOT, 1); // Жестко определено количество входных ресурсов
        this.setStack(OUTPUT_SLOT, new ItemStack(output.getItem(),
                this.getStack(OUTPUT_SLOT).getCount() + output.getCount()));
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        this.progress++;
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<GrowthChamberRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()) {
            return false;
        }

        ItemStack output = recipe.get().value().output();
        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
    }

    private Optional<RecipeEntry<GrowthChamberRecipe>> getCurrentRecipe() {
        return ((ServerWorld) this.getWorld()).getRecipeManager()
                .getFirstMatch(ModRecipes.GROWTH_CHAMBER_TYPE, new GrowthChamberRecipeInput(inventory.get(INPUT_SLOT)), this.getWorld());
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = this.getStack(OUTPUT_SLOT).isEmpty() ? 64 : this.getStack(OUTPUT_SLOT).getMaxCount();
        int currentCount = this.getStack(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        /*if(hasRecipe()) {
            increaseCraftingProgress();
            markDirty(world, pos, state);

            if(hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }*/

        if (world.isClient) return;

        isActuallyBurning = false;

        boolean isGeneratingEnergy = false;

        // Если есть горючее и есть место для энергии - продолжаем работу
        if (burnTime > 0 && energy < MAX_ENERGY) {
            burnTime--;
            energy += 10;
            if (energy > MAX_ENERGY) energy = MAX_ENERGY;
            isGeneratingEnergy = true; // Только здесь блок должен светиться
            markDirty();
        }
        else if (burnTime > 0 && energy >= MAX_ENERGY) {
            // Топливо не расходуется если хранилище полное
        }
        else if (energy < MAX_ENERGY) {
            // Загрузка нового топлива
            ItemStack fuelStack = getStack(0);
            if (!fuelStack.isEmpty()) {
                int fuelValue = getFuelTime(world.getFuelRegistry(), fuelStack);
                if (fuelValue > 0) {
                    burnTime = fuelValue;
                    fuelTime = fuelValue;
                    fuelStack.decrement(1);
                    // Не включаем свечение здесь, только на следующем тике
                    markDirty();
                }
            }
        }

        // Заряжаем батарейку в выходном слоте
        chargeBattery();
        
        // Обновляем состояние блока, если оно изменилось
        boolean currentLit = state.get(SolidFuelGeneratorBlock.LIT);
        if (currentLit != isGeneratingEnergy) {
            // Принудительно обновляем состояние блока
            world.setBlockState(pos, state.with(SolidFuelGeneratorBlock.LIT, isGeneratingEnergy), 3);
        }
    }
    protected int getFuelTime(FuelRegistry fuelRegistry, ItemStack stack) {
        return fuelRegistry.getFuelTicks(stack);
    }

    public boolean isBurning() {
        return isActuallyBurning;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("growth_chamber.progress", progress);
        nbt.putInt("growth_chamber.max_progress", maxProgress);
        nbt.putInt("Energy", energy);
        nbt.putInt("BurnTime", burnTime);
        nbt.putInt("FuelTime", fuelTime);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);
        progress = nbt.getInt("growth_chamber.progress").get();
        maxProgress = nbt.getInt("growth_chamber.max_progress").get();
        super.readNbt(nbt, registryLookup);
        energy = nbt.getInt("Energy").get();
        burnTime = nbt.getInt("BurnTime").get();
        fuelTime = nbt.getInt("FuelTime").get();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public boolean isEnergyFull() {
        return energy >= MAX_ENERGY;
    }
    
    private void chargeBattery() {
        ItemStack outputStack = getStack(OUTPUT_SLOT);
        if (outputStack.getItem() instanceof BatteryItem batteryItem) {
            int currentBatteryEnergy = batteryItem.getEnergy(outputStack);
            int maxBatteryEnergy = batteryItem.getMaxEnergy();
            
            if (currentBatteryEnergy < maxBatteryEnergy && energy > 0) {
                int transferAmount = Math.min(energy, Math.min(50, maxBatteryEnergy - currentBatteryEnergy));
                int actualTransferred = batteryItem.receiveEnergy(outputStack, transferAmount);
                energy -= actualTransferred;
                markDirty();
            }
        }
    }
}
