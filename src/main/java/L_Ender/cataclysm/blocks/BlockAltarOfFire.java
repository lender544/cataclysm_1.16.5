package L_Ender.cataclysm.blocks;

import L_Ender.cataclysm.init.ModTileentites;
import L_Ender.cataclysm.tileentities.TileEntityAltarOfFire;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockAltarOfFire extends ContainerBlock {

    public BlockAltarOfFire(Properties props) {
        super(props);
    }


    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if ( random.nextInt(5) == 0) {
            for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                worldIn.addParticle(ParticleTypes.LAVA, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.5D, (double)pos.getZ() + 0.5D, (double)(random.nextFloat() / 2.0F), 5.0E-5D, (double)(random.nextFloat() / 2.0F));
            }
        }
    }

    public void onEntityCollision(BlockState p_51269_, World p_51270_, BlockPos p_51271_, Entity p_51272_) {
        if ( p_51272_ instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)p_51272_)) {
            p_51272_.attackEntityFrom(DamageSource.IN_FIRE, 3);
        }

        super.onEntityCollision(p_51269_, p_51270_, p_51271_, p_51272_);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getHeldItem(handIn);
        if (worldIn.getTileEntity(pos) instanceof TileEntityAltarOfFire && (!player.isSneaking() && heldItem.getItem() != this.asItem())) {
            TileEntityAltarOfFire aof = (TileEntityAltarOfFire)worldIn.getTileEntity(pos);
            ItemStack copy = heldItem.copy();
            copy.setCount(1);
            if(aof.getStackInSlot(0).isEmpty()){
                aof.setInventorySlotContents(0, copy);
                if(!player.isCreative()){
                    heldItem.shrink(1);
                }
            }else{
                spawnAsEntity(worldIn, pos, aof.getStackInSlot(0).copy());
                aof.setInventorySlotContents(0, ItemStack.EMPTY);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityAltarOfFire();
    }
}