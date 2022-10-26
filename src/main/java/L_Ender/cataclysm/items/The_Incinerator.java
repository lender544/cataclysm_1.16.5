package L_Ender.cataclysm.items;

import L_Ender.cataclysm.entity.effect.Charge_Watcher_Entity;
import L_Ender.cataclysm.entity.effect.Flame_Strike_Entity;
import L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import L_Ender.cataclysm.init.ModSounds;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class The_Incinerator extends Item {
    private final Multimap<Attribute, AttributeModifier> guantletAttributes;


    public The_Incinerator(Properties group) {
        super(group);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 11.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", -2.8F, AttributeModifier.Operation.ADDITION));
        this.guantletAttributes = builder.build();
    }


    public UseAction getUseAction(ItemStack p_77661_1_) {
        return UseAction.BOW;
    }

    public int getUseDuration(ItemStack p_77626_1_) {
        return 72000;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            player.setActiveHand(hand);
            return ActionResult.resultConsume(player.getHeldItem(hand));
        } else {
            return ActionResult.resultFail(player.getHeldItem(hand));
        }
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            int i = this.getUseDuration(stack) - timeLeft;
            int standingOnY = MathHelper.floor(entityLiving.getPosY()) - 1;
            double headY = entityLiving.getPosY() + 1.0D;
            float yawRadians = (float) (Math.toRadians(90 + entityLiving.rotationYaw));
            boolean hasSucceeded = false;
            if (i >= 60) {
                for (int l = 0; l < 10; l++) {
                    double d2 = 2.25D * (double) (l + 1);
                    int j2 = (int) (1.5F * l);
                    if (this.spawnFlameStrike(entityLiving.getPosX() + (double) MathHelper.cos(yawRadians) * d2, entityLiving.getPosZ() + (double) MathHelper.sin(yawRadians) * d2, (double) standingOnY, headY, yawRadians, 40, j2, j2, worldIn, 1F, (PlayerEntity) entityLiving)) {
                        hasSucceeded = true;
                    }
                }
                if (hasSucceeded) {
                    if (!worldIn.isRemote) {
                        ((PlayerEntity) entityLiving).getCooldownTracker().setCooldown(this, 400);
                    }
                    ScreenShake_Entity.ScreenShake(worldIn, entityLiving.getPositionVec(), 30, 0.15f, 0, 30);
                    entityLiving.playSound(ModSounds.SWORD_STOMP.get(), 1.0F, 1.0f);
                }
            }
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        int i = this.getUseDuration(stack) - count;
        if (i == 60) {
            player.playSound(ModSounds.FLAME_BURST.get(), 1.0F, 1.0f);
        }
    }

    private boolean spawnFlameStrike(double x, double z, double minY, double maxY, float rotation, int duration, int wait, int delay, World world, float radius, LivingEntity player) {
        BlockPos blockpos = new BlockPos(x, maxY, z);
        boolean flag = false;
        double d0 = 0.0D;

        do {
            BlockPos blockpos1 = blockpos.down();
            BlockState blockstate = world.getBlockState(blockpos1);
            if (blockstate.isSolidSide(world, blockpos1, Direction.UP)) {
                if (!world.isAirBlock(blockpos)) {
                    BlockState blockstate1 = world.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(world, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.getEnd(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            blockpos = blockpos.down();
        } while (blockpos.getY() >= minY);

        if (flag) {
            world.addEntity(new Flame_Strike_Entity(world, x, (double) blockpos.getY() + d0, z, rotation, duration, wait, delay, radius, false, player));
            return true;
        }
        return false;
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 16;
    }

    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment.type != EnchantmentType.BREAKABLE && enchantment.type == EnchantmentType.WEAPON && enchantment != Enchantments.SWEEPING;
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == EquipmentSlotType.MAINHAND ? this.guantletAttributes : super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.cataclysm.incinerator.desc").mergeStyle(TextFormatting.DARK_GREEN));
        tooltip.add(new TranslationTextComponent("item.cataclysm.incinerator2.desc").mergeStyle(TextFormatting.DARK_GREEN));
        tooltip.add(new TranslationTextComponent("item.cataclysm.incinerator3.desc").mergeStyle(TextFormatting.DARK_GREEN));
    }
}