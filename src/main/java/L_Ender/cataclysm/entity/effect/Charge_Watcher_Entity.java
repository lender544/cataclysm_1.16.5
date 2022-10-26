package L_Ender.cataclysm.entity.effect;


import L_Ender.cataclysm.init.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public class Charge_Watcher_Entity extends Entity {
    static final DataParameter<Integer> TIMER = EntityDataManager.createKey(Charge_Watcher_Entity.class, DataSerializers.VARINT);
    int effectiveChargeTime;
    double dx;
    double dz;
    float damagePerEffectiveCharge;
    double speedIndex;
    double knockbackSpeedIndex;
    LivingEntity source;
    boolean stopTracking = false;


    public Charge_Watcher_Entity(EntityType<? extends Charge_Watcher_Entity> entityTypeIn, World level) {
        super(entityTypeIn, level);
    }

    public Charge_Watcher_Entity(World level, BlockPos pos, int effectiveChargeTime, double knockbackSpeedIndex, double speedIndex, float damagePerEffectiveCharge, double dx, double dz, LivingEntity source) {
        super(ModEntities.CHARGE_WATCHER.get(), level);
        setPosition(pos.getX(), pos.getY(), pos.getZ());
        dataManager.set(TIMER, effectiveChargeTime);
        this.effectiveChargeTime = effectiveChargeTime;
        this.knockbackSpeedIndex = knockbackSpeedIndex;
        this.damagePerEffectiveCharge = damagePerEffectiveCharge;
        this.dx = dx;
        this.dz = dz;
        this.source = source;
        this.speedIndex = speedIndex;
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isRemote()) {
            int temp = dataManager.get(TIMER);

            //Deal with rocket punch is valid
            if (temp > 0 && !stopTracking && source != null) {
                //Slightly enlarge player's hitbox
                AxisAlignedBB collideBox = source.getBoundingBox().grow(0.5f, 0.5, 0.5f);

                //Collision Detection
                List<LivingEntity> checks = world.getEntitiesWithinAABB(LivingEntity.class, collideBox);
                checks.remove(source);

                //If any mob is detected
                if (!checks.isEmpty()) {
                    // spawn an watchEntity to simulate rocket punch effect
                    Wall_Watcher_Entity watchEntity = new Wall_Watcher_Entity(world, source.getPosition(), temp, effectiveChargeTime,
                            knockbackSpeedIndex, damagePerEffectiveCharge, dx, dz,
                            source);
                    for (LivingEntity target : checks) {
                        // Deal damage
                        boolean flag = target.attackEntityFrom(DamageSource.causeIndirectDamage(this,source), damagePerEffectiveCharge * effectiveChargeTime);
                        watchEntity.watch(target);
                        if(flag){
                            target.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.5f, 0.8F);
                        }

                    }
                    source.world.addEntity(watchEntity);

                    // Player stop moving and clear pocket punch status
                    source.setMotion(0, 0, 0);
                    source.markPositionDirty();
                    stopTracking = true;
                }

                // If rocket punch is active and player hit a wall
                // stop player and clear rocket punch status
                if (source.collidedHorizontally) {
                    stopTracking = true;
                }

                // Deal with player rocket punch movement
                if (!stopTracking) {
                    dataManager.set(TIMER, temp - 1);
                }
            }

            if (stopTracking || source == null || temp == 0) {
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

}
