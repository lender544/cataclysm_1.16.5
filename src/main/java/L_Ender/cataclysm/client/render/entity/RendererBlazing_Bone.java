package L_Ender.cataclysm.client.render.entity;

import L_Ender.cataclysm.entity.projectile.Blazing_Bone_Entity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class RendererBlazing_Bone extends EntityRenderer<Blazing_Bone_Entity> {

    public RendererBlazing_Bone(EntityRendererManager mgr) {
        super(mgr);
    }
    @Override
    public void render(Blazing_Bone_Entity entity, float yaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
        stack.push();
        float spin = (entity.ticksExisted + partialTicks) * 30F;
        // size up
        stack.scale(1.25F, 1.25F, 1.25F);
        this.renderDroppedItem(stack, buffer, light, entity.getItem(), yaw, spin);
        stack.pop();
    }

    private void renderDroppedItem(MatrixStack matrix, IRenderTypeBuffer buffer, int light, ItemStack stack, float rotation, float spin) {
        matrix.push();
        matrix.rotate(Vector3f.YP.rotationDegrees(rotation + 90));
        matrix.rotate(Vector3f.ZP.rotationDegrees(spin));
        matrix.translate(0f, 0f, 0);
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, matrix, buffer);
        matrix.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(Blazing_Bone_Entity p_114632_) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
