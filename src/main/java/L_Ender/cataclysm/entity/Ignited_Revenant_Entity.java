package L_Ender.cataclysm.entity;

import L_Ender.cataclysm.entity.AI.AttackAnimationGoal1;
import L_Ender.cataclysm.entity.AI.AttackMoveGoal;
import L_Ender.cataclysm.entity.AI.SimpleAnimationGoal;
import L_Ender.cataclysm.entity.etc.CMPathNavigateGround;
import L_Ender.cataclysm.entity.etc.SmartBodyHelper2;
import L_Ender.cataclysm.entity.projectile.Ashen_Breath_Entity;
import L_Ender.cataclysm.entity.projectile.Blazing_Bone_Entity;
import L_Ender.cataclysm.init.ModEntities;
import L_Ender.cataclysm.init.ModSounds;
import L_Ender.cataclysm.init.ModTag;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.EnumSet;


public class Ignited_Revenant_Entity extends Boss_monster {

    public static final Animation ASH_BREATH_ATTACK = Animation.create(53);
    public static final Animation BONE_STORM_ATTACK = Animation.create(49);
    public static final int BREATH_COOLDOWN = 200;
    public static final int STORM_COOLDOWN = 200;
    private static final DataParameter<Boolean> ANGER = EntityDataManager.createKey(Ignited_Revenant_Entity.class, DataSerializers.BOOLEAN);
    private float heightOffset = 0.5F;
    private int heightOffsetUpdateTime;
    public float angerProgress;
    public float prevangerProgress;
    private int breath_cooldown = 0;
    private int storm_cooldown = 0;

