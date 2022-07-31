package L_Ender.cataclysm.entity.AI;

import L_Ender.cataclysm.entity.Boss_monster;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.entity.LivingEntity;

public class AttackAnimationGoal1<T extends Boss_monster & IAnimatedEntity> extends SimpleAnimationGoal<T> {
    private final int look1;
    private final boolean see;
    public AttackAnimationGoal1(T entity, Animation animation, int look1, boolean see) {
        super(entity, animation);
        this.look1 = look1;
        this.see = see;
    }
    public void tick() {
        LivingEntity target = entity.getAttackTarget();
        if(see) {
            if (entity.getAnimationTick() < look1 && target != null) {
                entity.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            } else {
                entity.rotationYaw = entity.prevRotationYaw;
            }
        }else{
            if (entity.getAnimationTick() > look1 && target != null) {
                entity.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            } else {
                entity.rotationYaw = entity.prevRotationYaw;
            }
        }
        entity.setMotion(0, entity.getMotion().y, 0);
    }
}
