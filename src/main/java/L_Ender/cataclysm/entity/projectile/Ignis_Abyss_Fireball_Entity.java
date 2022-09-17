package L_Ender.cataclysm.entity.projectile;

import L_Ender.cataclysm.entity.Ignis_Entity;
import L_Ender.cataclysm.init.ModEffect;
import L_Ender.cataclysm.init.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;


public class Ignis_Abyss_Fireball_Entity extends DamagingProjectileEntity {

    private static final DataParameter<Integer> BOUNCES = EntityDataManager.createKey(Ignis_Abyss_Fireball_Entity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> FIRED = EntityDataManager.createKey(Ignis_Abyss_Fireball_Entity.class, DataSerializers.BOOLEAN);
    private int timer;

    public Ignis_Abyss_Fireball_Entity(EntityType<? extends Ignis_Abyss_Fireball_Entity> type, World world) {
        super(type, world);
    }

    public Ignis_Abyss_Fireball_Entity(World worldIn, LivingEntity shooter, double accelX, double accelY, double accelZ) {
        super(ModEntities.IGNIS_ABYSS_FIREBALL.get(), shooter, accelX, accelY, accelZ, worldIn);
    }

    public Ignis_Abyss_Fireball_Entity(World worldIn, LivingEntity entity) {
        this(ModEntities.IGNIS_ABYSS_FIREBALL.get(), worldIn);
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

        if (timer == 0 || timer == -40) {
            Entity entity = this.getShooter();
            if (entity instanceof MobEntity && ((MobEntity) entity).getAttackTarget() != null) {
                LivingEntity target = ((MobEntity) entity).getAttackTarget();
                if(target == null){
                    this.remove();
                }

                double d0 = target.getPosX() - this.getPosX();
                double d1 = target.getPosY() + target.getHeight() * 0.5F - this.getPosY();
                double d2 = target.getPosZ() - this.getPosZ();
                float speed = 1.8F;
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
                if (entity instanceof LivingEntity) {
                    flag = entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, owner).setProjectile(), 10.0F + ((LivingEntity) entity).getMaxHealth() * 0.2f);
                }else{
                    flag = entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, owner).setProjectile(), 10.0F);
                }

                if (flag) {
                    this.applyEnchantments(owner, entity);
                    owner.heal(5.0F);
                }
            } else {
                flag = entity.attackEntityFrom(DamageSource.MAGIC, 6.0F);
            }
            this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), 2.0F, true, Explosion.Mode.NONE);
            this.remove();
            if (flag && entity instanceof LivingEntity) {
                EffectInstance effectinstance1 = ((LivingEntity)entity).getActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                int i = 2;
                if (effectinstance1 != null) {
                    i += effectinstance1.getAmplifier();
                    ((LivingEntity)entity).removeActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                } else {
                    --i;
                }

                i = MathHelper.clamp(i, 0, 4);
                EffectInstance effectinstance = new EffectInstance(ModEffect.EFFECTBLAZING_BRAND.get(), 200, i, false, false, true);
                ((LivingEntity)entity).addPotionEffect(effectinstance);

            }

        }
    }

    protected void func_230299_a_(BlockRayTraceResult result) {
        super.func_230299_a_(result);
        BlockState blockstate = this.world.getBlockState(result.getPos());
        if (!blockstate.getCollisionShape(this.world, result.getPos()).isEmpty() && getFired()) {
            Direction face = result.getFace();
            blockstate.onProjectileCollision(this.world, blockstate, result, this);

            Vector3d motion = this.getMotion();

            double motionX = motion.getX();
            double motionY = motion.getY();
            double motionZ = motion.getZ();

            if (face == Direction.EAST)
                motionX = -motionX;
            else if (face == Direction.SOUTH)
                motionZ = -motionZ;
            else if (face == Direction.WEST)
                motionX = -motionX;
            else if (face == Direction.NORTH)
                motionZ = -motionZ;
            else if (face == Direction.UP)
                motionY = -motionY;
            else if (face == Direction.DOWN)
                motionY = -motionY;

            this.setMotion(motionX, motionY, motionZ);
            this.accelerationX = motionX * 0.05D;
            this.accelerationY = motionY * 0.05D;
            this.accelerationZ = motionZ * 0.05D;

            if (this.ticksExisted > 500 || this.getTotalBounces() > 5) {
                if (!this.world.isRemote) {
                    this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), 2.0F, true, Explosion.Mode.NONE);
                    this.remove();
                }
            } else {
                this.setTotalBounces(this.getTotalBounces() + 1);
            }
        }

    }

    @Override
    protected void onImpact(RayTraceResult result) {
        RayTraceResult.Type raytraceresult$type = result.getType();
        if (raytraceresult$type == RayTraceResult.Type.ENTITY) {
            this.onEntityHit((EntityRayTraceResult) result);
        } else if (raytraceresult$type == RayTraceResult.Type.BLOCK) {
            this.func_230299_a_((BlockRayTraceResult) result);
        }
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.markVelocityChanged();
            Entity entity = source.getTrueSource();
            if (entity != null && this.getFired()) {
                Vector3d vector3d = entity.getLookVec();
                this.setMotion(vector3d);
                this.accelerationX = vector3d.x * 0.1D;
                this.accelerationY = vector3d.y * 0.1D;
                this.accelerationZ = vector3d.z * 0.1D;
                this.setShooter(entity);
                return true;
            } else {
                return false;
            }
        }
    }


    protected void registerData() {
        this.dataManager.register(FIRED, false);
        this.dataManager.register(BOUNCES, 0);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("timer", timer);
        compound.putBoolean("fired", getFired());
    }


    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        timer = compound.getInt("timer");
        this.setFired(compound.getBoolean("fired"));
    }

    public void setTotalBounces(int bounces) {
        getDataManager().set(BOUNCES, bounces);
    }

    public int getTotalBounces() {
        return getDataManager().get(BOUNCES);
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
