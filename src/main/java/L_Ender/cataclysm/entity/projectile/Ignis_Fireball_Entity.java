package L_Ender.cataclysm.entity.projectile;

import L_Ender.cataclysm.entity.Ignis_Entity;
import L_Ender.cataclysm.init.ModEffect;
import L_Ender.cataclysm.init.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;


public class Ignis_Fireball_Entity extends DamagingProjectileEntity {

    private static final DataParameter<Boolean> SOUL = EntityDataManager.createKey(Ignis_Fireball_Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FIRED = EntityDataManager.createKey(Ignis_Fireball_Entity.class, DataSerializers.BOOLEAN);
    private int timer;

    public Ignis_Fireball_Entity(EntityType<? extends Ignis_Fireball_Entity> type, World world) {
        super(type, world);
    }

    public Ignis_Fireball_Entity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(ModEntities.IGNIS_FIREBALL.get(), shooter, accelX, accelY, accelZ, worldIn);
    }

    public Ignis_Fireball_Entity(World worldIn, LivingEntity entity) {
        this(ModEntities.IGNIS_FIREBALL.get(), worldIn);
        this.setShooter(entity);
    }

    public void tick() {
        super.tick();
        if (!this.world.isRemote) {
            timer--;
            if (timer <= 0) {
                if (!getFired()){
                    setFired(true);
                }
            }
        }
        if (this.ticksExisted > 300) {
            this.remove();
        }

        if (timer == 0) {
            Entity entity = this.getShooter();
            if (entity instanceof MobEntity && ((MobEntity) entity).getAttackTarget() != null) {
                LivingEntity target = ((MobEntity) entity).getAttackTarget();
                if(target == null){
                    this.remove();
                }

                double d0 = target.getPosX() - this.getPosX();
                double d1 = target.getPosY() + target.getHeight() * 0.5F - this.getPosY();
                double d2 = target.getPosZ() - this.getPosZ();
                float speed = this.isSoul() ? 2.5F : 2.0F;
                shoot(d0, d1, d2, speed, 0);
                this.rotationYaw = -((float) MathHelper.atan2(d0, d2)) * (180F / (float) Math.PI);

            }
        }
    }

    public void setUp(int delay) {
        setFired(false);
        timer = delay;

    }

    protected void onEntityHit(EntityRayTraceResult result) {
        super.onEntityHit(result);
        Entity shooter = this.getShooter();
        if (!this.world.isRemote && getFired() && !(result.getEntity() instanceof Ignis_Fireball_Entity || result.getEntity() instanceof Ignis_Abyss_Fireball_Entity || result.getEntity() instanceof Ignis_Entity && shooter instanceof Ignis_Entity)) {
            Entity entity = result.getEntity();
            boolean flag;
            if (shooter instanceof LivingEntity) {
                LivingEntity owner = (LivingEntity)shooter;
                float damage = this.isSoul() ? 8.0F : 6.0F;
                if (entity instanceof LivingEntity) {
                    flag = entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, owner).setProjectile(), damage + ((LivingEntity) entity).getMaxHealth() * 0.07f);
                }else{
                    flag = entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, owner).setProjectile(), damage);
                }

                if (flag) {
                    this.applyEnchantments(owner, entity);
                    owner.heal(5.0F);
                }
            } else {
                flag = entity.attackEntityFrom(DamageSource.MAGIC, 6.0F);
            }
            this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), 1.0F, false, Explosion.Mode.NONE);
            this.remove();

            if (flag && entity instanceof LivingEntity) {
                EffectInstance effectinstance1 = ((LivingEntity)entity).getActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                int i = 1;
                if (effectinstance1 != null) {
                    i += effectinstance1.getAmplifier();
                    ((LivingEntity)entity).removeActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                } else {
                    --i;
                }

                i = MathHelper.clamp(i, 0, 4);
                EffectInstance effectinstance = new EffectInstance(ModEffect.EFFECTBLAZING_BRAND.get(), 150, i, false, false, true);
                ((LivingEntity)entity).addPotionEffect(effectinstance);

            }

        }
    }

    protected void func_230299_a_(BlockRayTraceResult result) {
        super.func_230299_a_(result);
        if (!this.world.isRemote && getFired()) {
            this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), 1.0F, false, Explosion.Mode.NONE);
            this.remove();
        }

    }

    public boolean canBeCollidedWith() {
        return false;
    }


    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    protected void registerData() {
        this.dataManager.register(SOUL, false);
        this.dataManager.register(FIRED, false);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("is_soul", this.isSoul());
        compound.putInt("timer", timer);
        compound.putBoolean("fired", getFired());
    }


    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSoul(compound.getBoolean("is_soul"));
        timer = compound.getInt("timer");
        this.setFired(compound.getBoolean("fired"));
    }


    public void setSoul(boolean IsSoul) {
        getDataManager().set(SOUL, IsSoul);
    }

    public boolean isSoul() {
        return getDataManager().get(SOUL);
    }

    public void setFired(boolean fired) {
        getDataManager().set(FIRED, fired);
    }

    public boolean getFired() {
        return getDataManager().get(FIRED);
    }

    protected boolean isFireballFiery() {
        return false;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
