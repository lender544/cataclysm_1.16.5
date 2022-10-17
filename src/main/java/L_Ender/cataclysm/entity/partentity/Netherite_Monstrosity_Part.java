package L_Ender.cataclysm.entity.partentity;

import L_Ender.cataclysm.entity.Netherite_Monstrosity_Entity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;


public class Netherite_Monstrosity_Part extends Cm_Part_Entity<Netherite_Monstrosity_Entity> {

    private EntitySize size;
    public float scale = 1;

    public Netherite_Monstrosity_Part(Netherite_Monstrosity_Entity parent, float sizeX, float sizeY) {
        super(parent);
        this.size = EntitySize.flexible(sizeX, sizeY);
        this.recalculateSize();
    }

    public Netherite_Monstrosity_Part(Netherite_Monstrosity_Entity entityCachalotWhale, float sizeX, float sizeY, EntitySize size) {
        super(entityCachalotWhale);
        this.size = size;
    }
    @Override
    protected void registerData() {
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean func_241845_aY() {
        return this.getParent().isAlive();
    }


    public EntitySize getSize(Pose poseIn) {
        return this.size.scale(scale);
    }


    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return getParent() != null && getParent().attackEntityFromPart(this, source, amount * 1.35F);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }

    @Override
    public boolean isEntityEqual(Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    @Override
    protected void setRotation(float yaw, float pitch) {
        this.rotationYaw = yaw % 360.0F;
        this.rotationPitch = pitch % 360.0F;
    }

    @Override
    protected boolean canBeRidden(Entity entityIn) {
        return false;
    }

    @Override
    public boolean canChangeDimension() {
        return false;
    }

}
