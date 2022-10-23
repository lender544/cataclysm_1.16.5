package L_Ender.cataclysm.structures;

import L_Ender.cataclysm.cataclysm;
import L_Ender.cataclysm.entity.Ignited_Revenant_Entity;
import L_Ender.cataclysm.entity.Netherite_Monstrosity_Entity;
import L_Ender.cataclysm.init.ModEntities;
import L_Ender.cataclysm.init.ModStructures;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class BurningArenaPieces {

    private static final ResourceLocation ARENA1 = new ResourceLocation(cataclysm.MODID, "burning_arena1");
    private static final ResourceLocation ARENA2 = new ResourceLocation(cataclysm.MODID, "burning_arena2");
    private static final ResourceLocation ARENA3 = new ResourceLocation(cataclysm.MODID, "burning_arena3");
    private static final ResourceLocation ARENA4 = new ResourceLocation(cataclysm.MODID, "burning_arena4");
    private static final ResourceLocation ARENA5 = new ResourceLocation(cataclysm.MODID, "burning_arena5");
    private static final ResourceLocation ARENA6 = new ResourceLocation(cataclysm.MODID, "burning_arena6");
    private static final ResourceLocation ARENA7 = new ResourceLocation(cataclysm.MODID, "burning_arena7");
    private static final ResourceLocation ARENA8 = new ResourceLocation(cataclysm.MODID, "burning_arena8");
    private static final Map<ResourceLocation, BlockPos> OFFSET = ImmutableMap.<ResourceLocation, BlockPos>builder()
            .put(ARENA1, new BlockPos(0, 1, 0))
            .put(ARENA2, new BlockPos(0, 1, 0))
            .put(ARENA3, new BlockPos(0, 1, 0))
            .put(ARENA4, new BlockPos(0, 1, 0))
            .put(ARENA5, new BlockPos(0, 1, 0))
            .put(ARENA6, new BlockPos(0, 1, 0))
            .put(ARENA7, new BlockPos(0, 1, 0))
            .put(ARENA8, new BlockPos(0, 1, 0))
            .build();

    /*
     * Begins assembling your structure and where the pieces needs to go.
     */
    public static void start(TemplateManager templateManager, BlockPos pos, Rotation rotation, List<StructurePiece> pieceList, Random random) {
        int x = pos.getX();
        int z = pos.getZ();

        BlockPos rotationOffSet = new BlockPos(0, 0, 0).rotate(rotation);
        BlockPos blockpos = rotationOffSet.add(x, pos.getY(), z);
        pieceList.add(new BurningArenaPieces.Piece(templateManager, ARENA1, blockpos, rotation));

        rotationOffSet = new BlockPos(0, 0, 38).rotate(rotation);
        blockpos = rotationOffSet.add(x, pos.getY(), z);
        pieceList.add(new BurningArenaPieces.Piece(templateManager, ARENA2, blockpos, rotation));

        rotationOffSet = new BlockPos(47, 0, 0).rotate(rotation);
        blockpos = rotationOffSet.add(x, pos.getY(), z);
        pieceList.add(new BurningArenaPieces.Piece(templateManager, ARENA3, blockpos, rotation));

        rotationOffSet = new BlockPos(47, 0, 38).rotate(rotation);
        blockpos = rotationOffSet.add(x, pos.getY(), z);
        pieceList.add(new BurningArenaPieces.Piece(templateManager, ARENA4, blockpos, rotation));

        rotationOffSet = new BlockPos(0, 48, 0).rotate(rotation);
        blockpos = rotationOffSet.add(x, pos.getY(), z);
        pieceList.add(new BurningArenaPieces.Piece(templateManager, ARENA5, blockpos, rotation));

        rotationOffSet = new BlockPos(0, 48, 38).rotate(rotation);
        blockpos = rotationOffSet.add(x, pos.getY(), z);
        pieceList.add(new BurningArenaPieces.Piece(templateManager, ARENA6, blockpos, rotation));

        rotationOffSet = new BlockPos(47, 48, 0).rotate(rotation);
        blockpos = rotationOffSet.add(x, pos.getY(), z);
        pieceList.add(new BurningArenaPieces.Piece(templateManager, ARENA7, blockpos, rotation));

        rotationOffSet = new BlockPos(47, 48, 38).rotate(rotation);
        blockpos = rotationOffSet.add(x, pos.getY(), z);
        pieceList.add(new BurningArenaPieces.Piece(templateManager, ARENA8, blockpos, rotation));
    }

    /*
     * Here's where some voodoo happens. Most of this doesn't need to be touched but you do
     * have to pass in the IStructurePieceType you registered into the super constructors.
     *
     * The method you will most likely want to touch is the handleDataMarker method.
     */
    public static class Piece extends TemplateStructurePiece {
        private ResourceLocation resourceLocation;
        private Rotation rotation;

        public Piece(TemplateManager templateManagerIn, ResourceLocation resourceLocationIn, BlockPos pos, Rotation rotationIn) {
            super(ModStructures.BAP, 0);
            this.resourceLocation = resourceLocationIn;
            BlockPos blockpos = BurningArenaPieces.OFFSET.get(resourceLocation);
            this.templatePosition = pos.add(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            this.rotation = rotationIn;
            this.setupPiece(templateManagerIn);
        }

        public Piece(TemplateManager templateManagerIn, CompoundNBT tagCompound) {
            super(ModStructures.BAP, tagCompound);
            this.resourceLocation = new ResourceLocation(tagCompound.getString("Template"));
            this.rotation = Rotation.valueOf(tagCompound.getString("Rot"));
            this.setupPiece(templateManagerIn);
        }

        private void setupPiece(TemplateManager templateManager) {
            Template template = templateManager.getTemplateDefaulted(this.resourceLocation);
            PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE);
            this.setup(template, this.templatePosition, placementsettings);
        }

        /**
         * (abstract) Helper method to read subclass data from NBT
         */
        @Override
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putString("Template", this.resourceLocation.toString());
            tagCompound.putString("Rot", this.rotation.name());
        }

        /*
         * If you added any data marker structure blocks to your structure, you can access and modify them here.
         * In this case, our structure has a data maker with the string "chest" put into it. So we check to see
         * if the incoming function is "chest" and if it is, we now have that exact position.
         *
         * So what is done here is we replace the structure block with
         * a chest and we can then set the loottable for it.
         *
         * You can set other data markers to do other behaviors such as spawn a random mob in a certain spot,
         * randomize what rare block spawns under the floor, or what item an Item Frame will have.
         */
        @Override
        protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb) {
            if ("revenant".equals(function)) {
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                Ignited_Revenant_Entity revenant = ModEntities.IGNITED_REVENANT.get().create(worldIn.getWorld());
                revenant.moveToBlockPosAndAngles(pos, 180.0F, 180.0F);
                worldIn.addEntity(revenant);
            }
        }
    }
}
