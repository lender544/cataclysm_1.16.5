package L_Ender.cataclysm.client.model.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.math.vector.Vector3d;

public class ModelIgnitium_Elytra_Chestplate extends BipedModel {

    public ModelRenderer rightWing;
    public ModelRenderer leftWing;
    public ModelRenderer outer_body;
    public ModelRenderer inner_body;
    public ModelRenderer right_shoulderpad;
    public ModelRenderer left_shoulderpad;
    public ModelRenderer right_spike;
    public ModelRenderer left_spike;
    public ModelRenderer side_spike;
    public ModelRenderer side_spike2;

    public ModelIgnitium_Elytra_Chestplate(float modelSize) {
        super(modelSize, 0, 128, 128);
        leftWing = new ModelRenderer(this);
        leftWing.setRotationPoint(5.0F, 0.0F, 1.5F);
        leftWing.setTextureOffset(0, 65).addBox(-10.0F, 0.0F, 1.5F, 11.0F, 23.0F, 2.0F, 0.0F,true);

        rightWing = new ModelRenderer(this);
        rightWing.setRotationPoint(-5.0F, 0.0F, 1.5F);
        rightWing.setTextureOffset(0, 65).addBox(0F, 0.0F, 1.5F, 11.0F, 23.0F, 2.0F, 0.0F,false);

        outer_body = new ModelRenderer(this);
        outer_body.setRotationPoint(0.0F, -1.0F, 0.0F);
        bipedBody.addChild(outer_body);
        outer_body.setTextureOffset(30, 47).addBox(-4.5F, 1.0F, -2.5F, 9.0F, 12.0F, 5.0F, 0.4F, false);

        inner_body = new ModelRenderer(this);
        inner_body.setRotationPoint(0.0F, 11.0F, 0.0F);
        bipedBody.addChild(inner_body);
        inner_body.setTextureOffset(0, 51).addBox(-4.0F, -6.0F, -2.0F, 8.0F, 9.0F, 4.0F, 0.5F, false);

        left_shoulderpad = new ModelRenderer(this);
        left_shoulderpad.setRotationPoint(5.0F, 4.0F, 0.0F);
        bipedLeftArm.addChild(left_shoulderpad);
        left_shoulderpad.setTextureOffset(30, 33).addBox(-6.0F, -7.0F, -3.0F, 5.0F, 7.0F, 6.0F, 0.3F, false);

        left_spike = new ModelRenderer(this);
        left_spike.setRotationPoint(-1.0F, -8.5F, 0.0F);
        left_shoulderpad.addChild(left_spike);
        setRotateAngle(left_spike, 0.0F, 0.0F, 0.6109F);
        left_spike.setTextureOffset(21, 43).addBox(-1.0F, -3.5F, 0.0F, 4.0F, 7.0F, 0.0F, 0.0F, false);

        side_spike = new ModelRenderer(this);
        side_spike.setRotationPoint(2.5F, 3.0F, 0.5F);
        left_spike.addChild(side_spike);
        setRotateAngle(side_spike, 0.0F, 0.0F, 0.829F);
        side_spike.setTextureOffset(30, 47).addBox(0.5F, -3.5F, -0.5F, 2.0F, 4.0F, 0.0F, 0.0F, false);

        right_shoulderpad = new ModelRenderer(this);
        right_shoulderpad.setRotationPoint(-4.0F, 4.0F, 0.0F);
        bipedRightArm.addChild(right_shoulderpad);
        right_shoulderpad.setTextureOffset(30, 33).addBox(0.0F, -7.0F, -3.0F, 5.0F, 7.0F, 6.0F, 0.3F, true);

        right_spike = new ModelRenderer(this);
        right_spike.setRotationPoint(0.0F, -8.5F, 0.0F);
        right_shoulderpad.addChild(right_spike);
        setRotateAngle(right_spike, 0.0F, 0.0F, -0.6109F);
        right_spike.setTextureOffset(21, 43).addBox(-3.0F, -3.5F, 0.0F, 4.0F, 7.0F, 0.0F, 0.0F, true);

        side_spike2 = new ModelRenderer(this);
        side_spike2.setRotationPoint(-2.5F, 3.0F, 0.5F);
        right_spike.addChild(side_spike2);
        setRotateAngle(side_spike2, 0.0F, 0.0F, -0.829F);
        side_spike2.setTextureOffset(30, 47).addBox(-2.5F, -3.5F, -0.5F, 2.0F, 4.0F, 0.0F, 0.0F, true);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of();
    }

    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.leftWing,
                this.rightWing,
                this.bipedBody,
                this.bipedLeftArm,
                this.bipedRightArm);
    }


    public ModelIgnitium_Elytra_Chestplate withAnimations(LivingEntity entity){
        float partialTick = Minecraft.getInstance().getRenderPartialTicks();
        float limbSwingAmount = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTick;
        float limbSwing = entity.limbSwing + partialTick;
        setRotationAngles(entity, limbSwing, limbSwingAmount, entity.ticksExisted + partialTick, 0, 0);
        return this;
    }

    public void setRotationAngles(LivingEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float f = 0.2617994F;
        float f1 = -0.2617994F;
        float f2 = 0.0F;
        float f3 = 0.0F;
        if (entityIn.isElytraFlying()) {
            float f4 = 1.0F;
            Vector3d vector3d = entityIn.getMotion();
            if (vector3d.y < 0.0D) {
                Vector3d vector3d1 = vector3d.normalize();
                f4 = 1.0F - (float)Math.pow(-vector3d1.y, 1.5D);
            }

            f = f4 * 0.34906584F + (1.0F - f4) * f;
            f1 = f4 * (-(float)Math.PI / 2F) + (1.0F - f4) * f1;
        } else if (entityIn.isCrouching()) {
            f = 0.6981317F;
            f1 = (-(float)Math.PI / 4F);
            f2 = 3.0F;
            f3 = 0.08726646F;
        }

        this.leftWing.rotationPointX = 5.0F;
        this.leftWing.rotationPointY = f2;
        if (entityIn instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractclientplayerentity = (AbstractClientPlayerEntity)entityIn;
            abstractclientplayerentity.rotateElytraX = (float)((double)abstractclientplayerentity.rotateElytraX + (double)(f - abstractclientplayerentity.rotateElytraX) * 0.1D);
            abstractclientplayerentity.rotateElytraY = (float)((double)abstractclientplayerentity.rotateElytraY + (double)(f3 - abstractclientplayerentity.rotateElytraY) * 0.1D);
            abstractclientplayerentity.rotateElytraZ = (float)((double)abstractclientplayerentity.rotateElytraZ + (double)(f1 - abstractclientplayerentity.rotateElytraZ) * 0.1D);
            this.leftWing.rotateAngleX = abstractclientplayerentity.rotateElytraX;
            this.leftWing.rotateAngleY = abstractclientplayerentity.rotateElytraY;
            this.leftWing.rotateAngleZ = abstractclientplayerentity.rotateElytraZ;
        } else {
            this.leftWing.rotateAngleX = f;
            this.leftWing.rotateAngleZ = f1;
            this.leftWing.rotateAngleY = f3;
        }

        this.rightWing.rotationPointX = -this.leftWing.rotationPointX;
        this.rightWing.rotateAngleY = -this.leftWing.rotateAngleY;
        this.rightWing.rotationPointY = this.leftWing.rotationPointY;
        this.rightWing.rotateAngleX = this.leftWing.rotateAngleX;
        this.rightWing.rotateAngleZ = -this.leftWing.rotateAngleZ;

    }
}
