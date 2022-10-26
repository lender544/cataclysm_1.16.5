package L_Ender.cataclysm.items;

import L_Ender.cataclysm.entity.effect.Charge_Watcher_Entity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Bulwark_of_the_flame extends Item {
    public Bulwark_of_the_flame(Item.Properties group) {
        super(group);
    }

    public boolean isShield(ItemStack stack, @Nullable LivingEntity entity) {
        return true;
    }

    public UseAction getUseAction(ItemStack p_77661_1_) {
        return UseAction.BLOCK;
    }

    public int getUseDuration(ItemStack p_77626_1_) {
        return 72000;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if(entityLiving.isSneaking()) {
            if(!entityLiving.isElytraFlying()) {
                int i = this.getUseDuration(stack) - timeLeft;
                int t = MathHelper.clamp(i, 1, 4);
                float f7 = entityLiving.rotationYaw;
                float f = entityLiving.rotationPitch;

                float f1 = -MathHelper.sin(f7 * ((float) Math.PI / 180F)) * MathHelper.cos(f * ((float) Math.PI / 180F));
                float f2 = -MathHelper.sin(f * ((float) Math.PI / 180F));
                float f3 = MathHelper.cos(f7 * ((float) Math.PI / 180F)) * MathHelper.cos(f * ((float) Math.PI / 180F));
                float f4 = MathHelper.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                float f5 = 3.0F * (t / 6.0F);
                f1 *= f5 / f4;
                f3 *= f5 / f4;
                entityLiving.addVelocity((double) f1, (double) 0, (double) f3);
                if (entityLiving.isOnGround()) {
                    float f6 = 1.1999999F;
                    entityLiving.move(MoverType.SELF, new Vector3d(0.0D, (double) f6 / 2, 0.0D));
                }
                if (!worldIn.isRemote) {
                    Charge_Watcher_Entity initializer = new Charge_Watcher_Entity(entityLiving.world, entityLiving.getPosition(), t * 2,
                            t * 0.25, 2, 0.5F,
                            f1 * 0.5F, f3 * 0.5F,
                            entityLiving);
                    worldIn.addEntity(initializer);
                    ((PlayerEntity) entityLiving).getCooldownTracker().setCooldown(this, 80);
                }
            }
        }
    }

    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack lvt_4_1_ = player.getHeldItem(hand);
        player.setActiveHand(hand);
        return ActionResult.resultConsume(lvt_4_1_);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.cataclysm.bulwark_of_the_flame.desc").mergeStyle(TextFormatting.DARK_GREEN));
        tooltip.add(new TranslationTextComponent("item.cataclysm.bulwark_of_the_flame2.desc").mergeStyle(TextFormatting.DARK_GREEN));
    }
}