package L_Ender.cataclysm.entity.projectile;

import L_Ender.cataclysm.config.CMConfig;
import L_Ender.cataclysm.entity.Netherite_Monstrosity_Entity;
import L_Ender.cataclysm.entity.partentity.Netherite_Monstrosity_Part;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class Lava_Bomb_Entity extends ThrowableEntity {


    public double prevMotionX, prevMotionY, prevMotionZ;

    public Lava_Bomb_Entity(EntityType type, World world) {
        super(type, world);
    }

    public Lava_Bomb_Entity(EntityType type, World world, LivingEntity thrower) {
        super(type, thrower, world);
    }


    @Override
    protected void registerData() {

    }

    @Override
    protected void onImpact(RayTraceResult result) {
        RayTraceResult.Type raytraceresult$type = result.getType();
        if (raytraceresult$type == RayTraceResult.Type.ENTITY) {
            this.onEntityHit((EntityRayTraceResult) result);
        } else if (raytraceresult$type == RayTraceResult.Type.BLOCK) {
            this.func_230299_a_((BlockRayTraceResult) result);
        }
    }

    protected void onEntityHit(EntityRayTraceResult result) {
        super.onEntityHit(result);
        Entity shooter = this.getShooter();
        if (!this.world.isRemote && !(result.getEntity() instanceof Lava_Bomb_Entity || result.getEntity() instanceof Netherite_Monstrosity_Part || result.getEntity() instanceof Netherite_Monstrosity_Entity)) {
            this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 1.5f, 0.75f);
            this.world.createExplosion(shooter, this.getPosX(), this.getPosY(), this.getPosZ(), CMConfig.Lavabombradius, Explosion.Mode.NONE);
            this.doTerrainEffects();
            remove();
        }
    }

    protected void func_230299_a_(BlockRayTraceResult result) {
        super.func_230299_a_(result);
        Entity shooter = this.getShooter();
        if(!this.world.isRemote) {
            this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 1.5f, 0.75f);
            this.world.createExplosion(shooter, this.getPosX(), this.getPosY(), this.getPosZ(), CMConfig.Lavabombradius, Explosion.Mode.NONE);
            this.doTerrainEffects();
            remove();
        }
    }


    private void doTerrainEffects() {

        final int range = 0;

        int ix = MathHelper.floor(this.lastTickPosX);
        int iy = MathHelper.floor(this.lastTickPosY);
        int iz = MathHelper.floor(this.lastTickPosZ);

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = new BlockPos(ix + x, iy + y, iz + z);
                    this.doTerrainEffect(pos);
                }
            }
        }
    }


    @Override
    public void tick() {
        super.tick();
        prevMotionX = getMotion().x;
        prevMotionY = getMotion().y;
        prevMotionZ = getMotion().z;

        rotationYaw = -((float) MathHelper.atan2(getMotion().x, getMotion().z)) * (180F / (float)Math.PI);
        makeTrail();
    }

    private void doTerrainEffect(BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getMaterial() == Material.WATER) {
            this.world.setBlockState(pos, Blocks.STONE.getDefaultState());
        }
        if (this.world.isAirBlock(pos) && Blocks.LAVA.getDefaultState().isValidPosition(this.world, pos)) {
            this.world.setBlockState(pos, Blocks.LAVA.getDefaultState());
        }
    }


    public void makeTrail() {
        for (int i = 0; i < 5; i++) {
            double dx = getPosX() + 1.5F * (rand.nextFloat() - 0.5F);
            double dy = getPosY() + 1.5F * (rand.nextFloat() - 0.5F);
            double dz = getPosZ() + 1.5F * (rand.nextFloat() - 0.5F);

            world.addParticle(ParticleTypes.FLAME, dx, dy, dz, -getMotion().getX(), -getMotion().getY(), -getMotion().getZ());
        }
    }

    public float getBrightness() {
        return 1.0F;
    }

    @Override
    protected float getGravityVelocity() {
        return 0.025F;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}