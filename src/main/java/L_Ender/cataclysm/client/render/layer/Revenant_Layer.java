package L_Ender.cataclysm.client.render.layer;

import L_Ender.cataclysm.client.model.entity.ModelIgnited_Revenant;
import L_Ender.cataclysm.client.render.entity.RendererIgnited_Revenant;
import L_Ender.cataclysm.entity.Ignis_Entity;
import L_Ender.cataclysm.entity.Ignited_Revenant_Entity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Revenant_Layer extends LayerRenderer<Ignited_Revenant_Entity, ModelIgnited_Revenant> {

    private final ModelIgnited_Revenant model = new ModelIgnited_Revenant();

    private static final ResourceLocation REVENANT_SHIELD = new ResourceLocation("cataclysm:textures/entity/revenant_shield.png");


    public Revenant_Layer(RendererIgnited_Revenant renderIgnis) {
        super(renderIgnis);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, Ignited_Revenant_Entity revenant, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getEntityModel().copyModelAttributesTo(this.model);
        this.model.setRotationAngles(revenant, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        IVertexBuilder lvt_13_1_ = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(REVENANT_SHIELD));
        this.model.render(matrixStackIn, lvt_13_1_, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}