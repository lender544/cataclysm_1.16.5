package L_Ender.cataclysm.client.render.entity;


import L_Ender.cataclysm.client.model.entity.ModelIgnis;
import L_Ender.cataclysm.client.render.RenderUtils;
import L_Ender.cataclysm.client.render.layer.Ignis_Armor_Crack_Layer;
import L_Ender.cataclysm.client.render.layer.Ignis_Shield_Layer;
import L_Ender.cataclysm.entity.Ignis_Entity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererIgnis extends MobRenderer<Ignis_Entity, ModelIgnis> {

    private static final ResourceLocation IGNIS_TEXTURES = new ResourceLocation("cataclysm:textures/entity/ignis/ignis.png");

    private static final ResourceLocation IGNIS_SOUL_TEXTURES = new ResourceLocation("cataclysm:textures/entity/ignis/ignis_soul.png");

    public RendererIgnis(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ModelIgnis(), 1.0F);
        this.addLayer(new Ignis_Armor_Crack_Layer(this));
        this.addLayer(new Ignis_Shield_Layer(this));

    }
    @Override
    public ResourceLocation getEntityTexture(Ignis_Entity entity) {
        return entity.getBossPhase() > 0 ? IGNIS_SOUL_TEXTURES : IGNIS_TEXTURES;
    }

    @Override
    public void render(Ignis_Entity entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        if (entity.getAnimation() == Ignis_Entity.HORIZONTAL_SWING_ATTACK
                || entity.getAnimation() == Ignis_Entity.SWING_ATTACK
                || entity.getAnimation() == Ignis_Entity.HORIZONTAL_SWING_ATTACK_SOUL
                || entity.getAnimation() == Ignis_Entity.SWING_ATTACK_SOUL
                || entity.getAnimation() == Ignis_Entity.BURNS_THE_EARTH
                || entity.getAnimation() == Ignis_Entity.TRIPLE_ATTACK
                || entity.getAnimation() == Ignis_Entity.PHASE_3
                || entity.getAnimation() == Ignis_Entity.FOUR_COMBO
                || entity.getAnimation() == Ignis_Entity.STRIKE
                || entity.getAnimation() == Ignis_Entity.SWING_UPPERSLASH) {
            Vector3d bladePos = RenderUtils.getWorldPosFromModel(entity, entityYaw, entityModel.blade2);
            entity.setSocketPosArray(0, bladePos);
        }
    }

    protected int getBlockLight(Ignis_Entity entityIn, BlockPos pos) {
        return 15;
    }

    @Override
    protected float getDeathMaxRotation(Ignis_Entity entity) {
        return 0;
    }

}