package L_Ender.cataclysm.client.render.layer;

import L_Ender.cataclysm.client.model.entity.ModelIgnis;
import L_Ender.cataclysm.client.render.entity.RendererIgnis;
import L_Ender.cataclysm.entity.Ignis_Entity;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;


@OnlyIn(Dist.CLIENT)
public class Ignis_Armor_Crack_Layer extends LayerRenderer<Ignis_Entity, ModelIgnis> {

    private static final Map<Ignis_Entity.Crackiness, ResourceLocation> resourceLocations = ImmutableMap.of(
            Ignis_Entity.Crackiness.LOW, new ResourceLocation("cataclysm:textures/entity/ignis/ignis_armor_crack1.png"),
            Ignis_Entity.Crackiness.MEDIUM, new ResourceLocation("cataclysm:textures/entity/ignis/ignis_armor_crack2.png"),
            Ignis_Entity.Crackiness.HIGH, new ResourceLocation("cataclysm:textures/entity/ignis/ignis_armor_crack3.png"));

    public Ignis_Armor_Crack_Layer(RendererIgnis renderIn) {
        super(renderIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, Ignis_Entity ignis, float p_117152_, float p_117153_, float p_117154_, float p_117155_, float p_117156_, float p_117157_) {
        if (!ignis.isInvisible()) {
            if(ignis.getBossPhase() > 0){
                Ignis_Entity.Crackiness ignis$crackiness = ignis.getCrackiness();
                if (ignis$crackiness != Ignis_Entity.Crackiness.NONE) {
                    ResourceLocation resourcelocation = resourceLocations.get(ignis$crackiness);
                    renderCutoutModel(this.getEntityModel(), resourcelocation, matrixStackIn, bufferIn, packedLightIn, ignis, 1.0F, 1.0F, 1.0F);
                }
            }
        }

    }
}