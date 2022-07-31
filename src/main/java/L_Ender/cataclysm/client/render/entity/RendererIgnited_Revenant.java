package L_Ender.cataclysm.client.render.entity;

import L_Ender.cataclysm.client.model.entity.ModelIgnited_Revenant;
import L_Ender.cataclysm.client.render.CMRenderTypes;
import L_Ender.cataclysm.entity.Ignited_Revenant_Entity;
import L_Ender.cataclysm.entity.Nameless_Sorcerer_Entity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererIgnited_Revenant extends MobRenderer<Ignited_Revenant_Entity, ModelIgnited_Revenant> {

    private static final ResourceLocation IGNITED_REVENANT_TEXTURES = new ResourceLocation("cataclysm:textures/entity/ignited_revenant.png");
    private static final ResourceLocation IGNITED_REVENANT_LAYER_TEXTURES = new ResourceLocation("cataclysm:textures/entity/ignited_revenant_layer.png");

    public RendererIgnited_Revenant(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelIgnited_Revenant(), 0.5F);
        this.addLayer(new Ignited_Revenant_GlowLayer(this));

    }
    @Override
    public ResourceLocation getEntityTexture(Ignited_Revenant_Entity entity) {
        return IGNITED_REVENANT_TEXTURES;
    }

    @Override
    protected void preRenderCallback(Ignited_Revenant_Entity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.1F, 1.1F, 1.1F);
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
