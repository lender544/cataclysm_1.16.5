package L_Ender.cataclysm.entity;

import L_Ender.cataclysm.entity.AI.AttackAnimationGoal1;
import L_Ender.cataclysm.entity.AI.AttackMoveGoal;
import L_Ender.cataclysm.entity.AI.SimpleAnimationGoal;
import L_Ender.cataclysm.entity.etc.CMPathNavigateGround;
import L_Ender.cataclysm.entity.etc.SmartBodyHelper2;
import L_Ender.cataclysm.entity.projectile.Ash_Breath_Entity;
import L_Ender.cataclysm.init.ModEntities;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.EnumSet;


public class Ignited_Revenant_Entity extends Boss_monster {

    public static final Animation ASH_BREATH_ATTACK = Animation.create(53);

    private static final DataParameter<Boolean> ANGER = EntityDataManager.createKey(Ignited_Revenant_Entity.class, DataSerializers.BOOLEAN);

    public float angerProgress;
    public float prevangerProgress;

    public Ignited_Revenant_Entity(EntityType entity, World world) {
        super(entity, world);
        this.experienceValue = 15;
        this.stepHeight = 1.5F;
        this.setPathPriority(PathNodeType.UNPASSABLE_RAIL, 0.0F);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{NO_ANIMATION,ASH_BREATH_ATTACK};
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new ChargeGoal());
        this.goalSelector.addGoal(2, new Ignited_Revenant_Goal());
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
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 10)
                .createMutableAttribute(Attributes.MAX_HEALTH, 150)
                .createMutableAttribute(Attributes.ARMOR, 12)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    protected int decreaseAirSupply(int air) {
        return air;
    }


    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        return super.attackEntityFrom(source, damage);
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

    class ChargeGoal extends Goal {
        public ChargeGoal() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        private LivingEntity attackTarget;

        @Override
        public boolean shouldExecute() {
            this.attackTarget = getAttackTarget();
            return this.attackTarget != null
                    && this.attackTarget.isAlive() && Ignited_Revenant_Entity.this.getDistance(attackTarget) <= 4F;
        }

        @Override
        public void startExecuting() {
            Ignited_Revenant_Entity.this.setIsAnger(false);
        }

        @Override
        public void tick() {
            if (angerProgress == 0 && Ignited_Revenant_Entity.this.getAnimation() == NO_ANIMATION ){
                Ignited_Revenant_Entity.this.setAnimation(ASH_BREATH_ATTACK);
            }
        }

        @Override
        public void resetTask() {
            this.attackTarget = null;
        }

    }

    class ShootGoal extends SimpleAnimationGoal<Ignited_Revenant_Entity> {

        public ShootGoal(Ignited_Revenant_Entity entity, Animation animation) {
            super(entity, animation);
        }

        public void tick() {
            LivingEntity target = Ignited_Revenant_Entity.this.getAttackTarget();

            if (target != null) {
                Ignited_Revenant_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            }
            Vector3d mouthPos = new Vector3d(0, 2.3, 0);
            mouthPos = mouthPos.rotateYaw((float) Math.toRadians(-rotationYaw - 90));
            mouthPos = mouthPos.add(getPositionVec());
            mouthPos = mouthPos.add(new Vector3d(0, 0, 0).rotatePitch((float) Math.toRadians(-rotationPitch)).rotateYaw((float) Math.toRadians(-rotationYawHead)));
            Ash_Breath_Entity breath = new Ash_Breath_Entity(ModEntities.ASH_BREATH.get(), Ignited_Revenant_Entity.this.world, Ignited_Revenant_Entity.this);
            if (Ignited_Revenant_Entity.this.getAnimationTick() == 27) {
                breath.setPositionAndRotation(mouthPos.x, mouthPos.y, mouthPos.z, Ignited_Revenant_Entity.this.rotationYawHead, Ignited_Revenant_Entity.this.rotationPitch);
                Ignited_Revenant_Entity.this.world.addEntity(breath);
            }

        }
    }
}





