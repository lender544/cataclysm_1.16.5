package L_Ender.cataclysm.client.render.entity;

import L_Ender.cataclysm.client.model.entity.ModelIgnited_Revenant;
import L_Ender.cataclysm.client.render.CMRenderTypes;
import L_Ender.cataclysm.client.render.layer.Revenant_Layer;
import L_Ender.cataclysm.entity.Ignited_Revenant_Entity;
import L_Ender.cataclysm.entity.Nameless_Sorcerer_Entity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class RendererIgnited_Revenant extends MobRenderer<Ignited_Revenant_Entity, ModelIgnited_Revenant> {

    private static final ResourceLocation IGNITED_REVENANT_TEXTURES = new ResourceLocation("cataclysm:textures/entity/revenant_body.png");
    private static final ResourceLocation IGNITED_REVENANT_LAYER_TEXTURES = new ResourceLocation("cataclysm:textures/entity/revenant_layer.png");
    private final Random rnd = new Random();

    public RendererIgnited_Revenant(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelIgnited_Revenant(), 0.5F);
        this.addLayer(new Ignited_Revenant_GlowLayer(this));
        this.addLayer(new Revenant_Layer(this));

    }
    @Override
    public ResourceLocation getEntityTexture(Ignited_Revenant_Entity entity) {
        return IGNITED_REVENANT_TEXTURES;
    }

    @Override
    protected void preRenderCallback(Ignited_Revenant_Entity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.1F, 1.1F, 1.1F);
    }
    public Vector3d getRenderOffset(Ignited_Revenant_Entity entityIn, float partialTicks) {
        if (entityIn.getAnimation() == Ignited_Revenant_Entity.ASH_BREATH_ATTACK && entityIn.getAnimationTick() >= 28 && entityIn.getAnimationTick() <= 43) {
            double d0 = 0.02D;
            return new Vector3d(this.rnd.nextGaussian() * d0, 0.0D, this.rnd.nextGaussian() * d0);
        } else {
            return super.getRenderOffset(entityIn, partialTicks);
        }
    }
    static class Ignited_Revenant_GlowLayer extends LayerRenderer<Ignited_Revenant_Entity, ModelIgnited_Revenant> {
        public Ignited_Revenant_GlowLayer(RendererIgnited_Revenant p_i50928_1_) {
            super(p_i50928_1_);
        }

        @Override
        public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, Ignited_Revenant_Entity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(CMRenderTypes.getFlickering(IGNITED_REVENANT_LAYER_TEXTURES, 0));
            float alpha = 0.5F + (MathHelper.cos(ageInTicks * 0.2F) + 1F) * 0.2F;
            this.getEntityModel().render(matrixStackIn, ivertexbuilder, 240, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);

        }
    }

}
