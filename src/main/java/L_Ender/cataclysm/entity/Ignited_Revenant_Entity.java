package L_Ender.cataclysm.entity;

import L_Ender.cataclysm.config.CMConfig;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;


public class Ignited_Revenant_Entity extends Boss_monster {


    public float deactivateProgress;
    public float prevdeactivateProgress;

    public Ignited_Revenant_Entity(EntityType entity, World world) {
        super(entity, world);
        this.experienceValue = 15;
        this.stepHeight = 1.5F;
        this.setPathPriority(PathNodeType.UNPASSABLE_RAIL, 0.0F);
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        setConfigattribute(this, CMConfig.EnderGolemHealthMultiplier, CMConfig.EnderGolemDamageMultiplier);
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{NO_ANIMATION};
    }

    protected void registerGoals() {
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

    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);

    }


    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
    }


    public void tick() {
        super.tick();
        AnimationHandler.INSTANCE.updateAnimations(this);
        LivingEntity target = this.getAttackTarget();


    }


    @Override
    protected void onDeathAIUpdate() {
        super.onDeathAIUpdate();

    }

    @Override
    protected void repelEntities(float x, float y, float z, float radius) {
        super.repelEntities(x, y, z, radius);
    }

    @Override
    public boolean canBePushedByEntity(Entity entity) {
        return false;
    }


}





