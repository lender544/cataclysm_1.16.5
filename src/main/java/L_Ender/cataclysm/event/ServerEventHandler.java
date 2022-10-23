package L_Ender.cataclysm.event;

import L_Ender.cataclysm.entity.Ignis_Entity;
import L_Ender.cataclysm.init.ModBlocks;
import L_Ender.cataclysm.init.ModEffect;
import L_Ender.cataclysm.init.ModItems;
import L_Ender.cataclysm.items.final_fractal;
import L_Ender.cataclysm.items.zweiender;
import L_Ender.cataclysm.cataclysm;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = cataclysm.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {


    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        int p_45022_ = 2;
        final BlockPos p_45021_ = event.getEntityLiving().getPosition();
        if (!event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET).isEmpty() && event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.FEET).getItem() == ModItems.IGNITIUM_BOOTS.get()) {
            if (!event.getEntityLiving().isSneaking()) {
                if (event.getEntityLiving().isOnGround()) {
                    BlockState blockstate = ModBlocks.MELTING_NETHERRACK.get().getDefaultState();
                    float f = (float) Math.min(16, 2 + p_45022_);
                    BlockPos.Mutable blockpos$mutableblockpos = new BlockPos.Mutable();

                    for (BlockPos blockpos : BlockPos.getAllInBoxMutable(p_45021_.add((double) (-f), -1.0D, (double) (-f)), p_45021_.add((double) f, -1.0D, (double) f))) {
                        if (blockpos.withinDistance(event.getEntityLiving().getPositionVec(), (double) f)) {
                            blockpos$mutableblockpos.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                            BlockState blockstate1 = event.getEntityLiving().world.getBlockState(blockpos$mutableblockpos);
                            if (blockstate1.isAir()) {
                                BlockState blockstate2 = event.getEntityLiving().world.getBlockState(blockpos);
                                boolean isFull = blockstate2.getBlock() == Blocks.LAVA && blockstate2.get(FlowingFluidBlock.LEVEL) == 0; //TODO: Forge, modded waters?
                                if (blockstate2.getMaterial() == Material.LAVA && isFull && blockstate.isValidPosition(event.getEntityLiving().world, blockpos) && event.getEntityLiving().world.placedBlockCollides(blockstate, blockpos, ISelectionContext.dummy()) && !net.minecraftforge.event.ForgeEventFactory.onBlockPlace(event.getEntityLiving(), net.minecraftforge.common.util.BlockSnapshot.create(event.getEntityLiving().world.getDimensionKey(), event.getEntityLiving().world, blockpos), net.minecraft.util.Direction.UP)) {
                                    event.getEntityLiving().world.setBlockState(blockpos, blockstate);
                                    event.getEntityLiving().world.getPendingBlockTicks().scheduleTick(blockpos, ModBlocks.MELTING_NETHERRACK.get(), MathHelper.nextInt(event.getEntityLiving().getRNG(), 60, 120));
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingHurtEvent event) {
        World world = event.getEntityLiving().getEntityWorld();
        if (event.getSource() instanceof EntityDamageSource && event.getSource().getTrueSource() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) event.getSource().getTrueSource();
            LivingEntity target = event.getEntityLiving();
            ItemStack weapon = attacker.getHeldItemMainhand();
            if (weapon != null && weapon.getItem() instanceof zweiender) {
                Vector3d lookDir = new Vector3d(target.getLookVec().x, 0, target.getLookVec().z).normalize();
                Vector3d vecBetween = new Vector3d(target.getPosX() - attacker.getPosX(), 0, target.getPosZ() - attacker.getPosZ()).normalize();
                double dot = lookDir.dotProduct(vecBetween);
                if (dot > 0.05) {
                    event.setAmount(event.getAmount() * 2);
                    target.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.75F, 0.5F);
                }
            }
            if (weapon != null && weapon.getItem() instanceof final_fractal) {
                event.setAmount(event.getAmount() + target.getMaxHealth() * 0.03f);
            }


        }
    }
    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        World world = event.getEntityLiving().getEntityWorld();
        if (!event.getEntityLiving().getActiveItemStack().isEmpty() && event.getSource() != null && event.getSource().getTrueSource() != null) {
            if (event.getEntityLiving().getActiveItemStack().getItem() == ModItems.BULWARK_OF_THE_FLAME.get()) {
                Entity attacker = event.getSource().getTrueSource();
                if (attacker instanceof LivingEntity) {
                    if (attacker.getDistance(event.getEntityLiving()) <= 4 && !attacker.isBurning()) {
                        attacker.setFire(5);

                    }
                }

            }
        }
    }

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        if (event.isCancelable() && event.getEntityLiving().isPotionActive(ModEffect.EFFECTSTUN.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingSetTargetEvent(LivingSetAttackTargetEvent event) {
        if (event.getTarget() != null && event.getEntityLiving() instanceof MobEntity) {
            if (event.getEntityLiving().isPotionActive(ModEffect.EFFECTSTUN.get())) {
                ((MobEntity) event.getEntityLiving()).setAttackTarget(null);
            }
        }
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.getActivePotionEffect(ModEffect.EFFECTSTUN.get()) != null){
            entity.setMotion(entity.getMotion().x, 0.0D, entity.getMotion().z);
        }
    }

    @SubscribeEvent
    public void onPlayerLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        PlayerEntity player = event.getPlayer();
        if (event.isCancelable() && player.isPotionActive(ModEffect.EFFECTSTUN.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onUseItem(LivingEntityUseItemEvent event) {
        LivingEntity living = event.getEntityLiving();
        if (event.isCancelable() && living.isPotionActive(ModEffect.EFFECTSTUN.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            if (event.isCancelable() && living.isPotionActive(ModEffect.EFFECTSTUN.get())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onFillBucket(FillBucketEvent event) {
        LivingEntity living = event.getEntityLiving();
        if (living != null) {
            if (event.isCancelable() && living.isPotionActive(ModEffect.EFFECTSTUN.get())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event) {
        if (event.isCancelable() && event.getPlayer().isPotionActive(ModEffect.EFFECTSTUN.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickEmpty event) {
        if (event.isCancelable() && event.getEntityLiving().isPotionActive(ModEffect.EFFECTSTUN.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.isCancelable() && event.getEntityLiving().isPotionActive(ModEffect.EFFECTSTUN.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.isCancelable() && event.getEntityLiving().isPotionActive(ModEffect.EFFECTSTUN.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCancelable() && event.getEntityLiving().isPotionActive(ModEffect.EFFECTSTUN.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event) {
        if (event.isCancelable() && event.getEntityLiving().isPotionActive(ModEffect.EFFECTSTUN.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.getHealth() <= event.getAmount() && entity.isPotionActive(ModEffect.EFFECTSTUN.get())) {
            entity.removeActivePotionEffect(ModEffect.EFFECTSTUN.get());
        }
        if (!event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.LEGS).isEmpty() && event.getSource() != null && event.getSource().getTrueSource() != null){
            if(event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() == ModItems.IGNITIUM_LEGGINGS.get()){
                Entity attacker = event.getSource().getTrueSource();
                if (attacker instanceof LivingEntity) {
                    if (event.getEntityLiving().getRNG().nextFloat() < 0.5F) {
                        EffectInstance effectinstance1 = ((LivingEntity) attacker).getActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                        int i = 1;
                        if (effectinstance1 != null) {
                            i += effectinstance1.getAmplifier();
                            ((LivingEntity) attacker).removeActivePotionEffect(ModEffect.EFFECTBLAZING_BRAND.get());
                        } else {
                            --i;
                        }

                        i = MathHelper.clamp(i, 0, 2);
                        EffectInstance effectinstance = new EffectInstance(ModEffect.EFFECTBLAZING_BRAND.get(), 100, i, false, false, true);
                        ((LivingEntity) attacker).addPotionEffect(effectinstance);

                        if (!attacker.isBurning()) {
                            attacker.setFire(5);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickItem event) {
        if (event.isCancelable() && event.getEntityLiving().isPotionActive(ModEffect.EFFECTSTUN.get())) {
            event.setCanceled(true);
        }
    }

}


