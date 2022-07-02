package L_Ender.cataclysm.client.model.entity;

import L_Ender.cataclysm.entity.projectile.Void_Rune_Entity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelVoid_Rune extends AdvancedEntityModel<Void_Rune_Entity> {
    private final AdvancedModelBox root;

    public ModelVoid_Rune() {
        textureWidth = 64;
        textureHeight = 64;

        root = new AdvancedModelBox(this);
        root.setRotationPoint(0.0F, 41.0F, 0.0F);
        root.setTextureOffset(0, 0).addBox(-3.0F, -16.0F, -3.0F, 6.0F, 16.0F, 6.0F, 0.0F, false);
        root.setTextureOffset(20, 18).addBox(-5.0F, -11.0F, -2.0F, 2.0F, 11.0F, 4.0F, 0.0F, false);
        root.setTextureOffset(18, 0).addBox(3.0F, -3.0F, -1.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setRotationAngles(Void_Rune_Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTick = Minecraft.getInstance().getRenderPartialTicks();
        float activateProgress = entityIn.prevactivateProgress + (entityIn.activateProgress - entityIn.prevactivateProgress) * partialTick;
        progressPositionPrev(root, activateProgress,0, -17, 0, 10f);
    }

    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(this.root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root);
    }

}