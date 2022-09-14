package L_Ender.cataclysm.message;

import L_Ender.cataclysm.entity.partentity.Cm_Part_Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MessageCMMultipart {

    private int id;
    private PacketBuffer buffer;
    private Entity entity;

    public MessageCMMultipart(PacketBuffer buf) {
        id = buf.readInt();
        buffer = buf;
    }

    public MessageCMMultipart(Entity entity) {
        this.entity = entity;
    }

    public void encode(PacketBuffer buf) {
        try {
            buf.writeInt(entity.getEntityId());
            PartEntity<?>[] parts = entity.getParts();
            // We assume the client and server part arrays are identical, else everything will crash and burn. Don't even bother handling it.
            if (parts != null) {
                for (PartEntity<?> part : parts) {
                    if (part instanceof Cm_Part_Entity) {
                        Cm_Part_Entity<?> tfPart = (Cm_Part_Entity<?>) part;
                        tfPart.writeData(buf);
                        boolean dirty = tfPart.getDataManager().isDirty();
                        buf.writeBoolean(dirty);
                        if (dirty)
                            EntityDataManager.writeEntries(tfPart.getDataManager().getDirty(), buf);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Handler {
        public static boolean onMessage(MessageCMMultipart message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(new Runnable() {
                @Override
                public void run() {
                    try {
                        World world = Minecraft.getInstance().world;
                        if (world == null)
                            return;
                        Entity ent = world.getEntityByID(message.id);
                        if (ent != null && ent.isMultipartEntity()) {
                            PartEntity<?>[] parts = ent.getParts();
                            if (parts == null)
                                return;
                            for (PartEntity<?> part : parts) {
                                if (part instanceof Cm_Part_Entity) {
                                    Cm_Part_Entity<?> tfPart = (Cm_Part_Entity<?>) part;
                                    tfPart.readData(message.buffer);
                                    if (message.buffer.readBoolean()) {
                                        List<EntityDataManager.DataEntry<?>> data = EntityDataManager.readEntries(message.buffer);
                                        if (data != null)
                                            tfPart.getDataManager().setEntryValues(data);
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return true;
        }
    }
}
