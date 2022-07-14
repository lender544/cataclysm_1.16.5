package L_Ender.cataclysm.client.render.layer;

import L_Ender.cataclysm.client.model.entity.ModelIgnis;
import L_Ender.cataclysm.client.render.entity.RendererIgnis;
import L_Ender.cataclysm.entity.Ignis_Entity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class Ignis_Shield_Crack_Layer extends LayerRenderer<Ignis_Entity, ModelIgnis> {

    private static final ResourceLocation IGNIS_SHIELD_CRACK1 = new ResourceLocation("cataclysm:textures/entity/ignis/ignis_shield_crack1.png");

    private static final ResourceLocation IGNIS_SHIELD_CRACK2 = new ResourceLocation("cataclysm:textures/entity/ignis/ignis_shield_crack2.png");

    private static final ResourceLocation IGNIS_SHIELD_CRACK3 = new ResourceLocation("cataclysm:textures/entity/ignis/ignis_shield_crack3.png");

    public Ignis_Shield_Crack_Layer(RendererIgnis renderIn) {
        super(renderIn);
    }
    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, Ignis_Entity ignis, float p_117152_, float p_117153_, float p_117154_, float p_117155_, float p_117156_, float p_117157_) {
        if (!ignis.isInvisible()) {
            if (ignis.getShieldDurability() == 1) {
                renderCutoutModel(this.getEntityModel(), IGNIS_SHIELD_CRACK1, matrixStackIn, bufferIn, packedLightIn, ignis, 1.0F, 1.0F, 1.0F);
            }
            if (ignis.getShieldDurability() == 2) {
                renderCutoutModel(this.getEntityModel(), IGNIS_SHIELD_CRACK2, matrixStackIn, bufferIn, packedLightIn, ignis, 1.0F, 1.0F, 1.0F);
            }
            if (ignis.getShieldDurability() >= 3) {
                renderCutoutModel(this.getEntityModel(), IGNIS_SHIELD_CRACK3, matrixStackIn, bufferIn, packedLightIn, ignis, 1.0F, 1.0F, 1.0F);
            }
        }

    }
}