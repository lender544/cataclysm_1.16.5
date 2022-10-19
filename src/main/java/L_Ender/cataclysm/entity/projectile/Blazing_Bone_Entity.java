package L_Ender.cataclysm.entity.projectile;

import L_Ender.cataclysm.init.ModEntities;
import L_Ender.cataclysm.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;


public class Blazing_Bone_Entity extends ProjectileItemEntity {


    public Blazing_Bone_Entity(EntityType<? extends Blazing_Bone_Entity> type, World world) {
        super(type, world);
    }

    public Blazing_Bone_Entity(World worldIn, LivingEntity throwerIn) {
        super(ModEntities.BLAZING_BONE.get(), throwerIn, worldIn);
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);

    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);

    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BLAZING_BONE.get();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult result) {
        super.onEntityHit(result);
        Entity shooter = this.getShooter();
        Entity entity = result.getEntity();
        float i = 4f;
        if (shooter instanceof LivingEntity) {
            if (!((entity == shooter) || (shooter.isOnSameTeam(entity)))) {
                entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, (LivingEntity) shooter).setProjectile(), i);
            }
        }else{
            entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, null).setProjectile(), i);
        }

    }

    @Override
    public boolean hasNoGravity() {
        return false;
    }

    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte)3);
            this.remove();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for(int i = 0; i < 8; ++i) {
                this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(ModItems.BLAZING_BONE.get())), this.getPosX(), this.getPosY(), this.getPosZ(), rand.nextGaussian() * 0.3D, rand.nextGaussian() * 0.3D, rand.nextGaussian() * 0.3D);
            }
        }
    }
}
