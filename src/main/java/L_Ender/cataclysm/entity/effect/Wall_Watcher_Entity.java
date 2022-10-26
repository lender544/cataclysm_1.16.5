package L_Ender.cataclysm.entity.effect;

import L_Ender.cataclysm.init.ModEffect;
import L_Ender.cataclysm.init.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;
import java.util.List;


public class Wall_Watcher_Entity extends Entity {
    static final DataParameter<Integer> TIMER = EntityDataManager.createKey(Wall_Watcher_Entity.class, DataSerializers.VARINT);
    int effectiveChargeTime;
    double knockbackSpeedIndex;
    float damagePerEffectiveCharge;
    double dx;
    double dz;
    LivingEntity source;
    List<YUnchangedLivingEntity> watchedEntities;


    public Wall_Watcher_Entity(EntityType<? extends Wall_Watcher_Entity> entityTypeIn, World level) {
        super(entityTypeIn, level);
    }

    public Wall_Watcher_Entity(World level, BlockPos pos, int timer, int effectiveChargeTime, double knockbackSpeedIndex, float damagePerEffectiveCharge, double dx, double dz, LivingEntity source) {
        super(ModEntities.WALL_WATCHER.get(), level);
        setPosition(pos.getX(), pos.getY(), pos.getZ());
        dataManager.set(TIMER, timer);
        this.effectiveChargeTime = effectiveChargeTime;
        this.knockbackSpeedIndex = knockbackSpeedIndex;
        this.damagePerEffectiveCharge = damagePerEffectiveCharge;
        this.dx = dx;
        this.dz = dz;
        this.source = source;
        watchedEntities = new ArrayList<>();
    }

    public void watch(LivingEntity livingEntity) {
        if (livingEntity != null) {
            watchedEntities.add(new YUnchangedLivingEntity(livingEntity));
        }
    }

    public void removeFromWatchList(YUnchangedLivingEntity yUnchangedLivingEntity) {
        if (yUnchangedLivingEntity != null) {
            watchedEntities.remove(yUnchangedLivingEntity);
        }
    }


    @Override
    public void tick() {
        super.tick();
        if (!world.isRemote()) {
            int temp = dataManager.get(TIMER);
            if (watchedEntities != null && source != null) {
                if (!watchedEntities.isEmpty()) {
                    List<YUnchangedLivingEntity> entitiesRemoveFromWatchList = new ArrayList<>();
                    for (YUnchangedLivingEntity entity : watchedEntities) {
                        if (entity.livingEntity.collidedHorizontally) {
                            entity.livingEntity.hurtResistantTime = 0;
                            float realDamageApplied = damagePerEffectiveCharge * effectiveChargeTime + 1;
                            boolean flag =entity.livingEntity.attackEntityFrom(DamageSource.causeIndirectDamage(this,source), realDamageApplied);
                            if(flag){
                                entity.livingEntity.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE,0.3F,1);
                                entity.livingEntity.addPotionEffect(new EffectInstance(ModEffect.EFFECTSTUN.get(), 50));
                            }

                            entitiesRemoveFromWatchList.add(entity);
                        } else {
                            entity.setMotion(dx * knockbackSpeedIndex, dz * knockbackSpeedIndex);
                        }
                    }
                    for (YUnchangedLivingEntity remove : entitiesRemoveFromWatchList) {
                        removeFromWatchList(remove);
                    }
                    if (temp - 1 == 0) {
                        watchedEntities.clear();
                        remove();
                    } else dataManager.set(TIMER, temp - 1);
                } else {
                    if (temp - 1 == 0) remove();
                    else dataManager.set(TIMER, temp - 1);
                }
            } else {
                remove();
            }
        }
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }


    @Override
    protected void registerData() {
        dataManager.register(TIMER, 0);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        source = null;
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    static class YUnchangedLivingEntity {
        LivingEntity livingEntity;
        double Y;

        public YUnchangedLivingEntity(LivingEntity livingEntity) {
            this.livingEntity = livingEntity;
            Y = livingEntity.getPosY();
        }

        void setMotion(double X, double Z) {
            livingEntity.setMotion(X, 0, Z);
            livingEntity.setPosition(livingEntity.getPosX(), Y, livingEntity.getPosZ());
            livingEntity.markPositionDirty();
            livingEntity.velocityChanged = true;
        }

    }
}
