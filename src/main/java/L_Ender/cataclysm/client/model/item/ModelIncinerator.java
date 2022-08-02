package L_Ender.cataclysm.client.model.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;


public class ModelIncinerator extends EntityModel<Entity> {
    private final ModelRenderer root;
    private final ModelRenderer blade;
    private final ModelRenderer blade2;
    private final ModelRenderer blade_mid;
    private final ModelRenderer handle_core;
    private final ModelRenderer core;
    private final ModelRenderer upper_guard;
    private final ModelRenderer lower_guard;

    public ModelIncinerator() {
        textureWidth = 128;
        textureHeight = 128;

        root = new ModelRenderer(this);
        root.setRotationPoint(0.0F, 24.0F, 0.0F);
        root.setTextureOffset(0, 54).addBox(-1.5F, -23.0F, -1.5F, 3.0F, 12.0F, 3.0F, 0.0F, false);
        root.setTextureOffset(49, 53).addBox(-2.5F, -11.0F, -2.5F, 5.0F, 2.0F, 5.0F, 0.0F, false);
        root.setTextureOffset(0, 0).addBox(0.0F, -66.0F, -6.5F, 0.0F, 40.0F, 13.0F, 0.0F, false);
        root.setTextureOffset(27, 0).addBox(-0.5F, -66.0F, -3.5F, 1.0F, 40.0F, 7.0F, 0.0F, false);

        blade = new ModelRenderer(this);
        blade.setRotationPoint(0.0F, -66.0F, 0.0F);
        root.addChild(blade);
        setRotationAngle(blade, -0.7854F, 0.0F, 0.0F);
        blade.setTextureOffset(0, 0).addBox(-0.5F, -2.5F, -2.5F, 1.0F, 5.0F, 5.0F, -0.01F, false);

        blade2 = new ModelRenderer(this);
        blade2.setRotationPoint(0.0F, -66.0F, 0.0F);
        root.addChild(blade2);
        setRotationAngle(blade2, -0.7854F, 0.0F, 0.0F);
        blade2.setTextureOffset(35, 39).addBox(0.0F, -4.5F, -4.5F, 0.0F, 9.0F, 9.0F, 0.0F, false);

        blade_mid = new ModelRenderer(this);
        blade_mid.setRotationPoint(0.0F, -25.8F, 0.0F);
        root.addChild(blade_mid);
        setRotationAngle(blade_mid, 0.7854F, 0.0F, 0.0F);
        blade_mid.setTextureOffset(22, 53).addBox(-1.5F, -2.5F, -2.5F, 3.0F, 5.0F, 5.0F, 0.5F, false);
        blade_mid.setTextureOffset(54, 48).addBox(-1.0F, -0.5F, -5.5F, 2.0F, 1.0F, 3.0F, 0.0F, false);
        blade_mid.setTextureOffset(27, 48).addBox(-1.0F, 2.5F, -0.5F, 2.0F, 3.0F, 1.0F, 0.0F, false);

        handle_core = new ModelRenderer(this);
        handle_core.setRotationPoint(0.0F, -4.5F, 0.0F);
        root.addChild(handle_core);
        setRotationAngle(handle_core, -0.7854F, 0.0F, 0.0F);
        handle_core.setTextureOffset(45, 35).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, 0.7F, false);

        core = new ModelRenderer(this);
        core.setRotationPoint(0.0F, -4.5F, 0.0F);
        root.addChild(core);
        core.setTextureOffset(44, 24).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, 0.0F, false);

        upper_guard = new ModelRenderer(this);
        upper_guard.setRotationPoint(0.0F, -25.5F, 0.9F);
        root.addChild(upper_guard);
        setRotationAngle(upper_guard, -0.1745F, 0.0F, 0.0F);
        upper_guard.setTextureOffset(44, 12).addBox(-1.0F, -3.0F, 4.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        upper_guard.setTextureOffset(37, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
        upper_guard.setTextureOffset(42, 58).addBox(-0.5F, -1.0F, 4.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
        upper_guard.setTextureOffset(60, 40).addBox(-0.5F, -2.0F, 3.0F, 1.0F, 1.0F, 6.0F, 0.0F, false);
        upper_guard.setTextureOffset(56, 12).addBox(-0.5F, -3.0F, 3.0F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        upper_guard.setTextureOffset(60, 21).addBox(-0.5F, -6.0F, 6.0F, 1.0F, 1.0F, 6.0F, 0.0F, false);
        upper_guard.setTextureOffset(13, 54).addBox(-0.5F, -7.0F, 10.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        upper_guard.setTextureOffset(44, 12).addBox(-0.5F, -5.0F, 3.0F, 1.0F, 2.0F, 9.0F, 0.0F, false);

        lower_guard = new ModelRenderer(this);
        lower_guard.setRotationPoint(0.0F, -25.5F, -1.9F);
        root.addChild(lower_guard);
        setRotationAngle(lower_guard, 0.1745F, 0.0F, 0.0F);
        lower_guard.setTextureOffset(27, 0).addBox(-1.0F, -3.0F, -4.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
        lower_guard.setTextureOffset(14, 0).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
        lower_guard.setTextureOffset(14, 7).addBox(-0.5F, -1.0F, -6.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
        lower_guard.setTextureOffset(33, 58).addBox(-0.5F, -2.0F, -8.0F, 1.0F, 1.0F, 6.0F, 0.0F, false);
        lower_guard.setTextureOffset(56, 0).addBox(-0.5F, -3.0F, -9.0F, 1.0F, 1.0F, 7.0F, 0.0F, false);
        lower_guard.setTextureOffset(13, 58).addBox(-0.5F, -6.0F, -11.0F, 1.0F, 1.0F, 6.0F, 0.0F, false);
        lower_guard.setTextureOffset(46, 0).addBox(-0.5F, -7.0F, -11.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        lower_guard.setTextureOffset(44, 0).addBox(-0.5F, -5.0F, -11.0F, 1.0F, 2.0F, 9.0F, 0.0F, false);
    }

    @Override
    public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        //previously the render function, render code was moved to a method below
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        root.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

}