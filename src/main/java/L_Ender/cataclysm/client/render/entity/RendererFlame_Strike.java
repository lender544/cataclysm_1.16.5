package L_Ender.cataclysm.client.render.entity;

import L_Ender.cataclysm.client.render.CMRenderTypes;
import L_Ender.cataclysm.entity.effect.Flame_Strike_Entity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererFlame_Strike extends EntityRenderer<Flame_Strike_Entity> {
    public static final ResourceLocation FLAME_STRIKE = new ResourceLocation("cataclysm:textures/entity/flame_strike_sigil.png");
    public static final ResourceLocation SOUL_FLAME_STRIKE = new ResourceLocation("cataclysm:textures/entity/soul_flame_strike_sigil.png");

    public RendererFlame_Strike(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public ResourceLocation getEntityTexture(Flame_Strike_Entity entity) {
        return entity.isSoul() ? SOUL_FLAME_STRIKE : FLAME_STRIKE;
    }


    @Override
    public void render(Flame_Strike_Entity flameStrike, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        float f2 = (float) flameStrike.ticksExisted + partialTicks;
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(CMRenderTypes.getBright(this.getEntityTexture(flameStrike)));
        matrixStackIn.scale(flameStrike.getRadius(), flameStrike.getRadius(), flameStrike.getRadius());
        matrixStackIn.translate(0.0D, 0.001D, 0.0D);
        if(flameStrike.isSoul()) {
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f2));
        }else{
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0F - flameStrike.rotationYaw + f2));
        }
        MatrixStack.Entry lvt_19_1_ = matrixStackIn.getLast();
        Matrix4f lvt_20_1_ = lvt_19_1_.getMatrix();
        Matrix3f lvt_21_1_ = lvt_19_1_.getNormal();
        if(flameStrike.isSee()) {
            this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, -1, 0, 0, 1, 0, 1, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, -1, 0, 1, 0, 1, 1, 0, 1, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, 1, 1, 1, 1, 0, 1, 240);
            this.drawVertex(lvt_20_1_, lvt_21_1_, ivertexbuilder, 1, 0, -1, 1, 0, 1, 0, 1, 240);
        }
        matrixStackIn.pop();
        super.render(flameStrike, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }


    public void drawVertex(Matrix4f p_229039_1_, Matrix3f p_229039_2_, IVertexBuilder p_229039_3_, int p_229039_4_, int p_229039_5_, int p_229039_6_, float p_229039_7_, float p_229039_8_, int p_229039_9_, int p_229039_10_, int p_229039_11_, int p_229039_12_) {
        p_229039_3_.pos(p_229039_1_, (float) p_229039_4_, (float) p_229039_5_, (float) p_229039_6_).color(255, 255, 255, 255).tex(p_229039_7_, p_229039_8_).overlay(OverlayTexture.NO_OVERLAY).lightmap(p_229039_12_).normal(p_229039_2_, (float) p_229039_9_, (float) p_229039_11_, (float) p_229039_10_).endVertex();
    }
}
