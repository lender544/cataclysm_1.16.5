package L_Ender.cataclysm.entity.AI;

import L_Ender.cataclysm.entity.Boss_monster;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class PredictiveChargeAttackAnimationGoal<T extends Boss_monster & IAnimatedEntity> extends SimpleAnimationGoal<T> {

    protected LivingEntity target;
    private final int look1;
    private final int look2;

    private final float sensing;
    private final int charge;
    private final float motionx;
    private final float motionz;


    public double prevX;
    public double prevZ;
    private int newX;
    private int newZ;

    public PredictiveChargeAttackAnimationGoal(T entity, Animation animation, int look1, int look2, float sensing, int charge, float motionx, float motionz) {
        super(entity, animation);
        this.look1 = look1;
        this.look2 = look2;
        this.sensing = sensing;
        this.charge = charge;
        this.motionx = motionx;
        this.motionz = motionz;
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        target = entity.getAttackTarget();
        if (target != null) {
            prevX = target.getPosX();
            prevZ = target.getPosZ();
        }
    }

    public void tick() {
        if (entity.getAnimationTick() < look1 && target != null || entity.getAnimationTick() > look2 && target != null) {
            entity.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
        } else {
            entity.rotationYaw = entity.prevRotationYaw;
        }
        if (entity.getAnimationTick() == (charge -1) && target != null) {
            double x = target.getPosX();
            double z = target.getPosZ();
            double vx = (x - prevX) / charge;
            double vz = (z - prevZ) / charge;
            newX = MathHelper.floor(x + vx * sensing);
            newZ = MathHelper.floor(z + vz * sensing);
        }

        if (entity.getAnimationTick() == charge && target != null){
            entity.setMotion((newX - entity.getPosX()) * motionx, 0, (newZ - entity.getPosZ()) * motionz);

        }
    }

}
