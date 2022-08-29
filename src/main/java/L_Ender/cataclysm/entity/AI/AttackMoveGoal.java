package L_Ender.cataclysm.entity.AI;



import L_Ender.cataclysm.entity.Boss_monster;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;


import java.util.EnumSet;

public class AttackMoveGoal extends Goal {
    private final Boss_monster Boss_monster;
    private LivingEntity target;
    private final boolean followingTargetEvenIfNotSeen;
    private Path path;
    private int delayCounter;
    protected final double moveSpeed;


    public AttackMoveGoal(Boss_monster boss, boolean followingTargetEvenIfNotSeen, double moveSpeed) {
        this.Boss_monster = boss;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.moveSpeed = moveSpeed;
        this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
    }


    public boolean shouldExecute() {
        this.target = this.Boss_monster.getAttackTarget();
        return this.target != null && target.isAlive() && this.Boss_monster.getAnimation() == IAnimatedEntity.NO_ANIMATION;
    }


    public void resetTask() {
        this.Boss_monster.getNavigator().clearPath();
        if (this.Boss_monster.getAttackTarget() == null) {
            this.Boss_monster.setAggroed(false);
            this.Boss_monster.getNavigator().clearPath();
        }
    }

    public boolean shouldContinueExecuting() {
        if (target == null) {
            return false;
        } else if (!target.isAlive()) {
            return false;
        } else if (!this.followingTargetEvenIfNotSeen) {
            return !this.Boss_monster.getNavigator().noPath();
        } else if (!this.Boss_monster.isWithinHomeDistanceFromPosition(target.getPosition())) {
            return false;
        } else {
            return !(target instanceof PlayerEntity) || !target.isSpectator() && !((PlayerEntity)target).isCreative();
        }
    }

    public void startExecuting() {
        this.Boss_monster.getNavigator().setPath(this.path, this.moveSpeed);
        this.Boss_monster.setAggroed(true);
    }


    public void tick() {
        if (target == null) {
            return;
        }
        this.Boss_monster.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
        this.Boss_monster.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
        double distSq = this.Boss_monster.getDistanceSq(target.getPosX(), target.getBoundingBox().minY, target.getPosZ());
        if (--this.delayCounter <= 0) {
            this.delayCounter = 4 + this.Boss_monster.getRNG().nextInt(7);
            if (distSq > Math.pow(this.Boss_monster.getAttribute(Attributes.FOLLOW_RANGE).getValue(), 2.0D)) {
                if (!this.Boss_monster.hasPath()) {
                    if (!this.Boss_monster.getNavigator().tryMoveToEntityLiving(target, 1.0D)) {
                        this.delayCounter += 5;
                    }
                }
            } else {
                this.Boss_monster.getNavigator().tryMoveToEntityLiving(target, this.moveSpeed);
            }
        }
    }
}
