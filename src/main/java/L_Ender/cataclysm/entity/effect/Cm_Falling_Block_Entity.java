package L_Ender.cataclysm.entity.effect;

import L_Ender.cataclysm.init.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Optional;

public class Cm_Falling_Block_Entity extends Entity {
    public int duration;
    protected static final DataParameter<BlockPos> DATA_START_POS = EntityDataManager.createKey(Cm_Falling_Block_Entity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Optional<BlockState>> BLOCK_STATE = EntityDataManager.createKey(Cm_Falling_Block_Entity.class, DataSerializers.OPTIONAL_BLOCK_STATE);

    public Cm_Falling_Block_Entity(EntityType<Cm_Falling_Block_Entity> type, World world) {
        super(type, world);
        this.duration = 20;
    }

    public Cm_Falling_Block_Entity(World p_31953_, double p_31954_, double p_31955_, double p_31956_, BlockState p_31957_, int duration) {
        this(ModEntities.CM_FALLING_BLOCK.get(), p_31953_);
        this.setBlock(p_31957_);
        this.setPosition(p_31954_, p_31955_ + (double)((1.0F - this.getHeight()) / 2.0F), p_31956_);
        this.setMotion(Vector3d.ZERO);
        this.duration = duration;
        this.setMotion(Vector3d.ZERO);
        this.prevPosX = p_31954_;
        this.prevPosY = p_31955_;
        this.prevPosZ = p_31956_;
        this.setStartPos(this.getPosition());
    }


    public void setStartPos(BlockPos p_31960_) {
        this.dataManager.set(DATA_START_POS, p_31960_);
    }

    public BlockPos getStartPos() {
        return this.dataManager.get(DATA_START_POS);
    }

    protected void registerData() {
        this.dataManager.register(DATA_START_POS, BlockPos.ZERO);
        this.dataManager.register(BLOCK_STATE, Optional.of(Blocks.DIRT.getDefaultState()));
    }

    public BlockState getBlock() {
        Optional<BlockState> bsOp = this.dataManager.get(BLOCK_STATE);
        return bsOp.orElse(null);
    }

    public void setBlock(BlockState block) {
        this.dataManager.set(BLOCK_STATE, Optional.of(block));
    }

    public void tick() {
        if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
        }
        this.move(MoverType.SELF, this.getMotion());
        this.setMotion(this.getMotion().scale(0.98D));

        if (this.onGround && ticksExisted > duration) {
            remove();
        }
        if (ticksExisted > 300) {
            remove();
        }

    }

    protected void readAdditional(CompoundNBT p_31973_) {
        INBT blockStateCompound = p_31973_.get("block");
        if (blockStateCompound != null) {
            BlockState blockState = NBTUtil.readBlockState((CompoundNBT) blockStateCompound);
            setBlock(blockState);
        }
        p_31973_.putInt("Time", this.duration);

    }

    protected void writeAdditional(CompoundNBT p_31964_) {
        BlockState blockState = getBlock();
        if (blockState != null) p_31964_.put("block", NBTUtil.writeBlockState(blockState));
        this.duration = p_31964_.getInt("Time");

    }

    @OnlyIn(Dist.CLIENT)
    public World getWorldObj() {
        return this.world;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }

    public BlockState getBlockState() {
        Optional<BlockState> bsOp = dataManager.get(BLOCK_STATE);
        return bsOp.orElse(null);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
