package L_Ender.cataclysm.entity.AI;

import L_Ender.cataclysm.entity.Boss_monster;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.entity.LivingEntity;

public class ChargeAttackAnimationGoal2<T extends Boss_monster & IAnimatedEntity> extends SimpleAnimationGoal<T> {
    private final int look1;
    private final int look2;
    private final int charge;
    private final float motionx;
    private final float motionz;

    public ChargeAttackAnimationGoal2(T entity, Animation animation, int look1, int look2, int charge, float motionx, float motionz) {
        super(entity, animation);
        this.look1 = look1;
        this.look2 = look2;
        this.charge = charge;
        this.motionx = motionx;
        this.motionz = motionz;
    }
    public void tick() {
        LivingEntity target = entity.getAttackTarget();
        if (entity.getAnimationTick() < look1 && target != null || entity.getAnimationTick() > look2 && target != null) {
            entity.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
        } else {
            entity.rotationYaw = entity.prevRotationYaw;
        }
        if (entity.getAnimationTick() == charge && target != null){
            entity.setMotion((target.getPosX() - entity.getPosX()) * motionx, 0, (target.getPosZ() - entity.getPosZ()) * motionz);
        }
    }

}
