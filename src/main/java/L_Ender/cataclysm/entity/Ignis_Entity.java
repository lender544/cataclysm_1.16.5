package L_Ender.cataclysm.entity;

import L_Ender.cataclysm.config.CMConfig;
import L_Ender.cataclysm.entity.AI.*;
import L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import L_Ender.cataclysm.entity.etc.CMPathNavigateGround;
import L_Ender.cataclysm.entity.etc.SmartBodyHelper2;
import L_Ender.cataclysm.init.ModEffect;
import L_Ender.cataclysm.init.ModParticle;
import L_Ender.cataclysm.init.ModSounds;
import L_Ender.cataclysm.init.ModTag;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.google.common.collect.ImmutableList;
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
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
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
import net.minecraft.world.*;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Ignis_Entity extends Boss_monster {

    private final ServerBossInfo bossInfo = (ServerBossInfo) (new ServerBossInfo(this.getDisplayName(), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS)).setDarkenSky(false);
    public static final Animation SWING_ATTACK = Animation.create(65);
    public static final Animation SWING_ATTACK_SOUL = Animation.create(56);
    public static final Animation HORIZONTAL_SWING_ATTACK = Animation.create(68);
    public static final Animation HORIZONTAL_SWING_ATTACK_SOUL = Animation.create(58);
    public static final Animation SHIELD_SMASH_ATTACK = Animation.create(70);
    public static final Animation PHASE_2 = Animation.create(68);
    public static final Animation POKE_ATTACK = Animation.create(65);
    public static final Animation POKE_ATTACK2 = Animation.create(56);
    public static final Animation POKE_ATTACK3 = Animation.create(50);
    public static final Animation POKED_ATTACK = Animation.create(65);
    public static final Animation PHASE_3 = Animation.create(120);
    public static final Animation MAGIC_ATTACK = Animation.create(95);
    public static final Animation SMASH_IN_AIR = Animation.create(105);
    public static final Animation SMASH = Animation.create(47);
    public static final Animation BODY_CHECK_ATTACK1 = Animation.create(62);
    public static final Animation BODY_CHECK_ATTACK2 = Animation.create(62);
    public static final Animation BODY_CHECK_ATTACK3 = Animation.create(62);
    public static final Animation BODY_CHECK_ATTACK4 = Animation.create(62);
    public static final Animation BODY_CHECK_ATTACK_SOUL1 = Animation.create(45);
    public static final Animation BODY_CHECK_ATTACK_SOUL2 = Animation.create(45);
    public static final Animation BODY_CHECK_ATTACK_SOUL3 = Animation.create(45);
    public static final Animation BODY_CHECK_ATTACK_SOUL4 = Animation.create(45);
    public static final Animation IGNIS_DEATH = Animation.create(124);
    public static final Animation BURNS_THE_EARTH = Animation.create(67);
    public static final Animation COUNTER = Animation.create(111);
    public static final Animation STRIKE = Animation.create(62);
    public static final Animation TRIPLE_ATTACK = Animation.create(139);
    public static final Animation FOUR_COMBO = Animation.create(141);
    public static final Animation BREAK_THE_SHIELD = Animation.create(87);
    public static final Animation SWING_UPPERCUT = Animation.create(65);
    public static final Animation SWING_UPPERSLASH = Animation.create(54);
    public static final Animation SPIN_ATTACK = Animation.create(56);
    public static final Animation EARTH_SHUDDERS_ATTACK = Animation.create(138);
    public static final Animation HORIZONTAL_SMALL_SWING_ATTACK = Animation.create(42);
    public static final Animation HORIZONTAL_SMALL_SWING_ALT_ATTACK = Animation.create(38);
    public static final Animation HORIZONTAL_SMALL_SWING_ALT_ATTACK2 = Animation.create(38);
    public static final int AIR_SMASH_COOLDOWN = 160;
    public static final int BODY_CHECK_COOLDOWN = 200;
    public static final int POKE_COOLDOWN = 200;
    public static final int CONTER_STRIKE_COOLDOWN = 360;
    public static final int EARTH_SHUDDERS_COOLDOWN = 400;
    public static final int HORIZONTAL_SMALL_SWING_COOLDOWN = 150;
    private static final DataParameter<Boolean> IS_BLOCKING = EntityDataManager.createKey(Ignis_Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_SHIELD_BREAK = EntityDataManager.createKey(Ignis_Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_SHIELD = EntityDataManager.createKey(Ignis_Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> SHIELD_DURABILITY = EntityDataManager.createKey(Ignis_Entity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SHOW_SHIELD = EntityDataManager.createKey(Ignis_Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_SWORD = EntityDataManager.createKey(Ignis_Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BOSS_PHASE = EntityDataManager.createKey(Ignis_Entity.class, DataSerializers.VARINT);
    private Vector3d prevBladePos = new Vector3d(0, 0, 0);

    private int body_check_cooldown = 0;
    private int air_smash_cooldown = 0;
    private int poke_cooldown = 0;
    private int counter_strike_cooldown = 0;
    private int horizontal_small_swing_cooldown = 0;
    private int earth_shudders_cooldown = 0;
    public boolean Combo = false;

    private int timeWithoutTarget;
    public float blockingProgress;
    public float swordProgress;
    public float prevblockingProgress;
    public float prevswordProgress;

    public Ignis_Entity(EntityType entity, World world) {
        super(entity, world);
        this.stepHeight = 2.5F;
        this.experienceValue = 500;
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        this.setPathPriority(PathNodeType.UNPASSABLE_RAIL, 0.0F);
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
                SWING_ATTACK_SOUL,
                SWING_UPPERCUT,
                SWING_UPPERSLASH,
                SPIN_ATTACK,
                HORIZONTAL_SWING_ATTACK,
                HORIZONTAL_SWING_ATTACK_SOUL,
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
                BODY_CHECK_ATTACK_SOUL1,
                BODY_CHECK_ATTACK_SOUL2,
                BODY_CHECK_ATTACK_SOUL3,
                BODY_CHECK_ATTACK_SOUL4,
                SMASH,
                COUNTER,
                STRIKE,
                SMASH_IN_AIR,
                BURNS_THE_EARTH,
                TRIPLE_ATTACK,
                BREAK_THE_SHIELD,
                FOUR_COMBO,
                EARTH_SHUDDERS_ATTACK,
                HORIZONTAL_SMALL_SWING_ATTACK,
                HORIZONTAL_SMALL_SWING_ALT_ATTACK,
                HORIZONTAL_SMALL_SWING_ALT_ATTACK2,
                IGNIS_DEATH};
    }
    protected void registerGoals() {
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new CmAttackGoal(this, 1.0D));
        this.goalSelector.addGoal(1, new Hornzontal_SwingGoal(this, HORIZONTAL_SWING_ATTACK, 31, 51, 20, 36));
        this.goalSelector.addGoal(1, new Hornzontal_SwingGoal(this, HORIZONTAL_SWING_ATTACK_SOUL, 27, 47, 16, 31));
        this.goalSelector.addGoal(1, new PokeGoal(this, POKE_ATTACK, 39, 59, 34, 41, 34, 40));
        this.goalSelector.addGoal(1, new PokeGoal(this, POKE_ATTACK2, 33, 53, 28, 35, 28, 34));
        this.goalSelector.addGoal(1, new PokeGoal(this, POKE_ATTACK3, 29, 49, 24, 31, 24, 30));
        this.goalSelector.addGoal(1, new AttackAnimationGoal2<>(this, PHASE_2, 34, 54));
        this.goalSelector.addGoal(1, new AttackAnimationGoal1<>(this, PHASE_3, 34, true));
        this.goalSelector.addGoal(1, new AttackAnimationGoal1<>(this, SWING_UPPERSLASH, 23, true));
        this.goalSelector.addGoal(1, new AttackAnimationGoal1<>(this, BREAK_THE_SHIELD, 35, false));
        this.goalSelector.addGoal(1, new ChargeAttackAnimationGoal2<>(this, SWING_UPPERCUT, 34, 50, 27, 0.3f, 0.3f));
        this.goalSelector.addGoal(1, new Shield_Smash(this, SHIELD_SMASH_ATTACK));
        this.goalSelector.addGoal(1, new Poked(this, POKED_ATTACK));
        this.goalSelector.addGoal(1, new Air_Smash(this, SMASH_IN_AIR));
        this.goalSelector.addGoal(1, new SimpleAnimationGoal<>(this, SMASH));
        this.goalSelector.addGoal(1, new AttackAnimationGoal1<>(this, SPIN_ATTACK, 10, true));
        this.goalSelector.addGoal(1, new Swing_Attack_Goal(this, SWING_ATTACK, 34, 40));
        this.goalSelector.addGoal(1, new Swing_Attack_Goal(this, SWING_ATTACK_SOUL, 28, 34));
        this.goalSelector.addGoal(1, new AttackAnimationGoal1<>(this, COUNTER, 105, true));
        this.goalSelector.addGoal(1, new AttackAnimationGoal1<>(this, STRIKE, 34, true));
        this.goalSelector.addGoal(1, new Hornzontal_Small_SwingGoal(this, 17, 13, 19));
        this.goalSelector.addGoal(1, new Body_Check_Attack(this));
        this.goalSelector.addGoal(1, new Triple_Attack(this, TRIPLE_ATTACK));
        this.goalSelector.addGoal(1, new Earth_Shudders(this, EARTH_SHUDDERS_ATTACK));
        this.goalSelector.addGoal(1, new Four_Combo(this, FOUR_COMBO));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));

    }


    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        Entity entity = source.getImmediateSource();
        LivingEntity target = this.getAttackTarget();
        double range = calculateRange(source);
        if(entity != null && !isAIDisabled() && (blockingProgress == 10 || swordProgress == 10)) {
            if(target !=null && target.isAlive()) {
                if (this.getAnimation() == NO_ANIMATION) {
                    if (this.getRNG().nextFloat() * 100.0F < 20f && counter_strike_cooldown <= 0 && range < 225) {
                        counter_strike_cooldown = CONTER_STRIKE_COOLDOWN;
                        this.setAnimation(COUNTER);
                    }
                }
            }
            if(this.getAnimation() == COUNTER) {
                if (this.getAnimationTick() > 16 && this.getAnimationTick() <= 96) {
                    AnimationHandler.INSTANCE.sendAnimationMessage(this, STRIKE);
                    this.playSound(SoundEvents.ENTITY_BLAZE_HURT, 0.5f, 0.4F + this.getRNG().nextFloat() * 0.1F);
                    return false;
                }
            }
        }
        if (range > CMConfig.IgnisLongRangelimit * CMConfig.IgnisLongRangelimit) {
            return false;
        }

        if (!source.canHarmInCreative()) {
            damage = Math.min(CMConfig.IgnisDamageCap, damage);
        }

        if((this.getBossPhase() == 1 && this.getHealth() <= this.getMaxHealth() * 1/3 || this.getBossPhase() == 0 && this.getHealth() <= this.getMaxHealth() * 2/3) && !source.canHarmInCreative()){
            damage *= 0.5;
        }

        if ((this.getAnimation() == PHASE_3 || this.getAnimation() == PHASE_2) && !source.canHarmInCreative()) {
            return false;
        }

        if (damage > 0.0F && this.canBlockDamageSource(source)) {
            this.damageShield(damage);

            if (!source.isProjectile()) {
                if (entity instanceof LivingEntity) {
                    this.blockUsingShield((LivingEntity) entity);
                }
            }
            if(source.getImmediateSource() instanceof TridentEntity) {
                if(source.getTrueSource() instanceof PlayerEntity) {
                    if (!world.isRemote) {
                        if (this.getShieldDurability() < 3) {
                            this.setShieldDurability(this.getShieldDurability() + 1);
                            this.playSound(ModSounds.IGNIS_SHIELD_BREAK.get(), 3.0f, 0.4F + this.getRNG().nextFloat() * 0.1F);
                        }
                    }
                }
            }

            this.playSound(SoundEvents.ENTITY_BLAZE_HURT, 0.5f, 0.4F + this.getRNG().nextFloat() * 0.1F);
            return false;
        }

        Ignis_Entity.Crackiness ignis$crackiness = this.getCrackiness();
        if (this.getBossPhase() > 0 && super.attackEntityFrom(source, damage) && this.getCrackiness() != ignis$crackiness) {
            this.playSound(ModSounds.IGNIS_ARMOR_BREAK.get(), 1.0F, 0.8F);
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
        this.dataManager.register(IS_SHIELD_BREAK, false);
        this.dataManager.register(IS_SWORD, false);
        this.dataManager.register(SHOW_SHIELD, true);
        this.dataManager.register(SHIELD_DURABILITY, 0);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("BossPhase", this.getBossPhase());
        compound.putBoolean("Is_Shield_Break", this.getIsShieldBreak());
        compound.putInt("Shield_Durability", this.getShieldDurability());
    }


    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setBossPhase(compound.getInt("BossPhase"));
        this.setIsShieldBreak(compound.getBoolean("Is_Shield_Break"));
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

    public void setIsShieldBreak(boolean isShieldBreak) {
        getDataManager().set(IS_SHIELD_BREAK, isShieldBreak);
    }

    public boolean getIsShieldBreak() {
        return getDataManager().get(IS_SHIELD_BREAK);
    }

    public void setShieldDurability(int ShieldDurability) {
        getDataManager().set(SHIELD_DURABILITY, ShieldDurability);
    }

    public int getShieldDurability() {
        return getDataManager().get(SHIELD_DURABILITY);
    }

    public void setShowShield(boolean showShield) {
        getDataManager().set(SHOW_SHIELD, showShield);
    }

    public boolean getShowShield() {
        return getDataManager().get(SHOW_SHIELD);
    }

    public Ignis_Entity.Crackiness getCrackiness() {
        return Ignis_Entity.Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
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
                .createMutableAttribute(Attributes.MAX_HEALTH, 450)
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
            if(this.getBossPhase() > 1){
                int i = this.getCrackiness() == Crackiness.NONE ? 5 : this.getCrackiness() == Crackiness.LOW ? 4 : this.getCrackiness() == Crackiness.MEDIUM ? 3 : 2;
                if (rand.nextInt(i) == 0) {
                    this.world.addParticle(ModParticle.SOUL_LAVA.get(), this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), 0.0D, 0.0D, 0.0D);
                }
            }else{
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }

        }else{
            timeWithoutTarget++;
            if (target != null) {
                timeWithoutTarget = 0;
                if(this.getIsShieldBreak()) {
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

            if(this.getIsShieldBreak()) {
                if (this.getIsBlocking()) {
                    this.setIsBlocking(false);
                    this.setIsSword(true);
                }
                this.setShieldDurability(3);
                this.setShowShield(false);
            }

            if (this.getBossPhase() > 0){
                bossInfo.setColor(BossInfo.Color.BLUE);
            }
            if (this.getBossPhase() > 1){
                bossInfo.setDarkenSky(true);
                if (this.getAnimation() != PHASE_3) {
                    this.setIsShieldBreak(true);
                }
            }

            if(this.getIsBlocking() && blockingProgress == 10){
                if(this.getAnimation() == NO_ANIMATION) {
                    setIsShield(true);
                }
                if(this.getAnimation() == COUNTER) {
                    setIsShield(true);
                }
                else if(this.getAnimation() == POKED_ATTACK) {
                    setIsShield(false);
                }
                else if(this.getAnimation() == BREAK_THE_SHIELD) {
                    setIsShield(false);
                }
                else if (this.getAnimation() == HORIZONTAL_SWING_ATTACK) {
                    setIsShield(this.getAnimationTick() > 31);
                }
                else if (this.getAnimation() == HORIZONTAL_SWING_ATTACK_SOUL) {
                    setIsShield(this.getAnimationTick() > 27);
                }
                else if (this.getAnimation() == BODY_CHECK_ATTACK1 || this.getAnimation() == BODY_CHECK_ATTACK2 ||
                        this.getAnimation() == BODY_CHECK_ATTACK3 || this.getAnimation() == BODY_CHECK_ATTACK4) {
                    setIsShield(this.getAnimationTick() < 25);
                }
                else if (this.getAnimation() == BODY_CHECK_ATTACK_SOUL1 || this.getAnimation() == BODY_CHECK_ATTACK_SOUL2 ||
                        this.getAnimation() == BODY_CHECK_ATTACK_SOUL3 || this.getAnimation() == BODY_CHECK_ATTACK_SOUL4) {
                    setIsShield(this.getAnimationTick() < 21);
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
                else if (this.getAnimation() == SWING_ATTACK_SOUL) {
                    setIsShield(this.getAnimationTick() < 28);
                }
            }else{
                setIsShield(false);
            }

        }
        if (body_check_cooldown > 0) body_check_cooldown--;
        if (air_smash_cooldown > 0) air_smash_cooldown--;
        if (counter_strike_cooldown > 0) counter_strike_cooldown--;
        if (poke_cooldown > 0) poke_cooldown--;
        if (earth_shudders_cooldown > 0) earth_shudders_cooldown--;
        repelEntities(1.4F, 4, 1.4F, 1.4F);

        rotationYaw = renderYawOffset;

        if (this.isAlive()) {
            if (!isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getShieldDurability() > 2 && !this.getIsShieldBreak()) {
                this.setAnimation(BREAK_THE_SHIELD);
            }else if (!isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getHealth() <= this.getMaxHealth() * 2/3 && this.getBossPhase() < 1) {
                this.setAnimation(PHASE_2);
            }else if (!isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getHealth() <= this.getMaxHealth() * 1/3 && this.getBossPhase() < 2) {
                this.setAnimation(PHASE_3);
            } else if (target != null && target.isAlive()) {
                if ((blockingProgress == 10 || swordProgress == 10) && !isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getDistanceSq(target) >= 225 && this.getDistanceSq(target) <= 1024.0D && target.isOnGround() && !this.getIsShieldBreak() && air_smash_cooldown <= 0) {
                    air_smash_cooldown = AIR_SMASH_COOLDOWN;
                    this.setAnimation(SMASH_IN_AIR);
                } else if ((blockingProgress == 10 || swordProgress == 10) && !isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getDistance(target) < 9F && this.getRNG().nextFloat() * 100.0F < 0.9f) {
                    Animation animation = getRandomPoke(rand);
                    this.setAnimation(animation);
                } else if ((blockingProgress == 10 || swordProgress == 10) && !isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getDistance(target) < 9F && this.getRNG().nextFloat() * 100.0F < 15F && poke_cooldown <= 0 && target.isPotionActive(ModEffect.EFFECTSTUN.get())) {
                    poke_cooldown = POKE_COOLDOWN;
                    Animation animation = getRandomPoke(rand);
                    this.setAnimation(animation);
                } else if ((blockingProgress == 10 || swordProgress == 10) && !isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getDistance(target) < 6.5F && this.getRNG().nextFloat() * 100.0F < 6f) {
                    Animation animation2 = this.getBossPhase() > 0 ? HORIZONTAL_SWING_ATTACK_SOUL : HORIZONTAL_SWING_ATTACK;
                    this.setAnimation(animation2);
                } else if ((blockingProgress == 10 || swordProgress == 10) && !isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getDistance(target) < 4.75F && this.getRNG().nextFloat() * 100.0F < 12f) {
                    Animation animation3 = this.getBossPhase() > 0 ? SWING_ATTACK_SOUL : SWING_ATTACK;
                    this.setAnimation(animation3);
                    //} else if (this.getAnimation() == NO_ANIMATION && this.distanceTo(target) < 5F && this.getRandom().nextFloat() * 100.0F < 6f && this.getIsShieldBreak()) {
                    //    if (this.getBossPhase() < 2) {
                    //        this.setAnimation(TRIPLE_ATTACK);
                    //    }else{
                    //this.setAnimation(FOUR_COMBO);
                    //    }
                } else if ((blockingProgress == 10 || swordProgress == 10) && !isAIDisabled() &&this.getAnimation() == NO_ANIMATION && this.getDistance(target) < 3F && this.getRNG().nextFloat() * 100.0F < 20f && !this.getIsShieldBreak()) {
                    this.setAnimation(SHIELD_SMASH_ATTACK);
                } else if ((blockingProgress == 10 || swordProgress == 10) && !isAIDisabled() &&this.getAnimation() == NO_ANIMATION && this.getDistance(target) < 5F && this.getRNG().nextFloat() * 100.0F < 0.7f && counter_strike_cooldown <= 0) {
                    counter_strike_cooldown = CONTER_STRIKE_COOLDOWN;
                    this.setAnimation(COUNTER);
                } else if ((blockingProgress == 10 || swordProgress == 10) && !isAIDisabled() &&this.getAnimation() == NO_ANIMATION && this.getDistance(target) > 4.5F && this.getDistance(target) < 11F && this.getRNG().nextFloat() * 100.0F < 0.9f && earth_shudders_cooldown <= 0 && target.isOnGround()) {
                    earth_shudders_cooldown = EARTH_SHUDDERS_COOLDOWN;
                    this.setAnimation(EARTH_SHUDDERS_ATTACK);
                } else if ((blockingProgress == 10 || swordProgress == 10) && !isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getDistance(target) < 5.5F && this.getRNG().nextFloat() * 100.0F < 12f && horizontal_small_swing_cooldown <= 0) {
                    horizontal_small_swing_cooldown = HORIZONTAL_SMALL_SWING_COOLDOWN;
                    this.setAnimation(HORIZONTAL_SMALL_SWING_ATTACK);
                } else if ((blockingProgress == 10 || swordProgress == 10) && !isAIDisabled() && this.getAnimation() == NO_ANIMATION && this.getDistance(target) < 3F && this.getRNG().nextFloat() * 100.0F < 10f && body_check_cooldown <= 0) {
                    body_check_cooldown = BODY_CHECK_COOLDOWN;
                    Animation animation3 = this.getBossPhase() > 0 ? BODY_CHECK_ATTACK_SOUL1 : BODY_CHECK_ATTACK1;
                    this.setAnimation(animation3);
                }
            }
        }

        super.tick();
        AnimationHandler.INSTANCE.updateAnimations(this);

    }

    public void livingTick() {
        super.livingTick();
        if (this.getAnimation() == SWING_ATTACK) {
            if (this.getAnimationTick() == 34) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(6.5f, 6, 70, 1.1f, 0.05f, 80, 2, 150, false, 0);
            }
        }
        if (this.getAnimation() == HORIZONTAL_SWING_ATTACK) {
            if (this.getAnimationTick() == 31) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(5.25f, 6, 210, 1.0f, 0.06f, 80, 3, 150, false, 0);
            }
        }
        if (this.getAnimation() == SWING_ATTACK_SOUL) {
            if (this.getAnimationTick() == 28) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(6.5f, 6, 70, 1.1f, 0.05f, 80, 2, 150, false, 0);
            }
        }
        if (this.getAnimation() == HORIZONTAL_SWING_ATTACK_SOUL) {
            if (this.getAnimationTick() == 27) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(5.25f, 6, 210, 1.0f, 0.06f, 80, 3, 150, false, 0);
            }
        }
        if (this.getAnimation() == HORIZONTAL_SMALL_SWING_ATTACK) {
            if (this.getAnimationTick() == 17) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1.25F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(5.25f, 6, 120, 0.5f, 0.03f, 20, 2, 150, true, 0);
            }

        }
        if (this.getAnimation() == HORIZONTAL_SMALL_SWING_ALT_ATTACK || this.getAnimation() == HORIZONTAL_SMALL_SWING_ALT_ATTACK2) {
            if (this.getAnimationTick() == 13) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1.25F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(5.25f, 6, 120, 0.5f, 0.03f, 20, 2, 150, false, 0);
            }
        }
        if (this.getAnimation() == BREAK_THE_SHIELD) {
            if (this.getAnimationTick() == 25){
                this.setShowShield(false);
                ShieldExplode(-2.75f,1.5f,2);
            }
            if (this.getAnimationTick() == 79){
                this.setIsShieldBreak(true);
            }
            if (this.getAnimationTick() == 55){
                ScreenShake_Entity.ScreenShake(world, this.getPositionVec(), 30, 0.15f, 0, 50);
                List<LivingEntity> entities = getEntityLivingBaseNearby(12, 12, 12, 12);
                this.playSound(ModSounds.FLAME_BURST.get(), 1.0f, 0.8F);
                for (LivingEntity inRange : entities) {
                    if (inRange instanceof PlayerEntity && ((PlayerEntity) inRange).abilities.disableDamage) continue;
                    if (isOnSameTeam(inRange)) continue;
                    inRange.addPotionEffect(new EffectInstance(ModEffect.EFFECTSTUN.get(), 60));
                }
            }
        }
        if (this.getAnimation() == PHASE_2) {
            if (this.getAnimationTick() == 29){
                this.playSound(ModSounds.FLAME_BURST.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
            }
            if (this.getAnimationTick() > 29 && this.getAnimationTick() < 39){
                Sphereparticle(2, 0,5);
                Phase_Transition(14,0.4f,0.03f,5,150);
            }
            if (this.getAnimationTick() == 34) {
                setBossPhase(1);
            }
        }
        if (this.getAnimation() == PHASE_3) {
            if (this.getAnimationTick() == 58){
                this.setBossPhase(2);
                this.setShowShield(false);
                if(!this.getIsShieldBreak()) {
                    ShieldExplode(2,0.575f,2);
                }
                this.playSound(ModSounds.FLAME_BURST.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                this.playSound(ModSounds.SWORD_STOMP.get(), 1.0f, 0.75F + this.getRNG().nextFloat() * 0.1F);
                ScreenShake_Entity.ScreenShake(world, this.getPositionVec(), 30, 0.15f, 0, 10);
                ShieldSmashparticle(0.5f,1.0f,-0.15f);
            }
            if (this.getAnimationTick() > 58 && this.getAnimationTick() < 68){
                Sphereparticle(0.5f, 1.0f, 6);
                Phase_Transition(27, 0.6f, 0.05f, 5, 150);
            }
        }

        if (this.getAnimation() == SHIELD_SMASH_ATTACK) {
            if (this.getAnimationTick() == 34){
                this.playSound(SoundEvents.ITEM_TOTEM_USE, 1.5f, 0.8F + this.getRNG().nextFloat() * 0.1F);
                ScreenShake_Entity.ScreenShake(world, this.getPositionVec(), 20, 0.3f, 0, 20);
                AreaAttack(4.85f, 2.5f, 45, 1.5f, 0.15f, 200, 0, 0, false, 0);
                ShieldSmashDamage(2,4,1.5f, 2.75f,false,0,1,0.02f,0.1f);
                ShieldSmashparticle(1.3f, 2.75f,-0.1f);
            }
            if (this.getAnimationTick() == 37) {
                ShieldSmashDamage(2,5,1.5f,2.75f,false,0,1,0.02f,0.1f);
            }
            if (this.getAnimationTick() == 40) {
                ShieldSmashDamage(2,6,1.5f,2.75f,false,0,1,0.02f,0.1f);
            }
        }
        if (this.getAnimation() == SMASH) {
            if (this.getAnimationTick() == 5){
                this.playSound(SoundEvents.ITEM_TOTEM_USE, 1.5f, 0.8F + this.getRNG().nextFloat() * 0.1F);
                ScreenShake_Entity.ScreenShake(world, this.getPositionVec(), 20, 0.3f, 0, 20);
                AreaAttack(4.85f, 2.5f, 45, 1.5f, 0.15f, 200, 0, 0, false, 0);
                ShieldSmashDamage(2,3,1.5f,1.5f, false,0,1,0.02f,0.1f);
                ShieldSmashparticle(1.3f,1.5f,0.0f);
            }
            if (this.getAnimationTick() == 8) {
                ShieldSmashDamage(2,4,1.5f,1.5f, false,0,1,0.02f,0.1f);
            }
            if (this.getAnimationTick() == 11) {
                ShieldSmashDamage(2,5,1.5f,1.5f, false,0,1,0.02f,0.1f);
            }
            if (this.getAnimationTick() == 14) {
                ShieldSmashDamage(2,6,1.5f,1.5f, false,0,1,0.02f,0.1f);
            }
        }

        if (this.getAnimation() == STRIKE) {
            if (this.getAnimationTick() == 31) {
                AreaAttack(5.25f, 6, 120, 1.1f, 0.1f, 100, 5, 150, false, 0);
            }

            if (this.getAnimationTick() == 36) {
                ShieldSmashDamage(0.75f,4,2.5f,0,true,150,1.1f,0.12f,0.1f);
                ShieldSmashDamage(0.75f,5,2.5f,0,true,150,1.1f,0.12f,0.1f);
                earthquakesound(4.5f);
            }
            if (this.getAnimationTick() == 38) {
                ShieldSmashDamage(0.75f,6,2.5f,0,true,150,1.1f,0.12f,0.1f);
                ShieldSmashDamage(0.75f,7,2.5f,0,true,150,1.1f,0.12f,0.1f);
                earthquakesound(6.5f);
            }
            if (this.getAnimationTick() == 40) {
                ShieldSmashDamage(0.75f,8,2.5f,0,true,150,1.1f,0.12f,0.1f);
                ShieldSmashDamage(0.75f,9,2.5f,0,true,150,1.1f,0.12f,0.1f);
                earthquakesound(8.5f);
            }
            if (this.getAnimationTick() == 42) {
                ShieldSmashDamage(0.75f,10,2.5f,0,true,150,1.1f,0.12f,0.1f);
                ShieldSmashDamage(0.75f,11,2.5f,0,true,150,1.1f,0.12f,0.1f);
                earthquakesound(10.5f);
            }
            if (this.getAnimationTick() == 44) {
                ShieldSmashDamage(0.75f,12,2.5f,0,true,150,1.1f,0.12f,0.1f);
                ShieldSmashDamage(0.75f,13,2.5f,0,true,150,1.1f,0.12f,0.1f);
                earthquakesound(12.5f);
            }
            if (this.getAnimationTick() == 46) {
                ShieldSmashDamage(0.75f,14,2.5f,0,true,150,1.1f,0.12f,0.1f);
                ShieldSmashDamage(0.75f,15,2.5f,0,true,150,1.1f,0.12f,0.1f);
                earthquakesound(14.5f);
            }
            if (this.getAnimationTick() == 48) {
                ShieldSmashDamage(0.75f,16,2.5f,0,true,150,1.1f,0.12f,0.1f);
                ShieldSmashDamage(0.75f,17,2.5f,0,true,150,1.1f,0.12f,0.1f);
                earthquakesound(16.5f);
            }

            if(this.getAnimationTick() > 31 && this.getAnimationTick() < 35){
                StrikeParticle(0.75f,5,0);
            }
        }

        if (this.getAnimation() == POKE_ATTACK) {
            if (this.getAnimationTick() == 37) {
                this.playSound(ModSounds.IGNIS_POKE.get(), 1.0f, 0.75F + this.getRNG().nextFloat() * 0.1F);
            }
            if (this.getAnimationTick() == 39) {
                Poke(7, 70,60);
            }
        }

        if (this.getAnimation() == POKE_ATTACK2) {
            if (this.getAnimationTick() == 32) {
                this.playSound(ModSounds.IGNIS_POKE.get(), 1.0f, 0.75F + this.getRNG().nextFloat() * 0.1F);
            }
            if (this.getAnimationTick() == 34) {
                Poke(7, 65,50);
            }
        }

        if (this.getAnimation() == POKE_ATTACK3) {
            if (this.getAnimationTick() == 27) {
                this.playSound(ModSounds.IGNIS_POKE.get(), 1.0f, 0.75F + this.getRNG().nextFloat() * 0.1F);
            }
            if (this.getAnimationTick() == 29) {
                Poke(7, 60,40);
            }
        }

        if (this.getAnimation() == BODY_CHECK_ATTACK1
                || this.getAnimation() == BODY_CHECK_ATTACK2
                || this.getAnimation() == BODY_CHECK_ATTACK3
                || this.getAnimation() == BODY_CHECK_ATTACK4) {
            if (this.getAnimationTick() == 25) {
                BodyCheckAttack(3.0f,6,120,0.8f,0.03f,40,80,0.2f);
            }

        }
        if (this.getAnimation() == BODY_CHECK_ATTACK_SOUL1
                || this.getAnimation() == BODY_CHECK_ATTACK_SOUL2
                || this.getAnimation() == BODY_CHECK_ATTACK_SOUL3
                || this.getAnimation() == BODY_CHECK_ATTACK_SOUL4) {
            if (this.getAnimationTick() == 21) {
                BodyCheckAttack(3.0f,6,120,0.9f,0.03f,60,100,0.2f);
            }
        }
        if (this.getAnimation() == TRIPLE_ATTACK) {
            if (this.getAnimationTick() == 30) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(5.5f, 6, 100, 1.0f, 0.05f, 80, 3, 150, false, 0);
            }
            if (this.getAnimationTick() == 73) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(5.5f, 6, 45, 1.1f, 0.06f, 120, 5, 150, false, 0);
            }

            if (this.getAnimationTick() == 108) {
                BodyCheckAttack(4.85f, 6, 60, 1.0f, 0.01f, 40, 0, 0.2f);
            }
        }

        if (this.getAnimation() == SWING_UPPERCUT) {
            if (this.getAnimationTick() == 32) {
                BodyCheckAttack(3.5f,8,70,1.0f,0.03f,60,70,0.8);
            }
        }
        if (this.getAnimation() == SWING_UPPERSLASH) {
            if (this.getAnimationTick() == 24) {
                this.playSound(ModSounds.STRONGSWING.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                AreaAttack(4.5f, 8, 100, 1.0f, 0.05f, 80, 3, 150, false, 0.65f);
            }
            if (this.getAnimationTick() == 26) {
                ShieldSmashDamage(0.4f,3,2.5f,0,false,80,1.0f,0.03f,0.1f);
                earthquakesound(3.5f);
                ShieldSmashDamage(0.4f,4,2.5f,0,false,80,1.0f,0.03f,0.1f);
            }
            if (this.getAnimationTick() == 28) {
                ShieldSmashDamage(0.4f,5,2.5f,0,false,80,1.0f,0.03f,0.1f);
                earthquakesound(5.5f);
                ShieldSmashDamage(0.4f,6,2.5f,0,false,80,1.0f,0.03f,0.1f);
            }
            if (this.getAnimationTick() == 30) {
                ShieldSmashDamage(0.4f,7,2.5f,0,false,80,1.0f,0.03f,0.1f);
                earthquakesound(6.5f);
                ShieldSmashDamage(0.4f,8,2.5f,0,false,80,1.0f,0.03f,0.1f);
            }
        }

        if (this.getAnimation() == EARTH_SHUDDERS_ATTACK) {
            if (this.getAnimationTick() == 32) {
                this.playSound(SoundEvents.ITEM_TOTEM_USE, 1.5f, 0.8F + this.getRNG().nextFloat() * 0.1F);
                ScreenShake_Entity.ScreenShake(world, this.getPositionVec(), 20, 0.3f, 0, 20);
                AreaAttack(4f, 6, 80, 1.2f, 0.08f, 100, 5, 150, false, 0);
                ShieldSmashparticle(0.75f, 2.3f, -0.65f);
                //ShieldSmashDamage(2f,10,3f,2.3f,false,80,1.0f,0.05f,0.05f);
                //ShieldSmashDamage(2f,9,3f,2.3f,false,80,1.0f,0.05f,0.05f);
                ShieldSmashDamage(2f, 7, 3f, 2.3f, false, 80, 1.0f, 0.05f, 0.05f);
                ShieldSmashDamage(2f, 6, 3f, 2.3f, false, 80, 1.0f, 0.05f, 0.05f);
                ShieldSmashDamage(2f, 5, 3f, 2.3f, false, 80, 1.0f, 0.05f, 0.05f);
                ShieldSmashDamage(2f, 4, 3f, 2.3f, false, 80, 1.0f, 0.05f, 0.05f);
            }

            if (this.getAnimationTick() == 73) {
                this.playSound(SoundEvents.ITEM_TOTEM_USE, 1.5f, 0.8F + this.getRNG().nextFloat() * 0.1F);
                ScreenShake_Entity.ScreenShake(world, this.getPositionVec(), 20, 0.3f, 0, 20);
                AreaAttack(4f, 6, 80, 1.2f, 0.08f, 100, 5, 150, false, 0);
                ShieldSmashparticle(0.75f, 1.85f, -0.6f);
                ShieldSmashDamage(2f, 16, 3f, 1.85f, false, 80, 1.0f, 0.08f, 0.05f);
                ShieldSmashDamage(2f, 15, 3f, 1.85f, false, 80, 1.0f, 0.08f, 0.05f);
            }

            if (this.getAnimationTick() == 75) {
                ShieldSmashDamage(2f, 14, 3f, 1.85f, false, 80, 1.0f, 0.08f, 0.05f);
                ShieldSmashDamage(2f, 13, 3f, 1.85f, false, 80, 1.0f, 0.08f, 0.05f);
            }

            if (this.getAnimationTick() == 77) {
                ShieldSmashDamage(2f, 12, 3f, 1.85f, false, 80, 1.0f, 0.08f, 0.05f);
                ShieldSmashDamage(2f, 11, 3f, 1.85f, false, 80, 1.0f, 0.08f, 0.05f);
            }

            if (this.getAnimationTick() == 79) {
                ShieldSmashDamage(2f, 10, 3f, 1.85f, false, 80, 1.0f, 0.08f, 0.05f);
                ShieldSmashDamage(2f, 9, 3f, 1.85f, false, 80, 1.0f, 0.08f, 0.05f);
            }

            if (this.getAnimationTick() == 81) {
                ShieldSmashDamage(2f, 8, 3f, 1.85f, false, 80, 1.0f, 0.08f, 0.05f);
            }

            if (this.getAnimationTick() == 83) {
                ShieldSmashDamage(2f, 7, 3f, 1.85f, false, 80, 1.0f, 0.08f, 0.05f);
            }
            if (this.getAnimationTick() == 85) {
                ShieldSmashDamage(2f, 6, 3f, 1.85f, false, 80, 1.0f, 0.08f, 0.05f);
            }

            if (this.getAnimationTick() == 117) {
                this.playSound(SoundEvents.ITEM_TOTEM_USE, 1.5f, 0.8F + this.getRNG().nextFloat() * 0.1F);
                ScreenShake_Entity.ScreenShake(world, this.getPositionVec(), 20, 0.3f, 0, 20);
                ShieldSmashparticle(0.75f, 2.3f, -0.65f);
                AreaAttack(4f, 6, 80, 1.2f, 0.08f, 100, 5, 150, false, 0);
                ShieldSmashDamage(2f, 3, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
                ShieldSmashDamage(2f, 4, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
            }
            if (this.getAnimationTick() == 120) {
                ShieldSmashDamage(2f, 5, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
                ShieldSmashDamage(2f, 6, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
            }
            if (this.getAnimationTick() == 123) {
                ShieldSmashDamage(2f, 7, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
                ShieldSmashDamage(2f, 8, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
            }
            if (this.getAnimationTick() == 126) {
                ShieldSmashDamage(2f, 9, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
                ShieldSmashDamage(2f, 10, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
            }
            if (this.getAnimationTick() == 129) {
                ShieldSmashDamage(2f, 11, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
                ShieldSmashDamage(2f, 12, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
            }
            if (this.getAnimationTick() == 132) {
                ShieldSmashDamage(2f, 13, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
                ShieldSmashDamage(2f, 14, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
            }
            if (this.getAnimationTick() == 135) {
                ShieldSmashDamage(2f, 15, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
                ShieldSmashDamage(2f, 16, 3f, 2.3f, false, 80, 1.0f, 0.08f, 0.05f);
            }
        }

        if (this.getAnimation() == FOUR_COMBO) {
            if (this.getAnimationTick() == 115) {
                this.playSound(ModSounds.FLAME_BURST.get(), 1.0f, 1F + this.getRNG().nextFloat() * 0.1F);
                this.playSound(ModSounds.SWORD_STOMP.get(), 1.0f, 0.75F + this.getRNG().nextFloat() * 0.1F);
                ScreenShake_Entity.ScreenShake(world, this.getPositionVec(), 30, 0.15f, 0, 50);
                AreaAttack(4f,4f,45,1.2f,0.1f,200,5,150,false,0);
                ShieldSmashparticle(0.5f,1.0f,-0.15f);
            }

            if (this.getAnimationTick() > 115 && this.getAnimationTick() < 125){
                Sphereparticle(0.5f,1.0f,6);
                Phase_Transition(56,0.4f,0.03f,5,150);
            }
        }
    }

    @Nullable
    public Animation getDeathAnimation()
    {
        return IGNIS_DEATH;
    }

    private void AreaAttack(float range,float height,float arc ,float damage, float hpdamage ,int shieldbreakticks, int firetime, int brandticks, boolean combo, float airborne) {
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
                            this.heal(5 * (i + 1));
                        }
                        if (combo) {
                            if (!Combo) {
                                Combo = true;
                            }
                        }

                        if (airborne > 0){
                            entityHit.setMotion(entityHit.getMotion().add(0.0D, airborne, 0.0D));
                        }
                    }
                }
            }
        }
    }

    private void BodyCheckAttack(float range, float height, float arc, float damage, float hpdamage, int shieldbreakticks, int slowticks, double airborne) {
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
                    if (flag) {
                        this.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.5f, 0.8F + this.getRNG().nextFloat() * 0.1F);
                        double d0 = entityHit.getPosX() - this.getPosX();
                        double d1 = entityHit.getPosZ() - this.getPosZ();
                        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                        entityHit.addVelocity(d0 / d2 * 2.5D, airborne, d1 / d2 * 2.5D);
                        if (slowticks > 0) {
                            entityHit.addPotionEffect(new EffectInstance(ModEffect.EFFECTSTUN.get(), slowticks));
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
                if (flag && !EntityTypeTags.getCollection().get(ModTag.IGNIS_CANT_POKE).contains(target.getType()) && target.isAlive()) {
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
            if(this.getAnimation() == HORIZONTAL_SWING_ATTACK_SOUL) {
                if (this.getAnimationTick() > 24 && this.getAnimationTick() < 28) {
                    Flameswing();
                }
            }
            if(this.getAnimation() == SWING_ATTACK_SOUL) {
                if (this.getAnimationTick() > 26 && this.getAnimationTick() < 29) {
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
            if(this.getAnimation() == PHASE_3) {
                if (this.getAnimationTick() > 96 && this.getAnimationTick() < 100) {
                    Flameswing();
                }
            }
            if(this.getAnimation() == FOUR_COMBO) {
                Flameswing();
            }
            if(this.getAnimation() == STRIKE) {
                if (this.getAnimationTick() > 28 && this.getAnimationTick() < 33) {
                    Flameswing();
                }
            }
            if (this.getAnimation() == SWING_UPPERSLASH) {
                if (this.getAnimationTick() > 23 && this.getAnimationTick() < 28) {
                    Flameswing();
                }
            }
            if (this.getAnimation() == HORIZONTAL_SMALL_SWING_ATTACK) {
                if (this.getAnimationTick() > 5) {
                    Flameswing();
                }
            }
            if (this.getAnimation() == HORIZONTAL_SMALL_SWING_ALT_ATTACK2) {
                if (this.getAnimationTick() > 3) {
                    Flameswing();
                }
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

    private void ShieldExplode(float radius, float math, float y) {
        if (!this.world.isRemote) {
            float angle = (0.01745329251F * this.renderYawOffset);
            float f = MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F));
            float f1 = MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F));
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            this.world.createExplosion(this, this.getPosX() + extraX + f * math, this.getPosY() + y, this.getPosZ() + extraZ + f1 * math, 2.0F, Explosion.Mode.NONE);
        }
    }

    private void ShieldSmashDamage(float spreadarc,int distance, float mxy, float vec, boolean grab, int shieldbreakticks, float damage, float hpdamage, float airborne) {
            double perpFacing = this.renderYawOffset * (Math.PI / 180);
            double facingAngle = perpFacing + Math.PI / 2;
            int hitY = MathHelper.floor(this.getBoundingBox().minY - 0.5);
            double spread = Math.PI * spreadarc;
            int arcLen = MathHelper.ceil(distance * spread);
            double minY = this.getPosY() - 1;
            double maxY = this.getPosY() + mxy;
            for (int i = 0; i < arcLen; i++) {
                double theta = (i / (arcLen - 1.0) - 0.5) * spread + facingAngle;
                double vx = Math.cos(theta);
                double vz = Math.sin(theta);
                double px = this.getPosX() + vx * distance + vec * Math.cos((renderYawOffset + 90) * Math.PI / 180);
                double pz = this.getPosZ() + vz * distance + vec * Math.sin((renderYawOffset + 90) * Math.PI / 180);
                float factor = 1 - distance / (float) 12;
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
                        if (entity instanceof PlayerEntity) {
                            if (entity.isActiveItemStackBlocking() && shieldbreakticks > 0) {
                                disableShield(entity, shieldbreakticks);
                            }
                        }
                        boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damage + entity.getMaxHealth() * hpdamage));
                        if (flag) {
                            if (grab) {
                                double magnitude = -4;
                                double x = vx * (1 - factor) * magnitude;
                                double y = 0;
                                if (entity.isOnGround()) {
                                    y += 0.15;
                                }
                                double z = vz * (1 - factor) * magnitude;
                                entity.setMotion(entity.getMotion().add(x, y, z));
                            }else{
                                entity.setMotion(entity.getMotion().add(0.0D, airborne* distance + world.rand.nextDouble() * 0.15, 0.0D));
                            }
                        }
                    }
                }

            }
    }

    private void earthquakesound(float distance){
        double theta = (renderYawOffset) * (Math.PI / 180);
        theta += Math.PI / 2;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        this.world.playSound(this.getPosX() + distance * vecX, this.getPosY(), this.getPosZ() + distance * vecZ, SoundEvents.ITEM_TOTEM_USE, this.getSoundCategory(), 1.5f, 0.8F + this.getRNG().nextFloat() * 0.1F, false);
    }

    private void StrikeParticle(float spreadarc,int distance, float vec) {
        double perpFacing = this.renderYawOffset * (Math.PI / 180);
        double facingAngle = perpFacing + Math.PI / 2;
        double spread = Math.PI * spreadarc;
        int arcLen = MathHelper.ceil((distance) * spread);
        for (int i = 0; i < arcLen; i++) {
            double theta = (i / (arcLen - 1.0) - 0.5) * spread + facingAngle;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            double vy = MathHelper.sqrt((float) (vx * distance * vx * distance + vz * distance * vz * distance));
            double px = this.getPosX() + vx * distance + vec * Math.cos((renderYawOffset + 90) * Math.PI / 180);
            double pz = this.getPosZ() + vz * distance + vec * Math.sin((renderYawOffset + 90) * Math.PI / 180);
            if (this.world.isRemote) {
                if (this.ticksExisted % 2 == 0) {
                    for (int i1 = 0; i1 < 80 + rand.nextInt(12); i1++) {
                        double motionX = 0.2D * MathHelper.lerp(1, vx * distance + 3, vx * distance);
                        double motionY = 0.2D * MathHelper.lerp(1.5, vy * 0.1, vy * 0.1);
                        double motionZ = 0.2D * MathHelper.lerp(1, vz * distance + 3, vz * distance);
                        double spreads = 10 + this.getRNG().nextDouble() * 2.5;
                        double velocity = 0.5 + this.getRNG().nextDouble() * 0.15;

                        // spread flame
                        motionX += this.getRNG().nextGaussian() * 0.007499999832361937D * spreads;
                        motionZ += this.getRNG().nextGaussian() * 0.007499999832361937D * spreads;
                        motionX *= velocity;
                        ;
                        motionZ *= velocity;
                        IParticleData type = this.getBossPhase() > 0 ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
                        this.world.addParticle(type, px, this.getPosY() + 1.3f, pz, motionX, motionY, motionZ);
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

    private void Phase_Transition(int dist, float damage,float hpdamage, int firetime, int brandticks) {
        if (this.getAnimationTick() % 2 == 0) {
            int distance = this.getAnimationTick() / 2 - dist;
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
        if (this.getAnimation() == POKED_ATTACK || this.getAnimation() == SMASH) {
            if (Ignis_Entity.this.getNavigator().getPath() != null) {
                Ignis_Entity.this.getNavigator().clearPath();
            }
            travelVector = Vector3d.ZERO;
            super.travel(travelVector);
            return;
        }
        super.travel(travelVector);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.IGNIS_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.IGNIS_DEATH.get();
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


    public enum Crackiness {
        NONE(1.0F),
        LOW(0.35F),
        MEDIUM(0.25F),
        HIGH(0.1F);

        private static final List<Ignis_Entity.Crackiness> BY_DAMAGE = Stream.of(values()).sorted(Comparator.comparingDouble((p_28904_) -> {
            return (double)p_28904_.fraction;
        })).collect(ImmutableList.toImmutableList());
        private final float fraction;

        private Crackiness(float p_28900_) {
            this.fraction = p_28900_;
        }

        public static Ignis_Entity.Crackiness byFraction(float p_28902_) {
            for(Ignis_Entity.Crackiness ignis$crackiness : BY_DAMAGE) {
                if (p_28902_ < ignis$crackiness.fraction) {
                    return ignis$crackiness;
                }
            }

            return NONE;
        }
    }

    private static Animation getRandomFollow(Random rand) {
        switch (rand.nextInt(2)) {
            case 0:
                return SWING_UPPERSLASH;
            case 1:
                return SWING_UPPERCUT;
        }
        return SWING_UPPERSLASH;
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

    class Hornzontal_SwingGoal extends SimpleAnimationGoal<Ignis_Entity> {
        private final int look1;
        private final int look2;
        private final int charge;
        private final int bodycheck;


        public Hornzontal_SwingGoal(Ignis_Entity entity, Animation animation, int look1, int look2, int charge, int bodycheck) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.look1 = look1;
            this.look2 = look2;
            this.charge = charge;
            this.bodycheck = bodycheck;

        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (Ignis_Entity.this.getAnimationTick() < look1 && target != null || Ignis_Entity.this.getAnimationTick() > look2 && target != null) {
                Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
            }
            if (Ignis_Entity.this.getAnimationTick() == charge) {
                float f1 = (float) Math.cos(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
                float f2 = (float) Math.sin(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
                if(target != null) {
                    float r = Ignis_Entity.this.getDistance(target);
                    r = MathHelper.clamp(r, 0, 10);
                    Ignis_Entity.this.addVelocity(f1 * 0.3 * r, 0, f2 * 0.3 * r);
                }

            }
            if (Ignis_Entity.this.getAnimationTick() == bodycheck && shouldFollowUp(3.5f) && Ignis_Entity.this.rand.nextInt(3) == 0 && body_check_cooldown <= 0) {
                body_check_cooldown = BODY_CHECK_COOLDOWN;
                Animation bodycheck = Ignis_Entity.this.getBossPhase() > 0 ? BODY_CHECK_ATTACK_SOUL2 : BODY_CHECK_ATTACK2;
                AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, bodycheck);
            }
        }
    }

    class Hornzontal_Small_SwingGoal extends AnimationGoal<Ignis_Entity> {
        private final int look1;
        private final int look2;
        private final int follow_through_tick;


        public Hornzontal_Small_SwingGoal(Ignis_Entity entity, int look1, int look2, int follow_through_tick) {
            super(entity);
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
            this.look1 = look1;
            this.look2 = look2;
            this.follow_through_tick = follow_through_tick;

        }

        @Override
        protected boolean test(Animation animation) {
            return animation == HORIZONTAL_SMALL_SWING_ALT_ATTACK2 || animation == HORIZONTAL_SMALL_SWING_ATTACK || animation == HORIZONTAL_SMALL_SWING_ALT_ATTACK;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            float f1 = (float) Math.cos(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
            float f2 = (float) Math.sin(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
            if (Ignis_Entity.this.getAnimation() == HORIZONTAL_SMALL_SWING_ATTACK) {
                if (Ignis_Entity.this.getAnimationTick() < look1 && target != null) {
                    Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
                } else {
                    Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
                }
                if (Ignis_Entity.this.getAnimationTick() == 12) {
                    if(target != null) {
                        if (Ignis_Entity.this.getDistance(target) > 3.5F) {
                            Ignis_Entity.this.addVelocity(f1 * 1.5, 0, f2 * 1.5);
                        }
                    }else{
                        Ignis_Entity.this.addVelocity(f1 * 1.5, 0, f2 * 1.5);
                    }
                }
                if (Combo) {
                    if (Ignis_Entity.this.getAnimationTick() == follow_through_tick) {
                        Combo = false;
                        AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, HORIZONTAL_SMALL_SWING_ALT_ATTACK2);
                    }
                }
            }
            if (Ignis_Entity.this.getAnimation() == HORIZONTAL_SMALL_SWING_ALT_ATTACK2 || Ignis_Entity.this.getAnimation() == HORIZONTAL_SMALL_SWING_ALT_ATTACK) {
                if (Ignis_Entity.this.getAnimationTick() < look2 && target != null) {
                    Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
                } else {
                    Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
                }
                if (Ignis_Entity.this.getAnimationTick() == 10) {
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

    class Body_Check_Attack extends AnimationGoal<Ignis_Entity> {

        public Body_Check_Attack(Ignis_Entity entity) {
            super(entity);
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));

        }

        @Override
        protected boolean test(Animation animation) {
            return animation == BODY_CHECK_ATTACK1
                    || animation == BODY_CHECK_ATTACK2
                    || animation == BODY_CHECK_ATTACK3
                    || animation == BODY_CHECK_ATTACK4
                    || animation == BODY_CHECK_ATTACK_SOUL1
                    || animation == BODY_CHECK_ATTACK_SOUL2
                    || animation == BODY_CHECK_ATTACK_SOUL3
                    || animation == BODY_CHECK_ATTACK_SOUL4;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK_SOUL1
                    || Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK_SOUL2
                    || Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK_SOUL3
                    || Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK_SOUL4) {
                if (Ignis_Entity.this.getAnimationTick() < 21 && target != null) {
                    Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
                } else {
                    Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
                }
                if (Ignis_Entity.this.getAnimationTick() == 16 && target != null) {
                    Ignis_Entity.this.setMotion((target.getPosX() - Ignis_Entity.this.getPosX()) * 0.4F, 0, (target.getPosZ() - Ignis_Entity.this.getPosZ()) * 0.4F);
                }
            }
            if (Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK1
                    || Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK2
                    || Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK3
                    || Ignis_Entity.this.getAnimation() == BODY_CHECK_ATTACK4) {
                if (Ignis_Entity.this.getAnimationTick() < 25 && target != null) {
                    Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
                } else {
                    Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
                }
                if (Ignis_Entity.this.getAnimationTick() == 20 && target != null) {
                    Ignis_Entity.this.setMotion((target.getPosX() - Ignis_Entity.this.getPosX()) * 0.25F, 0, (target.getPosZ() - Ignis_Entity.this.getPosZ()) * 0.25F);
                }
            }
        }
    }

    class PokeGoal extends SimpleAnimationGoal<Ignis_Entity> {
        private final int look1;
        private final int look2;
        private final int charge;
        private final int bodycheck;
        private final int motion1;
        private final int motion2;

        public PokeGoal(Ignis_Entity entity, Animation animation, int look1, int look2, int charge, int bodycheck, int motion1, int motion2) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.look1 = look1;
            this.look2 = look2;
            this.charge = charge;
            this.bodycheck = bodycheck;
            this.motion1 = motion1;
            this.motion2 = motion2;
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            float f1 = (float) Math.cos(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
            float f2 = (float) Math.sin(Math.toRadians(Ignis_Entity.this.rotationYaw + 90));
            if (Ignis_Entity.this.getAnimationTick() < look1 && target != null || Ignis_Entity.this.getAnimationTick() > look2 && target != null) {
                Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
            }
            if (Ignis_Entity.this.getAnimationTick() == charge) {
                if (target != null) {
                    float r =  Ignis_Entity.this.getDistance(target);
                    r = MathHelper.clamp(r, 0, 15);
                    Ignis_Entity.this.addVelocity(f1 * 0.3 * r, 0, f2 * 0.3 * r);
                }else{
                    Ignis_Entity.this.addVelocity(f1,0, f2);
                }
            }
            if (Ignis_Entity.this.getAnimationTick() == bodycheck && shouldFollowUp(3.0f) && Ignis_Entity.this.rand.nextInt(2) == 0 && body_check_cooldown <= 0) {
                body_check_cooldown = BODY_CHECK_COOLDOWN;
                Animation bodycheck = Ignis_Entity.this.getBossPhase() > 0 ? BODY_CHECK_ATTACK_SOUL4 : BODY_CHECK_ATTACK4;
                AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, bodycheck);
            }
            if (Ignis_Entity.this.getAnimationTick() < motion1 || Ignis_Entity.this.getAnimationTick() > motion2) {
                Ignis_Entity.this.setMotion(0, Ignis_Entity.this.getMotion().y, 0);
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

    class Swing_Attack_Goal extends SimpleAnimationGoal<Ignis_Entity> {
        private final int look1;
        private final int follow_through_tick;

        public Swing_Attack_Goal(Ignis_Entity entity, Animation animation, int look1, int follow_through_tick) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.look1 = look1;
            this.follow_through_tick = follow_through_tick;

        }
        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (Ignis_Entity.this.getAnimationTick() < look1 && target != null) {
                Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
            }
            if (Ignis_Entity.this.getAnimationTick() == follow_through_tick  && shouldFollowUp(7.0f) && Ignis_Entity.this.rand.nextDouble() < 0.75D) {
                Animation animation = getRandomFollow(rand);
                AnimationHandler.INSTANCE.sendAnimationMessage(Ignis_Entity.this, animation);

            }
            Ignis_Entity.this.setMotion(0, Ignis_Entity.this.getMotion().y, 0);
        }
    }

    class Earth_Shudders extends SimpleAnimationGoal<Ignis_Entity> {

        public Earth_Shudders(Ignis_Entity entity, Animation animation) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        public void tick() {
            Ignis_Entity.this.setMotion(0, Ignis_Entity.this.getMotion().y, 0);
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (Ignis_Entity.this.getAnimationTick() < 31 && target != null
                    || Ignis_Entity.this.getAnimationTick() < 73 && Ignis_Entity.this.getAnimationTick() > 45 && target != null
                    || Ignis_Entity.this.getAnimationTick() < 117 && Ignis_Entity.this.getAnimationTick() > 89 && target != null) {
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

    class Four_Combo extends SimpleAnimationGoal<Ignis_Entity> {

        public Four_Combo(Ignis_Entity entity, Animation animation) {
            super(entity, animation);
            this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public void tick() {
            LivingEntity target = Ignis_Entity.this.getAttackTarget();
            if (Ignis_Entity.this.getAnimationTick() < 26 && target != null
                    || Ignis_Entity.this.getAnimationTick() < 48 && Ignis_Entity.this.getAnimationTick() > 28 && target != null
                    || Ignis_Entity.this.getAnimationTick() < 76 && Ignis_Entity.this.getAnimationTick() > 59 && target != null
                    || Ignis_Entity.this.getAnimationTick() < 103 && Ignis_Entity.this.getAnimationTick() > 87 && target != null) {
                Ignis_Entity.this.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            } else {
                Ignis_Entity.this.rotationYaw = Ignis_Entity.this.prevRotationYaw;
            }
            if (Ignis_Entity.this.getAnimationTick() == 69 || Ignis_Entity.this.getAnimationTick() == 42 || Ignis_Entity.this.getAnimationTick() == 19) {
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

