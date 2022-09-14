package L_Ender.cataclysm.entity.projectile;

import L_Ender.cataclysm.entity.Ignited_Revenant_Entity;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Ash_Breath_Entity extends Entity {
    private static final int RANGE = 7;
    private static final int ARC = 45;
    private LivingEntity caster;
    private UUID casterUuid;

    public Ash_Breath_Entity(EntityType<? extends Ash_Breath_Entity> type, World world) {
        super(type, world);

    }

    public Ash_Breath_Entity(EntityType<? extends Ash_Breath_Entity> type, World world, LivingEntity caster) {
        super(type, world);
        this.setCaster(caster);

    }

    @Override
    public PushReaction getPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public void tick() {
        super.tick();

        if (caster != null && !caster.isAlive()) this.remove();

        if (caster !=null){
            this.rotationYaw = caster.rotationYawHead;
           // this.setXRot(caster.getXRot());
        }
        float yaw = (float) Math.toRadians(-rotationYaw);
        float pitch = (float) Math.toRadians(-rotationPitch);
        float spread = 0.25f;
        float speed = 0.56f;
        float xComp = (float) (Math.sin(yaw) * Math.cos(pitch));
        float yComp = (float) (Math.sin(pitch));
        float zComp = (float) (Math.cos(yaw) * Math.cos(pitch));
        double theta = (rotationYaw) * (Math.PI / 180);
        theta += Math.PI / 2;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        double vec = 0.9;
        if (world.isRemote) {
            for (int i = 0; i < 80; i++) {
                double xSpeed = speed * xComp + (spread * 1 * (rand.nextFloat() * 2 - 1) * (Math.sqrt(1 - xComp * xComp)));
                double ySpeed = speed * yComp + (spread * 1 * (rand.nextFloat() * 2 - 1) * (Math.sqrt(1 - yComp * yComp)));
                double zSpeed = speed * zComp + (spread * 1 * (rand.nextFloat() * 2 - 1) * (Math.sqrt(1 - zComp * zComp)));
                world.addParticle(ParticleTypes.SMOKE, getPosX() + vec * vecX, getPosY(), getPosZ() + vec * vecZ, xSpeed, ySpeed, zSpeed);
            }
            for (int i = 0; i < 2; i++) {
                double xSpeed = speed * xComp + (spread * 0.7 * (rand.nextFloat() * 2 - 1) * (Math.sqrt(1 - xComp * xComp)));
                double ySpeed = speed * yComp + (spread * 0.7 * (rand.nextFloat() * 2 - 1) * (Math.sqrt(1 - yComp * yComp)));
                double zSpeed = speed * zComp + (spread * 0.7 * (rand.nextFloat() * 2 - 1) * (Math.sqrt(1 - zComp * zComp)));
                world.addParticle(ParticleTypes.FLAME, getPosX() + vec * vecX, getPosY(), getPosZ() + vec * vecZ, xSpeed, ySpeed, zSpeed);
            }
        }
        if (ticksExisted > 2 && caster != null) {
            hitEntities();
        }
        if (ticksExisted > 25) remove(); ;
    }

    public void hitEntities() {
        List<LivingEntity> entitiesHit = getEntityLivingBaseNearby(RANGE, RANGE, RANGE, RANGE);;
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitYaw = (float) ((Math.atan2(entityHit.getPosZ() - getPosZ(), entityHit.getPosX() - getPosX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingYaw = rotationYaw % 360;
            if (entityHitYaw < 0) {
                entityHitYaw += 360;
            }
            if (entityAttackingYaw < 0) {
                entityAttackingYaw += 360;
            }
            float entityRelativeYaw = entityHitYaw - entityAttackingYaw;

            float xzDistance = (float) Math.sqrt((entityHit.getPosZ() - getPosZ()) * (entityHit.getPosZ() - getPosZ()) + (entityHit.getPosX() - getPosX()) * (entityHit.getPosX() - getPosX()));
            double hitY = entityHit.getPosY() + entityHit.getHeight() / 2.0;
            float entityHitPitch = (float) ((Math.atan2((hitY - getPosY()), xzDistance) * (180 / Math.PI)) % 360);
            float entityAttackingPitch = -rotationPitch % 360;
            if (entityHitPitch < 0) {
                entityHitPitch += 360;
            }
            if (entityAttackingPitch < 0) {
                entityAttackingPitch += 360;
            }
            float entityRelativePitch = entityHitPitch - entityAttackingPitch;
            float entityHitDistance = (float) Math.sqrt((entityHit.getPosZ() - getPosZ()) * (entityHit.getPosZ() - getPosZ()) + (entityHit.getPosX() - getPosX()) * (entityHit.getPosX() - getPosX()) + (hitY - getPosY()) * (hitY - getPosY()));
            int distance = this.ticksExisted / 2 ;
            boolean inRange = entityHitDistance <= distance + 1.0F;
            boolean yawCheck = (entityRelativeYaw <= ARC / 2f && entityRelativeYaw >= -ARC / 2f) || (entityRelativeYaw >= 360 - ARC / 2f || entityRelativeYaw <= -360 + ARC / 2f);
            boolean pitchCheck = (entityRelativePitch <= ARC / 2f && entityRelativePitch >= -ARC / 2f) || (entityRelativePitch >= 360 - ARC / 2f || entityRelativePitch <= -360 + ARC / 2f);
            boolean CloseCheck = caster instanceof Ignited_Revenant_Entity && entityHitDistance <= 2;
            if (inRange && yawCheck && pitchCheck || CloseCheck) {
                if (this.ticksExisted % 3 == 0) {
                    if (!isOnSameTeam(entityHit) && entityHit != caster) {
                        boolean flag = entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, caster), 4);
                        if (flag) {
                            //entityHit.setDeltaMovement(entityHit.getDeltaMovement().multiply(0.25, 1, 0.25));
                            EffectInstance effectinstance = new EffectInstance(Effects.BLINDNESS, 100, 0, false, false, true);
                            EffectInstance effectinstance1 = new EffectInstance(Effects.NAUSEA, 100, 0, false, false, true);
                           // entityHit.addPotionEffect(effectinstance);
                          //  entityHit.addPotionEffect(effectinstance1);
                        }
                    }
                }
            }
        }
    }

    protected void registerData() {

    }

    public void setCaster(@Nullable LivingEntity p_190549_1_) {
        this.caster = p_190549_1_;
        this.casterUuid = p_190549_1_ == null ? null : p_190549_1_.getUniqueID();
    }

    @Nullable
    public LivingEntity getCaster() {
        if (this.caster == null && this.casterUuid != null && this.world instanceof ServerWorld) {
            Entity entity = ((ServerWorld)this.world).getEntityByUuid(this.casterUuid);
            if (entity instanceof LivingEntity) {
                this.caster = (LivingEntity)entity;
            }
        }

        return this.caster;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditional(CompoundNBT compound) {
        if (compound.hasUniqueId("Owner")) {
            this.casterUuid = compound.getUniqueId("Owner");
        }

    }

    protected void writeAdditional(CompoundNBT compound) {
        if (this.casterUuid != null) {
            compound.putUniqueId("Owner", this.casterUuid);
        }

    }


    @Override
    public boolean func_241845_aY() {
        return false;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }



    @Override
    public void applyEntityCollision(Entity entityIn) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    public  List<LivingEntity> getEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius) {
        return getEntitiesNearby(LivingEntity.class, distanceX, distanceY, distanceZ, radius);
    }

    public <T extends Entity> List<T> getEntitiesNearby(Class<T> entityClass, double dX, double dY, double dZ, double r) {
        return world.getEntitiesWithinAABB(entityClass, getBoundingBox().grow(dX, dY, dZ), e -> e != this && getDistance(e) <= r + e.getWidth() / 2f && e.getPosY() <= getPosY() + dY);
    }

    @Override
    public boolean canBePushed() {
        return false;
    }


}