    public Ignited_Revenant_Entity(EntityType entity, World world) {
        super(entity, world);
        this.experienceValue = 15;
        this.stepHeight = 1.5F;
        this.setPathPriority(PathNodeType.UNPASSABLE_RAIL, 0.0F);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{NO_ANIMATION,ASH_BREATH_ATTACK,BONE_STORM_ATTACK};
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new Ignited_Revenant_Goal());
        this.goalSelector.addGoal(0, new BoneStormGoal(this, BONE_STORM_ATTACK));
        this.goalSelector.addGoal(0, new ShootGoal(this, ASH_BREATH_ATTACK));
        this.goalSelector.addGoal(0, new AttackAnimationGoal1<>(this, ASH_BREATH_ATTACK, 27, true));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static AttributeModifierMap.MutableAttribute ignited_revenant() {
        return MonsterEntity.func_234295_eP_()
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 20.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.28F)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 6)
                .createMutableAttribute(Attributes.MAX_HEALTH, 80)
                .createMutableAttribute(Attributes.ARMOR, 12)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    protected int decreaseAirSupply(int air) {
        return air;
    }


    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEAD;
    }


    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        Entity entity = source.getImmediateSource();
        if (damage > 0.0F && this.canBlockDamageSource(source) && this.getIsAnger() && !EntityTypeTags.getCollection().get(ModTag.TRAP_BLOCK_NOT_DETECTED).contains(entity.getType())) {
            this.damageShield(damage);
            if (!source.isProjectile()) {
                if (entity instanceof LivingEntity) {
                    this.blockUsingShield((LivingEntity) entity);
                }
            }
            this.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 0.3F, 0.5F);
            return false;
        }
        return super.attackEntityFrom(source, damage);
    }

    private boolean canBlockDamageSource(DamageSource damageSourceIn) {
        Entity entity = damageSourceIn.getImmediateSource();
        boolean flag = false;
        if (entity instanceof AbstractArrowEntity) {
            AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity) entity;
            if (abstractarrowentity.getPierceLevel() > 0) {
                flag = true;
            }
        }
        if (!damageSourceIn.isUnblockable()) {
            flag = true;
        }
        return flag;
    }

    @Override
    protected void registerData() {
        super.registerData();
        getDataManager().register(ANGER, false);

    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);

    }


    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
    }

    public void setIsAnger(boolean isAnger) {
        getDataManager().set(ANGER, isAnger);
    }

    public boolean getIsAnger() {
        return getDataManager().get(ANGER);
    }

    public void tick() {
        super.tick();
        AnimationHandler.INSTANCE.updateAnimations(this);
        LivingEntity target = this.getAttackTarget();
        prevangerProgress = angerProgress;
        if (this.getIsAnger() && angerProgress < 5F) {
            angerProgress++;
        }
        if (!this.getIsAnger() && angerProgress > 0F) {
            angerProgress--;
        }
        if (breath_cooldown > 0) breath_cooldown--;
        if (storm_cooldown > 0) storm_cooldown--;
        if (this.isAlive()) {
            if (target != null && target.isAlive()) {
                if (breath_cooldown <= 0 && !isAIDisabled() && this.getAnimation() == NO_ANIMATION && (this.rand.nextInt(35) == 0 && this.getDistance(target) < 4.5F)) {
                    breath_cooldown = BREATH_COOLDOWN;
                    this.setAnimation(ASH_BREATH_ATTACK);
                } else if (storm_cooldown <= 0 && this.getDistance(target) < 6 && !isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.rand.nextInt(15) == 0) {
                    storm_cooldown = STORM_COOLDOWN;
                    this.setAnimation(BONE_STORM_ATTACK);
                }
            }
            if(this.getAnimation() == NO_ANIMATION && this.getIsAnger()){
                if(this.ticksExisted % 6 == 0){
                    for (LivingEntity entity : this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(1.5D))) {
                        if (!isOnSameTeam(entity) && !(entity instanceof Ignited_Revenant_Entity) && entity != this) {
                            boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                            if (flag) {
                                double d0 = entity.getPosX() - this.getPosX();
                                double d1 = entity.getPosZ() - this.getPosZ();
                                double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                                entity.addVelocity(d0 / d2 * 1.5D, 0.2D, d1 / d2 * 1.5D);
                            }
                        }
                    }
                }
            }
        }

    }

    protected SoundEvent getAmbientSound() {
        this.playSound(ModSounds.REVENANT_IDLE.get(), 1.0f, 0.75f);
        return null;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        this.playSound(ModSounds.REVENANT_HURT.get(), 1.0f, 0.75f);
        return null;
    }

    protected SoundEvent getDeathSound() {
        this.playSound(ModSounds.REVENANT_DEATH.get(), 1.0f, 0.75f);
        return null;
    }

    @Override
    protected void onDeathAIUpdate() {
        super.onDeathAIUpdate();

    }

    @Override
    protected BodyController createBodyController() {
        return new SmartBodyHelper2(this);
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new CMPathNavigateGround(this, world);
    }

    @Override
    protected void repelEntities(float x, float y, float z, float radius) {
        super.repelEntities(x, y, z, radius);
    }

    @Override
    public boolean canBePushedByEntity(Entity entity) {
        return false;
    }


    class Ignited_Revenant_Goal extends AttackMoveGoal {


        public Ignited_Revenant_Goal() {
            super(Ignited_Revenant_Entity.this, true, 1.1);
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            Ignited_Revenant_Entity.this.setIsAnger(true);

        }
        @Override
        public void resetTask() {
            super.resetTask();
            Ignited_Revenant_Entity.this.setIsAnger(false);
        }
    }


    class ShootGoal extends SimpleAnimationGoal<Ignited_Revenant_Entity> {

        public ShootGoal(Ignited_Revenant_Entity entity, Animation animation) {
            super(entity, animation);
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
            Ignited_Revenant_Entity.this.setIsAnger(true);

        }
        @Override
        public void resetTask() {
            super.resetTask();
            Ignited_Revenant_Entity.this.setIsAnger(false);
        }

        public void tick() {
            LivingEntity target = Ignited_Revenant_Entity.this.getAttackTarget();

            if (target != null) {
                if (Ignited_Revenant_Entity.this.getAnimationTick() < 27) {
                    Ignited_Revenant_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
                }else{
                    Ignited_Revenant_Entity.this.getLookController().setLookPositionWithEntity(target, 3.0F, 30.0F);
                }
            }

            if (Ignited_Revenant_Entity.this.getAnimationTick() == 21) {
                Ignited_Revenant_Entity.this.playSound(ModSounds.REVENANT_BREATH.get(), 1.0f, 1.0f);

            }
            Vector3d mouthPos = new Vector3d(0, 2.3, 0);
            mouthPos = mouthPos.rotateYaw((float) Math.toRadians(-rotationYaw - 90));
            mouthPos = mouthPos.add(getPositionVec());
            mouthPos = mouthPos.add(new Vector3d(0, 0, 0).rotatePitch((float) Math.toRadians(-rotationPitch)).rotateYaw((float) Math.toRadians(-rotationYawHead)));
            Ashen_Breath_Entity breath = new Ashen_Breath_Entity(ModEntities.ASHEN_BREATH.get(), Ignited_Revenant_Entity.this.world, Ignited_Revenant_Entity.this);
            if (Ignited_Revenant_Entity.this.getAnimationTick() == 27) {
                breath.setPositionAndRotation(mouthPos.x, mouthPos.y, mouthPos.z, Ignited_Revenant_Entity.this.rotationYawHead, Ignited_Revenant_Entity.this.rotationPitch);
                Ignited_Revenant_Entity.this.world.addEntity(breath);
            }

        }
    }

    class BoneStormGoal extends SimpleAnimationGoal<Ignited_Revenant_Entity> {

        public BoneStormGoal(Ignited_Revenant_Entity entity, Animation animation) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
        }

        public void tick() {
            LivingEntity target = entity.getAttackTarget();
            if (target != null) {
                entity.getLookController().setLookPositionWithEntity(target, 3.0F, 30.0F);
            }
            if (entity.getAnimationTick() == 5) {
                switch (rand.nextInt(3)) {
                    case 0: {
                        launchbone1();
                    }
                    break;
                    case 1: {
                        launchbone2();
                    }
                    break;
                    case 2: {
                        launchbone3();
                    }
                    break;
                    default : {
                        break;
                    }
                }

            }
            if(entity.getAnimationTick() == 10){
                switch (rand.nextInt(3)) {
                    case 0: {
                        launchbone1();
                    }
                    break;
                    case 1: {
                        launchbone2();
                    }
                    break;
                    case 2: {
                        launchbone3();
                    }
                    break;
                    default : {
                        break;
                    }
                }
            }
            if(entity.getAnimationTick() == 15){
                switch (rand.nextInt(3)) {
                    case 0: {
                        launchbone1();
                    }
                    break;
                    case 1: {
                        launchbone2();
                    }
                    break;
                    case 2: {
                        launchbone3();
                    }
                    break;
                    default : {
                        break;
                    }
                }
            }
            if(entity.getAnimationTick() == 20){
                switch (rand.nextInt(3)) {
                    case 0: {
                        launchbone1();
                    }
                    break;
                    case 1: {
                        launchbone2();
                    }
                    break;
                    case 2: {
                        launchbone3();
                    }
                    break;
                    default : {
                        break;
                    }
                }
            }
            --entity.heightOffsetUpdateTime;
            if (entity.heightOffsetUpdateTime <= 0) {
                entity.heightOffsetUpdateTime = 100;
                heightOffset = 0.5F + (float)entity.rand.nextGaussian() * 3.0F;
            }

            if (target != null && target.getPosYEye() > entity.getPosYEye() + (double)entity.heightOffset && entity.canAttack(target)) {
                Vector3d vector3d = entity.getMotion();
                entity.setMotion(entity.getMotion().add(0.0D, ((double)0.3F - vector3d.y) * (double)0.3F, 0.0D));
                entity.isAirBorne = true;
            }

        }
    }


    private void launchbone1() {
        this.playSound(SoundEvents.ITEM_TRIDENT_THROW, 1F, 0.75f);
        for (int i = 0; i < 8; i++) {
            float throwAngle = i * 3.14159165F / 4F;

            double sx = this.getPosX() + (MathHelper.cos(throwAngle) * 1);
            double sy = this.getPosY() + (this.getHeight() * 0.62D);
            double sz = this.getPosZ() + (MathHelper.sin(throwAngle) * 1);

            double vx = MathHelper.cos(throwAngle);
            double vy = 0;
            double vz = MathHelper.sin(throwAngle);

            Blazing_Bone_Entity projectile = new Blazing_Bone_Entity(this.world, this);

            projectile.setLocationAndAngles(sx, sy, sz, i * 45F, this.rotationPitch);
            float speed = 0.5F;
            projectile.shoot(vx, vy, vz, speed, 1.0F);
            this.world.addEntity(projectile);
        }

    }

    private void launchbone2() {
        this.playSound(SoundEvents.ITEM_TRIDENT_THROW, 1F, 0.75f);
        for (int i = 0; i < 6; i++) {
            float throwAngle = i * 3.14159165F / 3F;

            double sx = this.getPosX() + (MathHelper.cos(throwAngle) * 1);
            double sy = this.getPosY() + (this.getHeight() * 0.62D);
            double sz = this.getPosZ() + (MathHelper.sin(throwAngle) * 1);

            double vx = MathHelper.cos(throwAngle);
            double vy = 0;
            double vz = MathHelper.sin(throwAngle);

            Blazing_Bone_Entity projectile = new Blazing_Bone_Entity(this.world, this);

            projectile.setLocationAndAngles(sx, sy, sz, i * 45F, this.rotationPitch);
            float speed = 0.6F;
            projectile.shoot(vx, vy, vz, speed, 1.0F);
            this.world.addEntity(projectile);
        }

    }

    private void launchbone3() {
        this.playSound(SoundEvents.ITEM_TRIDENT_THROW, 1F, 0.75f);
        for (int i = 0; i < 10; i++) {
            float throwAngle = i * 3.14159165F / 5F;

            double sx = this.getPosX() + (MathHelper.cos(throwAngle) * 1);
            double sy = this.getPosY() + (this.getHeight() * 0.62D);
            double sz = this.getPosZ() + (MathHelper.sin(throwAngle) * 1);

            double vx = MathHelper.cos(throwAngle);
            double vy = 0;
            double vz = MathHelper.sin(throwAngle);

            Blazing_Bone_Entity projectile = new Blazing_Bone_Entity(this.world, this);

            projectile.setLocationAndAngles(sx, sy, sz, i * 45F, this.rotationPitch);
            float speed = 0.4F;
            projectile.shoot(vx, vy, vz, speed, 1.0F);
            this.world.addEntity(projectile);
        }

    }



}





