package L_Ender.cataclysm.entity;

import L_Ender.cataclysm.config.CMConfig;
import L_Ender.cataclysm.entity.AI.AnimationGoal;
import L_Ender.cataclysm.entity.AI.CmAttackGoal;
import L_Ender.cataclysm.entity.AI.SimpleAnimationGoal;
import L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import L_Ender.cataclysm.entity.etc.CMPathNavigateGround;
import L_Ender.cataclysm.entity.etc.SmartBodyHelper2;
import L_Ender.cataclysm.init.ModEffect;
import L_Ender.cataclysm.init.ModSounds;
import L_Ender.cataclysm.init.ModTag;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class Ignis_Entity extends Boss_monster {

    private final ServerBossInfo bossInfo = (ServerBossInfo) (new ServerBossInfo(this.getDisplayName(), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS)).setDarkenSky(false);
    public static final Animation SWING_ATTACK = Animation.create(65);
    public static final Animation HORIZONTAL_SWING_ATTACK = Animation.create(68);
    public static final Animation SHIELD_SMASH_ATTACK = Animation.create(70);
    public static final Animation PHASE_2 = Animation.create(68);
    public static final Animation POKE_ATTACK = Animation.create(65);
    public static final Animation POKE_ATTACK2 = Animation.create(56);
    public static final Animation POKE_ATTACK3 = Animation.create(50);
    public static final Animation POKED_ATTACK = Animation.create(65);
    public static final Animation PHASE_3 = Animation.create(127);
    public static final Animation MAGIC_ATTACK = Animation.create(95);
    public static final Animation SMASH_IN_AIR = Animation.create(105);
    public static final Animation SMASH = Animation.create(47);
    public static final Animation BODY_CHECK_ATTACK1 = Animation.create(62);
    public static final Animation BODY_CHECK_ATTACK2 = Animation.create(62);
    public static final Animation BODY_CHECK_ATTACK3 = Animation.create(62);
    public static final Animation BODY_CHECK_ATTACK4 = Animation.create(62);
    public static final Animation BURNS_THE_EARTH = Animation.create(67);
    public static final Animation TRIPLE_ATTACK = Animation.create(139);
    public static final Animation BREAK_THE_SHIELD = Animation.create(90);
    public static final int BODY_CHECK_COOLDOWN = 200;
    private static final DataParameter<Boolean> IS_BLOCKING = EntityDataManager.createKey(Ignis_Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_SHIELD = EntityDataManager.createKey(Ignis_Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> SHIELD_DURABILITY = EntityDataManager.createKey(Ignis_Entity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> IS_SWORD = EntityDataManager.createKey(Ignis_Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BOSS_PHASE = EntityDataManager.createKey(Ignis_Entity.class, DataSerializers.VARINT);
    private Vector3d prevBladePos = new Vector3d(0, 0, 0);
    private int body_check_cooldown = 0;
    private int timeWithoutTarget;
    public float blockingProgress;
    public float swordProgress;
    public float prevblockingProgress;
    public float prevswordProgress;

    public Ignis_Entity(EntityType entity, World world) {
        super(entity, world);
        this.stepHeight = 3F;
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        this.setPathPriority(PathNodeType.LAVA, 8.0F);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
        if (world.isRemote)
            socketPosArray = new Vector3d[] {new Vector3d(0, 0, 0)};
        setConfigattribute(this, CMConfig.IgnisHealthMultiplier, CMConfig.IgnisDamageMultiplier);
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{
                NO_ANIMATION,
                SWING_ATTACK,
                HORIZONTAL_SWING_ATTACK,
                POKE_ATTACK,
                POKE_ATTACK2,
                POKE_ATTACK3,
                POKED_ATTACK,
                MAGIC_ATTACK,
                PHASE_3,
                SHIELD_SMASH_ATTACK,
                PHASE_2,
                BODY_CHECK_ATTACK4,
                BODY_CHECK_ATTACK3,
                BODY_CHECK_ATTACK2,
                BODY_CHECK_ATTACK1,
                SMASH,
                SMASH_IN_AIR,
                BURNS_THE_EARTH,
                TRIPLE_ATTACK,
                BREAK_THE_SHIELD};
    }
    protected void registerGoals() {
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new CmAttackGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new Hornzontal_Swing(this,HORIZONTAL_SWING_ATTACK));
        this.goalSelector.addGoal(1, new Poke(this));
        this.goalSelector.addGoal(1, new Phase_Transition(this,PHASE_2));
        this.goalSelector.addGoal(1, new Phase_Transition2(this,PHASE_3));
        this.goalSelector.addGoal(1, new Shield_Smash(this,SHIELD_SMASH_ATTACK));
        this.goalSelector.addGoal(1, new Body_Check(this));
        this.goalSelector.addGoal(1, new Poked(this, POKED_ATTACK));
        this.goalSelector.addGoal(1, new Air_Smash(this,SMASH_IN_AIR));
        this.goalSelector.addGoal(1, new SimpleAnimationGoal<>(this, SMASH));
        this.goalSelector.addGoal(1, new Vertical_Swing(this, SWING_ATTACK));
        this.goalSelector.addGoal(1, new Triple_Attack(this, TRIPLE_ATTACK));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));

    }


    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        double range = calculateRange(source);

        if (range > CMConfig.IgnisLongRangelimit * CMConfig.IgnisLongRangelimit) {
            return false;
        }

        if (!source.canHarmInCreative()) {
            damage = Math.min(CMConfig.IgnisDamageCap, damage);
        }

        if (this.getAnimation() == PHASE_2 && !source.canHarmInCreative()) {
            return false;
        }

        Entity entity = source.getImmediateSource();
        if (damage > 0.0F && this.canBlockDamageSource(source)) {
            this.damageShield(damage);

            if (!source.isProjectile()) {
                if (entity instanceof LivingEntity) {
                    this.blockUsingShield((LivingEntity) entity);
                }
            }
            this.playSound(SoundEvents.ENTITY_BLAZE_HURT, 0.5f, 0.4F + this.getRNG().nextFloat() * 0.1F);
            return false;
        }

        return super.attackEntityFrom(source, damage);
    }

    private boolean canBlockDamageSource(DamageSource damageSourceIn) {
        Entity entity = damageSourceIn.getImmediateSource();
        boolean flag = false;
        if (entity instanceof AbstractArrowEntity) {
            AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity)entity;
            if (abstractarrowentity.getPierceLevel() > 0) {
                flag = true;
            }
        }


        if (!damageSourceIn.isUnblockable() && !flag && this.getIsShield()) {
            Vector3d vector3d2 = damageSourceIn.getDamageLocation();
            if (vector3d2 != null) {
                Vector3d vector3d = this.getLook(1.0F);
                Vector3d vector3d1 = vector3d2.subtractReverse(this.getPositionVec()).normalize();
                vector3d1 = new Vector3d(vector3d1.x, 0.0D, vector3d1.z);
                return vector3d1.dotProduct(vector3d) < 0.0D;
            }
        }
        return false;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(IS_BLOCKING, false);
        this.dataManager.register(IS_SHIELD, false);
        this.dataManager.register(BOSS_PHASE, 0);
        this.dataManager.register(IS_SWORD, false);
        this.dataManager.register(SHIELD_DURABILITY, 5);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("BossPhase", this.getBossPhase());
        compound.putInt("Shield_Durability", this.getShieldDurability());
    }


    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setBossPhase(compound.getInt("BossPhase"));
        this.setShieldDurability(compound.getInt("Shield_Durability"));
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    public void setIsBlocking(boolean isBlocking) {
        getDataManager().set(IS_BLOCKING, isBlocking);
    }

    public boolean getIsBlocking() {
        return getDataManager().get(IS_BLOCKING);
    }

    public void setIsShield(boolean isShield) {
        getDataManager().set(IS_SHIELD, isShield);
    }

    public boolean getIsShield() {
        return getDataManager().get(IS_SHIELD);
    }

    public void setIsSword(boolean isSword) {
        getDataManager().set(IS_SWORD, isSword);
    }

    public boolean getIsSword() {
        return getDataManager().get(IS_SWORD);
    }

    public void setShieldDurability(int ShieldDurability) {
        getDataManager().set(SHIELD_DURABILITY, ShieldDurability);
    }

    public int getShieldDurability() {
        return getDataManager().get(SHIELD_DURABILITY);
    }

    public void setBossPhase(int bossPhase) {
        getDataManager().set(BOSS_PHASE, Integer.valueOf(bossPhase));
    }

    public int getBossPhase() {
        return getDataManager().get(BOSS_PHASE).intValue();
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MonsterEntity.func_234295_eP_()
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 50.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.33F)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 14)
                .createMutableAttribute(Attributes.MAX_HEALTH, 333)
                .createMutableAttribute(Attributes.ARMOR, 10)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public float getBrightness() {
        return 1.0F;
    }

    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.IGNIS_AMBIENT.get();
    }

    private static Animation getRandomPoke(Random rand) {
        switch (rand.nextInt(3)) {
            case 0:
                return POKE_ATTACK;
            case 1:
                return POKE_ATTACK2;
            case 2:
                return POKE_ATTACK3;
        }
        return POKE_ATTACK;
    }

    public void tick() {
        if (!this.onGround && this.getMotion().y < 0.0D && this.getAnimation() == NO_ANIMATION && !this.getIsBlocking() && !this.getIsSword()) {
            this.setMotion(this.getMotion().mul(1.0D, 0.6D, 1.0D));
        }

        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
        prevblockingProgress = blockingProgress;
        prevswordProgress = swordProgress;
        if (this.getIsBlocking() && blockingProgress < 10F) {
            blockingProgress++;
        }
        if (!this.getIsBlocking() && blockingProgress > 0F) {
            blockingProgress--;
        }
        if (this.getIsSword() && swordProgress < 10F) {
            swordProgress++;
        }
        if (!this.getIsSword() && swordProgress > 0F) {
            swordProgress--;
        }

        if (!this.getPassengers().isEmpty() && this.getPassengers().get(0).isSneaking()) {
            this.getPassengers().get(0).setSneaking(false);
        }
        LivingEntity target = this.getAttackTarget();
        spawnSwipeParticles();
        if (this.world.isRemote) {
            if (this.rand.nextInt(24) == 0 && !this.isSilent()) {
                this.world.playSound(this.getPosX() + 0.5D, this.getPosY() + 0.5D, this.getPosZ() + 0.5D, SoundEvents.ENTITY_BLAZE_BURN, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
            }

            for(int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }else{
            timeWithoutTarget++;
            if (target != null) {
                timeWithoutTarget = 0;
                if(this.getShieldDurability() < 1) {
                    if (!this.getIsSword()) {
                        this.setIsSword(true);
                    }
                    if (this.getIsBlocking()) {
                        this.setIsBlocking(false);
                    }
                }else{
                    if (!this.getIsBlocking()) {
                        this.setIsBlocking(true);
                    }
                    if (this.getIsSword()) {
                        this.setIsSword(false);
                    }
                }
            }

            if (this.getAnimation() == NO_ANIMATION && timeWithoutTarget > 150 && (this.getIsBlocking() || this.getIsSword()) && target == null) {
                timeWithoutTarget = 0;
                this.setIsSword(false);
                this.setIsBlocking(false);
            }

            if(this.getShieldDurability() < 1) {
                if (this.getIsBlocking()) {
                    this.setIsBlocking(false);
                    this.setIsSword(true);
                }
            }

            if(this.getAnimation() == NO_ANIMATION && this.getShieldDurability() == 1) {
                this.setShieldDurability(0);
            }

            if (this.getBossPhase() > 0){
                bossInfo.setColor(BossInfo.Color.BLUE);
            }

            if (this.getBossPhase() > 1){
                bossInfo.setDarkenSky(true);
                this.setShieldDurability(0);
            }
        }
        if (body_check_cooldown > 0) body_check_cooldown--;


        repelEntities(1.7F, 4, 1.7F, 1.7F);

        rotationYaw = renderYawOffset;
        if (this.isAlive()) {
            if (!isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getHealth() <= this.getMaxHealth() / 2.0F && this.getBossPhase() < 1) {
                this.setAnimation(PHASE_2);
            } else if (target != null && target.isAlive()) {
                if (!isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getDistanceSq(target) >= 64 && this.getDistanceSq(target) <= 1024.0D && this.getRNG().nextFloat() * 100.0F < 3f) {
                    this.setAnimation(SMASH_IN_AIR);
                } else if (!isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getDistance(target) < 7F && this.getRNG().nextFloat() * 100.0F < 6f) {
                    Animation animation = getRandomPoke(rand);
                    this.setAnimation(animation);
                } else if (!isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getDistance(target) < 5F && this.getRNG().nextFloat() * 100.0F < 12f) {
                    if (this.rand.nextInt(3) == 0) {
                        this.setAnimation(HORIZONTAL_SWING_ATTACK);
                    }else{
                        this.setAnimation(SWING_ATTACK);
                    }
                } else if (!isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getDistance(target) < 3F && this.getRNG().nextFloat() * 100.0F < 20f && this.getIsShield() ) {
                    this.setAnimation(SHIELD_SMASH_ATTACK);

                } else if (!isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getDistance(target) < 3F && this.getRNG().nextFloat() * 100.0F < 10f && body_check_cooldown <= 0) {
                    body_check_cooldown = BODY_CHECK_COOLDOWN;
                    this.setAnimation(BODY_CHECK_ATTACK1);
                }
            }
            //if (!isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getShieldDurability() == 2 && blockingProgress == 10) {
          //      this.setAnimation(BREAK_THE_SHIELD);
           // }else if (!isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getHealth() <= this.getMaxHealth() / 2.0F && this.getBossPhase() < 1) {
           //     this.setAnimation(PHASE_2);
           // }else if (!isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getHealth() <= this.getMaxHealth() / 4.0F && this.getBossPhase() < 2) {
            //    this.setAnimation(PHASE_3);
        }

        super.tick();
        AnimationHandler.INSTANCE.updateAnimations(this);

        if(this.getIsBlocking() && blockingProgress == 10){
            if(this.getAnimation() == NO_ANIMATION) {
                setIsShield(true);
            }
            else if(this.getAnimation() == POKED_ATTACK) {
                setIsShield(false);
            }
            else if (this.getAnimation() == HORIZONTAL_SWING_ATTACK) {
                setIsShield(this.getAnimationTick() > 31);
            }
            else if (this.getAnimation() == BODY_CHECK_ATTACK1 || this.getAnimation() == BODY_CHECK_ATTACK2 ||
                    this.getAnimation() == BODY_CHECK_ATTACK3 || this.getAnimation() == BODY_CHECK_ATTACK4) {
                setIsShield(this.getAnimationTick() < 25);
            }
            else if(this.getAnimation() == POKE_ATTACK) {
                setIsShield(this.getAnimationTick() < 39);
            }
            else if(this.getAnimation() == POKE_ATTACK2) {
                setIsShield(this.getAnimationTick() < 34);
            }
            else if(this.getAnimation() == POKE_ATTACK3) {
                setIsShield(this.getAnimationTick() < 29);
            }
            else if (this.getAnimation() == SWING_ATTACK) {
                setIsShield(this.getAnimationTick() < 34);
            }
        }else{
            setIsShield(false);
        }

    }

    public void livingTick() {
        super.livingTick();
        if (this.getAnimation() == SWING_ATTACK) {
            if (this.getAnimationTick() == 34) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(5.5f,6,60,1.2f,0.08f,100,5 ,150);
            }
        }
        if (this.getAnimation() == HORIZONTAL_SWING_ATTACK) {
            if (this.getAnimationTick() == 31) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(5.5f,6,170,1.1f,0.06f,80,3 ,150);
            }
        }
        if (this.getAnimation() == PHASE_2) {
            if (this.getAnimationTick() == 29){
                this.playSound(ModSounds.FLAME_BURST.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
            }
            if (this.getAnimationTick() > 29 && this.getAnimationTick() < 39){
                Sphereparticle(2, 0,5);
                Phase_Transition(0.4f,0.03f,5,150);
            }
            if (this.getAnimationTick() == 34) {
                setBossPhase(1);
            }
        }
        if (this.getAnimation() == PHASE_3) {
            if (this.getAnimationTick() == 58){
                this.setBossPhase(2);
                this.playSound(ModSounds.FLAME_BURST.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                this.playSound(ModSounds.SWORD_STOMP.get(), 1.0f, 0.75F + this.getRNG().nextFloat() * 0.1F);
                ScreenShake_Entity.ScreenShake(world, this.getPositionVec(), 30, 0.15f, 0, 50);
                ShieldSmashparticle(0.5f,1.0f,-0.15f);
            }
            if (this.getAnimationTick() > 58 && this.getAnimationTick() < 68){
                Sphereparticle(0.5f,1.0f,6);
                Phase_Transition(0.6f,0.05f,5,150);
            }
        }

        if (this.getAnimation() == SHIELD_SMASH_ATTACK) {
            if (this.getAnimationTick() == 34){
                this.playSound(SoundEvents.ITEM_TOTEM_USE, 1.5f, 0.8F + this.getRNG().nextFloat() * 0.1F);
                ScreenShake_Entity.ScreenShake(world, this.getPositionVec(), 20, 0.3f, 0, 20);
                AreaAttack(4.85f,2.5f,45,1.5f,0.15f,200,0,0);
                ShieldSmashDamage(4,2.75f);
                ShieldSmashparticle(1.3f,2.75f,-0.1f);
            }
            if (this.getAnimationTick() == 37) {
                ShieldSmashDamage(5,2.75f);
            }
            if (this.getAnimationTick() == 40) {
                ShieldSmashDamage(6,2.75f);
            }
        }
        if (this.getAnimation() == SMASH) {
            if (this.getAnimationTick() == 5){
                this.playSound(SoundEvents.ITEM_TOTEM_USE, 1.5f, 0.8F + this.getRNG().nextFloat() * 0.1F);
                ScreenShake_Entity.ScreenShake(world, this.getPositionVec(), 20, 0.3f, 0, 20);
                AreaAttack(4.85f,2.5f,45,1.5f,0.15f,200,0,0);
                ShieldSmashDamage(3,1.5f);
                ShieldSmashparticle(1.3f,1.5f,0.0f);
            }
            if (this.getAnimationTick() == 8) {
                ShieldSmashDamage(4,1.5f);
            }
            if (this.getAnimationTick() == 11) {
                ShieldSmashDamage(5,1.5f);
            }
            if (this.getAnimationTick() == 14) {
                ShieldSmashDamage(6,1.5f);
            }
        }

        if (this.getAnimation() == POKE_ATTACK) {
            if (this.getAnimationTick() == 39) {
                Poke(7, 70,60);
            }
        }

        if (this.getAnimation() == POKE_ATTACK2) {
            if (this.getAnimationTick() == 34) {
                Poke(7, 65,50);
            }
        }

        if (this.getAnimation() == POKE_ATTACK3) {
            if (this.getAnimationTick() == 29) {
                Poke(7, 60,40);
            }
        }

        if (this.getAnimation() == BODY_CHECK_ATTACK1
                || this.getAnimation() == BODY_CHECK_ATTACK2
                || this.getAnimation() == BODY_CHECK_ATTACK3
                || this.getAnimation() == BODY_CHECK_ATTACK4) {
            if (this.getAnimationTick() == 25) {
                BodyCheckAttack(3.0f,6,120,0.8f,0.03f,40,80);
            }

        }
        if (this.getAnimation() == TRIPLE_ATTACK) {
            if (this.getAnimationTick() == 30) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(5.5f,6,100,1.0f,0.05f,80,3 ,150);
            }
            if (this.getAnimationTick() == 73) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(5.5f,6,45,1.1f,0.06f,120,5 ,150);
            }

            if (this.getAnimationTick() == 108) {
                BodyCheckAttack(4.85f,6,60,1.0f,0.01f,40,0);
            }
        }
    }

    private void AreaAttack(float range,float height,float arc ,float damage, float hpdamage ,int shieldbreakticks, int firetime, int brandticks) {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getPosZ() - this.getPosZ(), entityHit.getPosX() - this.getPosX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = this.renderYawOffset % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getPosZ() - this.getPosZ()) * (entityHit.getPosZ() - this.getPosZ()) + (entityHit.getPosX() - this.getPosX()) * (entityHit.getPosX() - this.getPosX()));
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                if (!(entityHit instanceof Ignis_Entity)) {
                    boolean flag = entityHit.attackEntityFrom(DamageSource.causeMobDamage(this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damage + entityHit.getMaxHealth() * hpdamage));
                    if (entityHit instanceof PlayerEntity && entityHit.isActiveItemStackBlocking() && shieldbreakticks > 0) {
                        disableShield(entityHit, shieldbreakticks);
                    }
                    if (flag) {
                        entityHit.setFire(firetime);
                        if (brandticks > 0) {
                            EffectInstance effectinstance1 = entityHit.getActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                            int i = 1;
                            if (effectinstance1 != null) {
                                i += effectinstance1.getAmplifier();
                                entityHit.removeActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                            } else {
                                --i;
                            }

                            i = MathHelper.clamp(i, 0, 4);
                            EffectInstance effectinstance = new EffectInstance(ModEffect.EFFECTBLAZING_BRAND.get(), brandticks, i, false, false, true);
                            entityHit.addPotionEffect(effectinstance);
                            this.heal(6 * (i + 1));
                        }
                    }
                }
            }
        }
    }

    private void BodyCheckAttack(float range, float height, float arc, float damage, float hpdamage, int shieldbreakticks, int slowticks) {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getPosZ() - this.getPosZ(), entityHit.getPosX() - this.getPosX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = this.renderYawOffset % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getPosZ() - this.getPosZ()) * (entityHit.getPosZ() - this.getPosZ()) + (entityHit.getPosX() - this.getPosX()) * (entityHit.getPosX() - this.getPosX()));
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                if (!isOnSameTeam(entityHit) && !(entityHit instanceof Ignis_Entity)) {
                    boolean flag = entityHit.attackEntityFrom(DamageSource.causeMobDamage(this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damage + entityHit.getMaxHealth() * hpdamage));
                    if (entityHit instanceof PlayerEntity) {
                        if (entityHit.isActiveItemStackBlocking() && shieldbreakticks > 0) {
                            disableShield(entityHit, shieldbreakticks);
                        }
                    }
                    double d0 = entityHit.getPosX() - this.getPosX();
                    double d1 = entityHit.getPosZ() - this.getPosZ();
                    double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                    entityHit.addVelocity(d0 / d2 * 2.5D, 0.2D, d1 / d2 * 2.5D);
                    if (flag) {
                        this.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.5f, 0.8F + this.getRNG().nextFloat() * 0.1F);
                        if (slowticks > 0){
                            entityHit.addPotionEffect(new EffectInstance(ModEffect.EFFECTSTUN.get(), slowticks, 1,false, false, true));

                        }
                    }
                }
            }
        }
    }

    private void Poke(float range, float arc, int shieldbreakticks){
        LivingEntity target = this.getAttackTarget();
        if (target != null) {
            float entityHitAngle = (float) ((Math.atan2(target.getPosZ() - this.getPosZ(), target.getPosX() - this.getPosX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = this.renderYawOffset % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            if (this.getDistance(target) <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                boolean flag = target.attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + target.getMaxHealth() * 0.1f);
                if (target instanceof PlayerEntity) {
                    if (target.isActiveItemStackBlocking() && shieldbreakticks > 0) {
                        disableShield(target, shieldbreakticks);
                    }
                }
                if (flag && !EntityTypeTags.getCollection().get(ModTag.IGNIS_CANT_POKE).contains(target.getType())) {
                    if(target.isSneaking()) {
                        target.setSneaking(false);
                    }
                    target.startRiding(this, true);
                    AnimationHandler.INSTANCE.sendAnimationMessage(this, POKED_ATTACK);
                }
            }
        }
    }

    private void Flameswing(){
        Vector3d bladePos = socketPosArray[0];
        int snowflakeDensity = 4;
        float snowflakeRandomness = 0.5f;
        double length = prevBladePos.subtract(bladePos).length();
        int numClouds = (int) Math.floor(2 * length);
        for (int i = 0; i < numClouds; i++) {
            double x = prevBladePos.x + i * (bladePos.x - prevBladePos.x) / numClouds;
            double y = prevBladePos.y + i * (bladePos.y - prevBladePos.y) / numClouds;
            double z = prevBladePos.z + i * (bladePos.z - prevBladePos.z) / numClouds;
            for (int j = 0; j < snowflakeDensity; j++) {
                float xOffset = snowflakeRandomness * (2 * rand.nextFloat() - 1);
                float yOffset = snowflakeRandomness * (2 * rand.nextFloat() - 1);
                float zOffset = snowflakeRandomness * (2 * rand.nextFloat() - 1);
                if (this.getBossPhase() > 0) {
                    world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x + xOffset, y + yOffset, z + zOffset, 0, 0, 0);
                }else{
                    world.addParticle(ParticleTypes.FLAME, x + xOffset, y + yOffset, z + zOffset, 0, 0, 0);
                }
            }
        }
    }

    private void spawnSwipeParticles() {
        if (world.isRemote) {
            Vector3d bladePos = socketPosArray[0];
            if(this.getAnimation() == HORIZONTAL_SWING_ATTACK) {
                if (this.getAnimationTick() > 27 && this.getAnimationTick() < 33) {
                    Flameswing();
                }
            }
            if(this.getAnimation() == SWING_ATTACK) {
                if (this.getAnimationTick() > 25 && this.getAnimationTick() < 37) {
                    Flameswing();
                }
            }
            if(this.getAnimation() == BURNS_THE_EARTH) {
                if (this.getAnimationTick() > 35 && this.getAnimationTick() < 52) {
                    Flameswing();
                }
            }
            if(this.getAnimation() == TRIPLE_ATTACK) {
                Flameswing();
            }
            prevBladePos = bladePos;
        }
    }

    private void ShieldSmashparticle(float radius,float vec, float math) {
        if (this.world.isRemote) {
            for (int i1 = 0; i1 < 80 + rand.nextInt(12); i1++) {
                double motionX = getRNG().nextGaussian() * 0.07D;
                double motionY = getRNG().nextGaussian() * 0.07D;
                double motionZ = getRNG().nextGaussian() * 0.07D;
                float angle = (0.01745329251F * this.renderYawOffset) + i1;
                float f = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)) ;
                float f1 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)) ;
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraY = 0.3F;
                double extraZ = radius * MathHelper.cos(angle);
                double theta = (renderYawOffset) * (Math.PI / 180);
                theta += Math.PI / 2;
                double vecX = Math.cos(theta);
                double vecZ = Math.sin(theta);
                int hitX = MathHelper.floor(getPosX() + vec * vecX+ extraX);
                int hitY = MathHelper.floor(getPosY());
                int hitZ = MathHelper.floor(getPosZ() + vec * vecZ + extraZ);
                BlockPos hit = new BlockPos(hitX, hitY, hitZ);
                BlockState block = world.getBlockState(hit.down());
                this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, block), getPosX() + vec * vecX + extraX + f * math, this.getPosY() + extraY, getPosZ() + vec * vecZ + extraZ + f1 * math, motionX, motionY, motionZ);

            }
        }
    }

    private void ShieldSmashDamage(int distance, float vec) {
            double perpFacing = this.renderYawOffset * (Math.PI / 180);
            double facingAngle = perpFacing + Math.PI / 2;
            int hitY = MathHelper.floor(this.getBoundingBox().minY - 0.5);
            double spread = Math.PI * 2;
            int arcLen = MathHelper.ceil(distance * spread);
            double minY = this.getPosY() - 1;
            double maxY = this.getPosY() + 1.5;
            for (int i = 0; i < arcLen; i++) {
                double theta = (i / (arcLen - 1.0) - 0.5) * spread + facingAngle;
                double vecX = Math.cos(theta);
                double vecZ = Math.sin(theta);
                double px = this.getPosX() + vecX * distance + vec * Math.cos((renderYawOffset + 90) * Math.PI / 180);
                double pz = this.getPosZ() + vecZ * distance + vec * Math.sin((renderYawOffset + 90) * Math.PI / 180);
                if (ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
                    int hitX = MathHelper.floor(px);
                    int hitZ = MathHelper.floor(pz);
                    BlockPos pos = new BlockPos(hitX, hitY, hitZ);
                    BlockPos abovePos = new BlockPos(pos).up();
                    BlockState block = world.getBlockState(pos);
                    BlockState blockAbove = world.getBlockState(abovePos);
                    if (block.getMaterial() != Material.AIR && !block.getBlock().hasTileEntity(block) && !blockAbove.getMaterial().blocksMovement() && !BlockTags.getCollection().get(ModTag.NETHERITE_MONSTROSITY_IMMUNE).contains(block.getBlock())) {
                        FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(world, hitX + 0.5D, hitY + 0.5D, hitZ + 0.5D, block);
                        fallingBlockEntity.addVelocity(0, 0.2D + getRNG().nextGaussian() * 0.15D, 0);
                        world.addEntity(fallingBlockEntity);
                    }
                }
                AxisAlignedBB selection = new AxisAlignedBB(px - 0.5, minY, pz - 0.5, px + 0.5, maxY, pz + 0.5);
                List<LivingEntity> hit = world.getEntitiesWithinAABB(LivingEntity.class, selection);
                for (LivingEntity entity : hit) {
                    if (!isOnSameTeam(entity) && !(entity instanceof Ignis_Entity) && entity != this) {
                        boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
                        if (flag) {
                            double airborne = 0.1 * distance + world.rand.nextDouble() * 0.15;
                            entity.setMotion(entity.getMotion().add(0.0D, airborne, 0.0D));
                        }
                    }
                }

            }
    }



    private void Sphereparticle(float height, float vec, float size) {
        if (this.world.isRemote) {
            if (this.ticksExisted % 2 == 0) {
                double d0 = this.getPosX();
                double d1 = this.getPosY() + height;
                double d2 = this.getPosZ();
                double theta = (renderYawOffset) * (Math.PI / 180);
                theta += Math.PI / 2;
                double vecX = Math.cos(theta);
                double vecZ = Math.sin(theta);
                for (float i = -size; i <= size; ++i) {
                    for (float j = -size; j <= size; ++j) {
                        for (float k = -size; k <= size; ++k) {
                            double d3 = (double) j + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                            double d4 = (double) i + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                            double d5 = (double) k + (this.rand.nextDouble() - this.rand.nextDouble()) * 0.5D;
                            double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5) / 0.5 + this.rand.nextGaussian() * 0.05D;
                            if (this.getBossPhase() == 0) {
                                this.world.addParticle(ParticleTypes.FLAME, d0 + vec * vecX, d1, d2 + vec * vecZ, d3 / d6, d4 / d6, d5 / d6);
                            } else {
                                this.world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d0 + vec * vecX, d1, d2 + vec * vecZ, d3 / d6, d4 / d6, d5 / d6);
                            }
                            if (i != -size && i != size && j != -size && j != size) {
                                k += size * 2 - 1;
                            }
                        }
                    }
                }
            }
        }
    }

    private void Phase_Transition(float damage,float hpdamage, int firetime, int brandticks) {
        if (this.getAnimationTick() % 2 == 0) {
            int distance = this.getAnimationTick() / 2 - 27;
            List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(distance, distance, distance, distance);
            for (LivingEntity entityHit : entitiesHit) {
                if (!isOnSameTeam(entityHit) && !(entityHit instanceof Ignis_Entity) && entityHit != this) {
                    boolean flag = entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this,this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damage + hpdamage));
                    if (flag) {
                        entityHit.setFire(firetime);
                        if (brandticks > 0){
                            EffectInstance effectinstance1 = entityHit.getActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                            int i = 1;
                            if (effectinstance1 != null) {
                                i += effectinstance1.getAmplifier();
                                entityHit.removeActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                            } else {
                                --i;
                            }

                            i = MathHelper.clamp(i, 0, 4);
                            EffectInstance effectinstance = new EffectInstance(ModEffect.EFFECTBLAZING_BRAND.get(), brandticks, i, false, false, true);
                            entityHit.addPotionEffect(effectinstance);
                        }
                    }
                }
            }
        }
    }

    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (isPassenger(passenger)) {
            int tick = 5;
            if (this.getAnimation() == POKED_ATTACK) {
                tick = this.getAnimationTick();
                if(this.getAnimationTick() == 46) {
                    passenger.stopRiding();
                }
                Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
                this.renderYawOffset = this.rotationYaw;
                this.rotationYawHead = this.rotationYaw;
            }
            float radius = 4F;
            float angle = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            double extraY = tick < 10 ? 0 : 0.2F * MathHelper.clamp(tick - 10, 0, 15);
            passenger.setPosition(this.getPosX() + extraX, this.getPosY() + extraY + 1.2F, this.getPosZ() + extraZ);
            if ((tick - 10) % 4 == 0) {
                //this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                LivingEntity target = this.getAttackTarget();
                if (target != null) {
                    if(passenger == target) {
                        boolean flag = target.attackEntityFrom(DamageSource.causeMobDamage(this), 4 + target.getMaxHealth() * 0.02f);
                        if (flag) {
                            EffectInstance effectinstance1 = target.getActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                            int i = 1;
                            if (effectinstance1 != null) {
                                i += effectinstance1.getAmplifier();
                                target.removeActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                            } else {
                                --i;
                            }

                            i = MathHelper.clamp(i, 0, 4);
                            EffectInstance effectinstance = new EffectInstance(ModEffect.EFFECTBLAZING_BRAND.get(), 150, i, false, false, true);
                            target.addPotionEffect(effectinstance);
                            this.heal(2 * (i + 1));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    protected void repelEntities(float x, float y, float z, float radius) {
        super.repelEntities(x, y, z, radius);
    }

    @Override
    public boolean canBePushedByEntity(Entity entity) {
        return false;
    }


    public void travel(Vector3d travelVector) {
        this.setAIMoveSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (isInLava() ? 0.2F : 1F));
        if (this.getAnimation() == POKED_ATTACK) {
            if (Ignis_Entity.this.getNavigator().getPath() != null) {
                Ignis_Entity.this.getNavigator().clearPath();
            }
            travelVector = Vector3d.ZERO;
            super.travel(travelVector);
            return;
        }
        super.travel(travelVector);
    }

    @Override
    protected BodyController createBodyController() {
        return new SmartBodyHelper2(this);
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new CMPathNavigateGround(this, world);
    }

    @Override
    public void addTrackingPlayer(ServerPlayerEntity player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(ServerPlayerEntity player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    private boolean shouldFollowUp(float Range) {
        LivingEntity target = this.getAttackTarget();
        if (target != null && target.isAlive()) {
            Vector3d targetMoveVec = target.getMotion();
            Vector3d betweenEntitiesVec = this.getPositionVec().subtract(target.getPositionVec());
            boolean targetComingCloser = targetMoveVec.dotProduct(betweenEntitiesVec) > 0;
            return this.getDistance(target) < Range || (this.getDistance(target) < 5 + Range && targetComingCloser);
        }
        return false;
    }

    class Hornzontal_Swing extends SimpleAnimationGoal<Ignis_Entity> {

        public Hornzontal_Swing(Ignis_Entity entity, Animation animation) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (Ignis_Entity.this.getAnimationTick() < 31 && target != null || Ignis_Entity.this.getAnimationTick() > 51 && target != null) {
                Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            }else{
                Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
            }
            if (Ignis_Entity.this.getAnimationTick() == 26) {
                float f1 = (float) Math.cos(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
                float f2 = (float) Math.sin(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
                if (target != null) {
                    float r = Ignis_Entity.this.getDistance(target);
                    r = MathHelper.clamp(r, 0, 6.5f);
                    Ignis_Entity.this.addVelocity(f1 * 0.3 * r, 0, f2 * 0.3 * r);
                } else {
                    Ignis_Entity.this.addVelocity(f1 * 1.5, 0, f2 * 1.5);
                }
            }
            if (Ignis_Entity.this.getAnimationTick() == 36 && shouldFollowUp(3.5f) && Ignis_Entity.this.rand.nextInt(3) == 0 && body_check_cooldown <= 0) {
                body_check_cooldown = BODY_CHECK_COOLDOWN;
                AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, BODY_CHECK_ATTACK2);
            }
            if(Ignis_Entity.this.getAnimationTick() > 32 || Ignis_Entity.this.getAnimationTick() < 26){
                Ignis_Entity.this.setMotion(0, Ignis_Entity.this.getMotion().y, 0);
            }
        }
    }


    class Poke extends AnimationGoal<Ignis_Entity> {

        public Poke(Ignis_Entity entity) {
            super(entity);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        @Override
        protected boolean test(Animation animation) {
            return Ignis_Entity.this.getAnimation() == POKE_ATTACK || Ignis_Entity.this.getAnimation() == POKE_ATTACK2 || Ignis_Entity.this.getAnimation() == POKE_ATTACK3;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if(Ignis_Entity.this.getAnimation() == POKE_ATTACK) {
                if (Ignis_Entity.this.getAnimationTick() < 39 && target != null || Ignis_Entity.this.getAnimationTick() > 59 && target != null) {
                    Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
                } else {
                    Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
                }
                if (Ignis_Entity.this.getAnimationTick() == 34) {
                    float f1 = (float) Math.cos(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
                    float f2 = (float) Math.sin(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
                    if (target != null) {
                        float r = Ignis_Entity.this.getDistance(target);
                        r = MathHelper.clamp(r, 0, 15f);
                        Ignis_Entity.this.addVelocity(f1 * 0.3 * r, 0, f2 * 0.3 * r);
                    } else {
                        Ignis_Entity.this.addVelocity(f1 * 1.5, 0, f2 * 1.5);
                    }
                }
                if (Ignis_Entity.this.getAnimationTick() == 42 && shouldFollowUp(3.0f) && Ignis_Entity.this.rand.nextInt(2) == 0 && body_check_cooldown <= 0) {
                    body_check_cooldown = BODY_CHECK_COOLDOWN;
                    AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, BODY_CHECK_ATTACK4);
                }

                if (Ignis_Entity.this.getAnimationTick() > 40 || Ignis_Entity.this.getAnimationTick() < 34) {
                    Ignis_Entity.this.setMotion(0, Ignis_Entity.this.getMotion().y, 0);
                }
            }
            if(Ignis_Entity.this.getAnimation() == POKE_ATTACK2) {
                if (Ignis_Entity.this.getAnimationTick() < 33 && target != null || Ignis_Entity.this.getAnimationTick() > 53 && target != null) {
                    Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
                } else {
                    Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
                }
                if (Ignis_Entity.this.getAnimationTick() == 28) {
                    float f1 = (float) Math.cos(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
                    float f2 = (float) Math.sin(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
                    if (target != null) {
                        float r = Ignis_Entity.this.getDistance(target);
                        r = MathHelper.clamp(r, 0, 15f);
                        Ignis_Entity.this.addVelocity(f1 * 0.3 * r, 0, f2 * 0.3 * r);
                    } else {
                        Ignis_Entity.this.addVelocity(f1 * 1.5, 0, f2 * 1.5);
                    }
                }
                if (Ignis_Entity.this.getAnimationTick() == 36 && shouldFollowUp(3.0f) && Ignis_Entity.this.rand.nextInt(2) == 0 && body_check_cooldown <= 0) {
                    body_check_cooldown = BODY_CHECK_COOLDOWN;
                    AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, BODY_CHECK_ATTACK4);
                }

                if (Ignis_Entity.this.getAnimationTick() > 34 || Ignis_Entity.this.getAnimationTick() < 28) {
                    Ignis_Entity.this.setMotion(0, Ignis_Entity.this.getMotion().y, 0);
                }
            }
            if(Ignis_Entity.this.getAnimation() == POKE_ATTACK3) {
                if (Ignis_Entity.this.getAnimationTick() < 29 && target != null) {
                    Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
                } else {
                    Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
                }
                if (Ignis_Entity.this.getAnimationTick() == 24) {
                    float f1 = (float) Math.cos(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
                    float f2 = (float) Math.sin(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
                    if (target != null) {
                        float r = Ignis_Entity.this.getDistance(target);
                        r = MathHelper.clamp(r, 0, 15f);
                        Ignis_Entity.this.addVelocity(f1 * 0.3 * r, 0, f2 * 0.3 * r);
                    } else {
                        Ignis_Entity.this.addVelocity(f1 * 1.5, 0, f2 * 1.5);
                    }
                }
                if (Ignis_Entity.this.getAnimationTick() == 33 && shouldFollowUp(3.0f) && Ignis_Entity.this.rand.nextInt(2) == 0 && body_check_cooldown <= 0) {
                    body_check_cooldown = BODY_CHECK_COOLDOWN;
                    AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, BODY_CHECK_ATTACK4);
                }

                if (Ignis_Entity.this.getAnimationTick() > 30 || Ignis_Entity.this.getAnimationTick() < 24) {
                    Ignis_Entity.this.setMotion(0, Ignis_Entity.this.getMotion().y, 0);
                }
            }
        }
    }

    class Poked extends SimpleAnimationGoal<Ignis_Entity> {

        public Poked(Ignis_Entity entity, Animation animation) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (target != null) {
                Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 20.0F, 20.0F);
            }
            Ignis_Entity.this.setMotion(0, Ignis_Entity.this.getMotion().y, 0);
        }
    }

    class Phase_Transition extends SimpleAnimationGoal<Ignis_Entity> {

        public Phase_Transition(Ignis_Entity entity, Animation animation) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (Ignis_Entity.this.getAnimationTick() < 34 && target != null || Ignis_Entity.this.getAnimationTick() > 54 && target != null) {
                Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
            }
            Ignis_Entity.this.setMotion(0, Ignis_Entity.this.getMotion().y, 0);
        }
    }

    class Phase_Transition2 extends SimpleAnimationGoal<Ignis_Entity> {

        public Phase_Transition2(Ignis_Entity entity, Animation animation) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (Ignis_Entity.this.getAnimationTick() < 34 && target != null) {
                Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
            }
            Ignis_Entity.this.setMotion(0, Ignis_Entity.this.getMotion().y, 0);
        }
    }

    class Shield_Smash extends SimpleAnimationGoal<Ignis_Entity> {

        public Shield_Smash(Ignis_Entity entity, Animation animation) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (Ignis_Entity.this.getAnimationTick() < 34 && target != null) {
                Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
            }
            Ignis_Entity.this.setMotion(0, Ignis_Entity.this.getMotion().y, 0);

            if (Ignis_Entity.this.getAnimationTick() == 45 && shouldFollowUp(4.0f) && Ignis_Entity.this.rand.nextInt(3) == 0 && body_check_cooldown <= 0) {
                body_check_cooldown = BODY_CHECK_COOLDOWN;
                AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, BODY_CHECK_ATTACK3);
            }

        }
    }

    class Air_Smash extends SimpleAnimationGoal<Ignis_Entity> {

        public Air_Smash(Ignis_Entity entity, Animation animation) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (target != null) {
                Ignis_Entity.this.faceEntity(target, 30.0F, 30.0F);
            }
            if (Ignis_Entity.this.getAnimationTick() == 19) {
                if (target != null) {
                    Ignis_Entity.this.setMotion((target.getPosX() - Ignis_Entity.this.getPosX()) * 0.2D, 1.4D, (target.getPosZ() - Ignis_Entity.this.getPosZ()) * 0.2D);
                }else{
                    Ignis_Entity.this.setMotion(0, 1.4D, 0);
                }
            }

            if (Ignis_Entity.this.getAnimationTick() > 19 && Ignis_Entity.this.isOnGround()){
                AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, SMASH);
            }

        }
    }

    class Body_Check extends AnimationGoal<Ignis_Entity> {

        public Body_Check(Ignis_Entity entity) {
            super(entity);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        @Override
        protected boolean test(Animation animation) {
            return Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK1
                    || Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK2
                    || Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK3
                    || Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK4;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (Ignis_Entity.this.getAnimationTick() < 25 && target != null) {
                Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
            }
            if (Ignis_Entity.this.getAnimationTick() == 20 && target != null){
                Ignis_Entity.this.setMotion((target.getPosX() - Ignis_Entity.this.getPosX()) * 0.25D, 0, (target.getPosZ() - Ignis_Entity.this.getPosZ()) * 0.25D);
            }
        }
    }

    class Vertical_Swing extends SimpleAnimationGoal<Ignis_Entity> {

        public Vertical_Swing(Ignis_Entity entity, Animation animation) {
            super(entity, animation);

        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (Ignis_Entity.this.getAnimationTick() < 34 && target != null) {
                Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
            }
        }
    }


    class Triple_Attack extends SimpleAnimationGoal<Ignis_Entity>  {

        public Triple_Attack(Ignis_Entity entity, Animation animation) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (Ignis_Entity.this.getAnimationTick() < 30 && target != null
                    || Ignis_Entity.this.getAnimationTick() < 69 && Ignis_Entity.this.getAnimationTick() > 42 && target != null
                    || Ignis_Entity.this.getAnimationTick() < 108 && Ignis_Entity.this.getAnimationTick() > 84 && target != null
                    || Ignis_Entity.this.getAnimationTick() > 124 && target != null) {
                Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
            }
            if (Ignis_Entity.this.getAnimationTick() == 27 || Ignis_Entity.this.getAnimationTick() == 105) {
                float f1 = (float) Math.cos(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
                float f2 = (float) Math.sin(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
                if(target != null) {
                    if (Ignis_Entity.this.getDistance(target) > 3.5F) {
                        Ignis_Entity.this.addVelocity(f1 * 1.5, 0, f2 * 1.5);
                    }
                }else{
                    Ignis_Entity.this.addVelocity(f1 * 1.5, 0, f2 * 1.5);
                }
            }
        }
    }
}

