package L_Ender.cataclysm.entity.AI;

import L_Ender.cataclysm.entity.Boss_monster;
import L_Ender.cataclysm.entity.Ignis_Entity;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.entity.LivingEntity;


public class AttackAnimationGoal2<T extends Boss_monster & IAnimatedEntity> extends SimpleAnimationGoal<T> {
    private final int look1;
    private final int look2;
    public AttackAnimationGoal2(T entity, Animation animation, int look1, int look2) {
        super(entity, animation);
        this.look1 = look1;
        this.look2 = look2;
    }
    public void tick() {
        LivingEntity target = entity.getAttackTarget();
        if (entity.getAnimationTick() < look1 && target != null || entity.getAnimationTick() > look2 && target != null) {
            entity.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
        } else {
            entity.rotationYaw = entity.prevRotationYaw;
        }
        entity.setMotion(0, entity.getMotion().y, 0);
    }

}
