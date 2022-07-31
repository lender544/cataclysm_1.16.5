package L_Ender.cataclysm.client.render.layer;

import L_Ender.cataclysm.client.model.entity.ModelIgnis;
import L_Ender.cataclysm.client.render.entity.RendererIgnis;
import L_Ender.cataclysm.entity.Ignis_Entity;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class Ignis_Shield_Layer extends LayerRenderer<Ignis_Entity, ModelIgnis> {

    private final ModelIgnis model = new ModelIgnis();

    private static final ResourceLocation IGNIS_SHIELD = new ResourceLocation("cataclysm:textures/entity/ignis/ignis_shield.png");

    private static final ResourceLocation IGNIS_SOUL_SHIELD = new ResourceLocation("cataclysm:textures/entity/ignis/ignis_soul_shield.png");

    private static final ResourceLocation IGNIS_SHIELD_CRACK1 = new ResourceLocation("cataclysm:textures/entity/ignis/ignis_shield_crack1.png");

    private static final ResourceLocation IGNIS_SHIELD_CRACK2 = new ResourceLocation("cataclysm:textures/entity/ignis/ignis_shield_crack2.png");

    private static final ResourceLocation IGNIS_SHIELD_CRACK3 = new ResourceLocation("cataclysm:textures/entity/ignis/ignis_shield_crack3.png");


    public Ignis_Shield_Layer(RendererIgnis renderIgnis) {
        super(renderIgnis);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, Ignis_Entity ignis, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation lvt_12_3_;
        if (ignis.getBossPhase() < 1) {
            lvt_12_3_ = IGNIS_SHIELD;
        } else {
            lvt_12_3_ = IGNIS_SOUL_SHIELD;
        }
        this.getEntityModel().copyModelAttributesTo(this.model);
        this.model.setRotationAngles(ignis, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        IVertexBuilder lvt_13_1_ = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(lvt_12_3_));
        this.model.render(matrixStackIn, lvt_13_1_, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        if (ignis.getShieldDurability() > 0) {
            IVertexBuilder lvt_13_2_ =
                    ignis.getShieldDurability() >= 3 ? bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(IGNIS_SHIELD_CRACK3))
                            : ignis.getShieldDurability() == 2 ? bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(IGNIS_SHIELD_CRACK2))
                            : bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(IGNIS_SHIELD_CRACK1));

            this.model.render(matrixStackIn, lvt_13_2_, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}