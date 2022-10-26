package L_Ender.cataclysm.entity.effect;

import L_Ender.cataclysm.init.ModEffect;
import L_Ender.cataclysm.init.ModEntities;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class Flame_Strike_Entity extends Entity {
    private static final DataParameter<Float> DATA_RADIUS = EntityDataManager.createKey(Flame_Strike_Entity.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> DATA_WAITING = EntityDataManager.createKey(Flame_Strike_Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_SEE = EntityDataManager.createKey(Flame_Strike_Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SOUL = EntityDataManager.createKey(Flame_Strike_Entity.class, DataSerializers.BOOLEAN);
    private static final float MAX_RADIUS = 32.0F;
    private int duration = 600;
    private int waitTime;
    private int warmupDelayTicks;
    private LivingEntity owner;
    private UUID ownerUUID;

    public Flame_Strike_Entity(EntityType<? extends Flame_Strike_Entity> p_19704_, World p_19705_) {
        super(p_19704_, p_19705_);
        this.noClip = true;
        this.setRadius(3.0F);
    }

    public Flame_Strike_Entity(World world, double x, double y, double z, float p_i47276_8_, int duration, int wait, int delay, float radius, boolean soul, LivingEntity casterIn) {
        this(ModEntities.FLAME_STRIKE.get(), world);
        this.setOwner(casterIn);
        this.setDuration(duration);
        this.waitTime = wait;
        this.warmupDelayTicks = delay;
        this.setRadius(radius);
        this.setSoul(soul);
        this.rotationYaw = p_i47276_8_ * (180F / (float)Math.PI);
        this.setPosition(x, y, z);
    }

    protected void registerData() {
        this.dataManager.register(DATA_RADIUS, 0.5F);
        this.dataManager.register(DATA_WAITING, true);
        this.dataManager.register(DATA_SEE, false);
        this.dataManager.register(SOUL, false);
    }

    public void setRadius(float p_19713_) {
        if (!this.world.isRemote) {
            this.getDataManager().set(DATA_RADIUS, p_19713_);
        }

    }

    public void recalculateSize() {
        double d0 = this.getPosX();
        double d1 = this.getPosY();
        double d2 = this.getPosZ();
        super.recalculateSize();
        this.setPosition(d0, d1, d2);
    }

    public float getRadius() {
        return this.getDataManager().get(DATA_RADIUS);
    }


    protected void setWaiting(boolean p_19731_) {
        this.getDataManager().set(DATA_WAITING, p_19731_);
    }

    public boolean isWaiting() {
        return this.getDataManager().get(DATA_WAITING);
    }


    protected void setSee(boolean p_19731_) {
        this.getDataManager().set(DATA_SEE, p_19731_);
    }

    public boolean isSee() {
        return this.getDataManager().get(DATA_SEE);
    }

    public void setSoul(boolean Soul) {
        this.getDataManager().set(SOUL, Soul);
    }

    public boolean isSoul() {
        return this.getDataManager().get(SOUL);
    }


    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int p_19735_) {
        this.duration = p_19735_;
    }


    public void tick() {
        super.tick();
        boolean flag = this.isWaiting();
        float f = this.getRadius();
        if (this.world.isRemote) {
            if (flag && this.rand.nextBoolean()) {
                return;
            }
            BasicParticleType particleoptions = this.isSoul() ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME ;
            float f1 = flag ? 0.2F : f;
            double spread = Math.PI * 2 ;
            int arcLen = MathHelper.ceil(this.getRadius() * spread);

            if(!flag) {
                if (this.ticksExisted % 2 == 0) {
                    for (int j = 0; j < arcLen; ++j) {
                        float f2 = this.rand.nextFloat() * ((float) Math.PI * 2F);
                        double d0 = this.getPosX() + (double) (MathHelper.cos(f2) * f1) * 0.9;
                        double d2 = this.getPosY();
                        double d4 = this.getPosZ() + (double) (MathHelper.sin(f2) * f1) * 0.9;
                        this.world.addParticle(particleoptions, d0, d2, d4, rand.nextGaussian() * 0.07D, 0.125D * this.getRadius() + 0.4D, rand.nextGaussian() * 0.07D);
                    }
                }
                if (this.rand.nextInt(24) == 0) {
                    this.world.playSound(this.getPosX() + 0.5D, this.getPosY() + 0.5D, this.getPosZ() + 0.5D, SoundEvents.ENTITY_BLAZE_BURN, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
                }
            }
        } else {
            if (this.ticksExisted >= this.waitTime + this.duration + this.warmupDelayTicks) {
                if(this.getRadius() > 0 ){
                    this.setRadius(getRadius() - 0.1F);
                }else{
                    if(!this.isSoul()) {
                        int explosionradius = this.owner instanceof PlayerEntity ? 1 : 2;
                        this.world.createExplosion(this.owner, this.getPosX(), this.getPosY(), this.getPosZ(), explosionradius, Explosion.Mode.NONE);
                    }
                    this.remove();
                }
            }


            if (this.ticksExisted >= this.warmupDelayTicks) {
                this.setSee(true);
            }


            boolean flag1 = this.ticksExisted < this.waitTime + this.warmupDelayTicks;
            if (flag != flag1) {
                this.setWaiting(flag1);
            }

            if (flag1) {
                return;
            }

        }

        if(!flag) {
            if (this.ticksExisted % 5 == 0) {
                for (LivingEntity livingentity : this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox())) {
                    this.damage(livingentity);
                }
            }
        }
    }

    private void damage(LivingEntity Hitentity) {
        LivingEntity caster = this.getOwner();
        if (Hitentity.isAlive() && !Hitentity.isInvulnerable() && Hitentity != caster) {
            if (this.ticksExisted % 2 == 0) {
                float damage = this.isSoul() ? 8.0F : 6.0F;
                if (caster == null) {

                    boolean flag = Hitentity.attackEntityFrom(DamageSource.MAGIC, damage + Hitentity.getMaxHealth() * 0.06f);
                    if (flag) {
                        EffectInstance effectinstance1 = Hitentity.getActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                        int i = 1;
                        if (effectinstance1 != null) {
                            i += effectinstance1.getAmplifier();
                            Hitentity.removeActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                        } else {
                            --i;
                        }

                        i = MathHelper.clamp(i, 0, 4);
                        EffectInstance effectinstance = new EffectInstance(ModEffect.EFFECTBLAZING_BRAND.get(), 200, i, false, false, true);
                        Hitentity.addPotionEffect(effectinstance);
                    }
                } else {
                    float hpDmg = (float) (caster instanceof PlayerEntity ? 0.02 : 0.06);
                    if (caster.isOnSameTeam(Hitentity)) {
                        return;
                    }
                    boolean flag = Hitentity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, caster), damage + Hitentity.getMaxHealth() * hpDmg);
                    if (flag) {
                        EffectInstance effectinstance1 = Hitentity.getActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                        int i = 1;
                        if (effectinstance1 != null) {
                            i += effectinstance1.getAmplifier();
                            Hitentity.removeActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                        } else {
                            --i;
                        }

                        i = MathHelper.clamp(i, 0, 4);
                        EffectInstance effectinstance = new EffectInstance(ModEffect.EFFECTBLAZING_BRAND.get(), 200, i, false, false, true);
                        Hitentity.addPotionEffect(effectinstance);

                    }
                }
            }
        }
    }


    public int getWaitTime() {
        return this.waitTime;
    }

    public void setWaitTime(int p_19741_) {
        this.waitTime = p_19741_;
    }

    public void setOwner(@Nullable LivingEntity ownerIn) {
        this.owner = ownerIn;
        this.ownerUUID = ownerIn == null ? null : ownerIn.getUniqueID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.world instanceof ServerWorld) {
            Entity entity = ((ServerWorld)this.world).getEntityByUuid(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    protected void readAdditional(CompoundNBT compound) {
        this.ticksExisted = compound.getInt("Age");
        this.duration = compound.getInt("Duration");
        this.waitTime = compound.getInt("WaitTime");
        this.warmupDelayTicks = compound.getInt("Delay");
        this.setRadius(compound.getFloat("Radius"));
        if (compound.hasUniqueId("Owner")) {
            this.ownerUUID = compound.getUniqueId("Owner");
        }
        setSoul(compound.getBoolean("is_soul"));

    }

    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("Age", this.ticksExisted);
        compound.putInt("Duration", this.duration);
        compound.putInt("WaitTime", this.waitTime);
        compound.putInt("Delay", this.warmupDelayTicks);
        compound.putFloat("Radius", this.getRadius());
        if (this.ownerUUID != null) {
            compound.putUniqueId("Owner", this.ownerUUID);
        }
        compound.putBoolean("is_soul", isSoul());


    }


    public void notifyDataManagerChange(DataParameter<?> key) {
        if (DATA_RADIUS.equals(key)) {
            this.recalculateSize();
        }

        super.notifyDataManagerChange(key);
    }
    public PushReaction getPushReaction() {
        return PushReaction.IGNORE;
    }

    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public EntitySize getSize(Pose p_19721_) {
        return EntitySize.flexible(this.getRadius() * 1.8F, this.getRadius() * 3.0F);
    }
}

