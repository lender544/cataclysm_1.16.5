package L_Ender.cataclysm.client.render.blockentity;

import L_Ender.cataclysm.client.model.block.Model_Altar_of_Fire;
import L_Ender.cataclysm.client.render.CMRenderTypes;
import L_Ender.cataclysm.tileentities.TileEntityAltarOfFire;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.SeparatePerspectiveModel;


public class RendererAltar_of_Fire<T extends TileEntityAltarOfFire> extends TileEntityRenderer<T> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("cataclysm:textures/blocks/altar_of_fire/altar_of_fire.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("cataclysm:textures/blocks/altar_of_fire/altarfire1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("cataclysm:textures/blocks/altar_of_fire/altarfire2.png");
    private static final ResourceLocation TEXTURE_3 = new ResourceLocation("cataclysm:textures/blocks/altar_of_fire/altarfire3.png");
    private static final ResourceLocation TEXTURE_4 = new ResourceLocation("cataclysm:textures/blocks/altar_of_fire/altarfire4.png");
    private static final Model_Altar_of_Fire MODEL = new Model_Altar_of_Fire();

    public RendererAltar_of_Fire(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0.5F, 1.5F, 0.5F);
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
        MODEL.animate(tileEntityIn, partialTicks);;
        MODEL.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(TEXTURE)), combinedLightIn, combinedOverlayIn, 1, 1F, 1, 1);
        MODEL.render(matrixStackIn, bufferIn.getBuffer(CMRenderTypes.getGlowingEffect(getIdleTexture(tileEntityIn.ticksExisted % 12))), 210, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        matrixStackIn.pop();
        renderItem(tileEntityIn, partialTicks,matrixStackIn,bufferIn,combinedLightIn);

    }
    private ResourceLocation getIdleTexture(int age) {
        if (age < 3) {
            return TEXTURE_1;
        } else if (age < 6) {
            return TEXTURE_2;
        } else if (age < 9) {
            return TEXTURE_3;
        } else if (age < 12) {
            return TEXTURE_4;
        } else {
            return TEXTURE_1;
        }
    }

    public void renderItem(T tileEntityIn,float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn) {
        ItemStack stack = tileEntityIn.getStackInSlot(0);
        float f2 = (float) tileEntityIn.ticksExisted + partialTicks;
        if (!stack.isEmpty()) {
            matrixStackIn.push();
            matrixStackIn.translate(0.5F, 1.0F, 0.5F);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f2));
            IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, tileEntityIn.getWorld(), (LivingEntity) null);
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
            matrixStackIn.pop();
        }
    }
}


