package L_Ender.cataclysm.message;

import L_Ender.cataclysm.cataclysm;
import L_Ender.cataclysm.tileentities.TileEntityAltarOfFire;
import com.github.alexthe666.citadel.server.message.PacketBufferUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageUpdateblockentity {

    public long blockPos;
    public ItemStack heldStack;

    public MessageUpdateblockentity(long blockPos, ItemStack heldStack) {
        this.blockPos = blockPos;
        this.heldStack = heldStack;

    }

    public MessageUpdateblockentity() {
    }

    public static MessageUpdateblockentity read(PacketBuffer buf) {
        return new MessageUpdateblockentity(buf.readLong(), PacketBufferUtils.readItemStack(buf));
    }

    public static void write(MessageUpdateblockentity message, PacketBuffer buf) {
        buf.writeLong(message.blockPos);
        PacketBufferUtils.writeItemStack(buf, message.heldStack);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageUpdateblockentity message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            PlayerEntity player = context.get().getSender();
            if(context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT){
                player = cataclysm.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                if (player.world != null) {
                    BlockPos pos = BlockPos.fromLong(message.blockPos);
                    if (player.world.getTileEntity(pos) != null) {
                        if (player.world.getTileEntity(pos) instanceof TileEntityAltarOfFire) {
                            TileEntityAltarOfFire podium = (TileEntityAltarOfFire) player.world.getTileEntity(pos);
                            podium.setInventorySlotContents(0, message.heldStack);
                        }
                    }
                }
            }
        }
    }

}