package L_Ender.cataclysm.tileentities;

import L_Ender.cataclysm.entity.Ignis_Entity;
import L_Ender.cataclysm.entity.effect.Flame_Strike_Entity;
import L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import L_Ender.cataclysm.init.ModEntities;
import L_Ender.cataclysm.init.ModItems;
import L_Ender.cataclysm.init.ModTag;
import L_Ender.cataclysm.init.ModTileentites;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityAltarOfFire extends LockableTileEntity implements ITickableTileEntity {

    public int ticksExisted;
    private static final int NUM_SLOTS = 1;
    private NonNullList<ItemStack> stacks = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
    public boolean summoningthis = false;
    private int summoningticks = 0;


    public TileEntityAltarOfFire() {
        super(ModTileentites.ALTAR_OF_FIRE.get());
    }

    public void tick() {
        ticksExisted++;
        summoningthis = false;
        if (!this.getStackInSlot(0).isEmpty()) {
            if(this.getStackInSlot(0).getItem() == ModItems.BURNING_ASHES.get()){
                summoningthis = true;
                if(summoningticks == 1) {
                    ScreenShake_Entity.ScreenShake(this.world, Vector3d.copyCentered(getPos()), 20, 0.05f, 0, 150);
                 //   this.world.addEntity(new Flame_Strike_Entity(this.world, this.getPos().getX() + 0.5F, this.getPos().getY(), this.getPos().getZ() + 0.5F, 0, 0, 100, 0, 2.5F, false, null));
                }
                if(summoningticks > 121) {
                    this.setInventorySlotContents(0, ItemStack.EMPTY);
                    BlockBreaking(3, 3, 3);
                    Ignis_Entity ignis = ModEntities.IGNIS.get().create(world);
                    ignis.setPosition(this.getPos().getX() + 0.5F, this.getPos().getY() + 3, this.getPos().getZ() + 0.5F);
                    if(!world.isRemote){
                        world.addEntity(ignis);
                    }
                }

            }
        }
        if(!summoningthis){
            summoningticks = 0;
        }else{
            summoningticks++;
        }
    }

    private void BlockBreaking(int x, int y, int z) {
        int MthX = MathHelper.floor(this.getPos().getX());
        int MthY = MathHelper.floor(this.getPos().getY());
        int MthZ = MathHelper.floor(this.getPos().getZ());
        boolean flag = false;
            for (int k2 = -x; k2 <= x; ++k2) {
                for (int l2 = -z; l2 <= z; ++l2) {
                    for (int j = -1; j <= y; ++j) {
                        int i3 = MthX + k2;
                        int k = MthY + j;
                        int l = MthZ + l2;
                        BlockPos blockpos = new BlockPos(i3, k, l);
                        BlockState blockstate = this.world.getBlockState(blockpos);
                        Block block = blockstate.getBlock();
                        ITag<Block> Tag = BlockTags.getCollection().get(ModTag.ALTAR_DESTROY_IMMUNE);
                        if (block != Blocks.AIR && !Tag.contains(block)) {
                            this.world.destroyBlock(blockpos, false);

                    }
                }
            }
        }
    }

    @Override
    public int getSizeInventory() {
        return this.stacks.size();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.stacks.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (!this.stacks.get(index).isEmpty()) {
            ItemStack itemstack;

            if (this.stacks.get(index).getCount() <= count) {
                itemstack = this.stacks.get(index);
                this.stacks.set(index, ItemStack.EMPTY);
            } else {
                itemstack = this.stacks.get(index).split(count);

                if (this.stacks.get(index).isEmpty()) {
                    this.stacks.set(index, ItemStack.EMPTY);
                }

            }
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        boolean flag = !stack.isEmpty() && stack.isItemEqual(this.stacks.get(index)) && ItemStack.areItemStackTagsEqual(stack, this.stacks.get(index));
        this.stacks.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }

    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.stacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.stacks);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        ItemStackHelper.saveAllItems(compound, this.stacks);
        return compound;
    }

    @Override
    public void openInventory(PlayerEntity player) {
    }

    @Override
    public void closeInventory(PlayerEntity player) {
    }



    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stacks.clear();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, -1, getUpdateTag());
    }


    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        read(this.getBlockState(), packet.getNbtCompound());
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack lvt_2_1_ = this.stacks.get(index);
        if (lvt_2_1_.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.stacks.set(index, ItemStack.EMPTY);
            return lvt_2_1_;
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        return getDefaultName();
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("block.cataclysm.altar_of_fire");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < this.getSizeInventory(); i++) {
            if (!this.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
