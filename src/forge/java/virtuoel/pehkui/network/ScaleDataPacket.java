package virtuoel.pehkui.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import virtuoel.pehkui.capability.ScaleDataCapability;

/** Server-to-client snapshot of one entity's scale capability. */
public final class ScaleDataPacket implements IMessage {
    private int entityId;
    private NBTTagCompound data;

    public ScaleDataPacket() {}

    public ScaleDataPacket(Entity entity, NBTTagCompound data) {
        this.entityId = entity.getEntityId();
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        data = ByteBufUtils.readTag(buf);
        if (data == null) data = new NBTTagCompound();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeTag(buf, data == null ? new NBTTagCompound() : data);
    }

    public static final class Handler implements IMessageHandler<ScaleDataPacket, IMessage> {
        @Override
        public IMessage onMessage(final ScaleDataPacket message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    if (Minecraft.getMinecraft().world == null) return;
                    Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
                    if (entity == null) return;
                    if (entity.hasCapability(ScaleDataCapability.CAPABILITY, null)) {
                        entity.getCapability(ScaleDataCapability.CAPABILITY, null)
                            .deserializeNBT(message.data);
                    }
                }
            });
            return null;
        }
    }
}
