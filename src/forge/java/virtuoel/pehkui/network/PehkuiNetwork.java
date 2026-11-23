package virtuoel.pehkui.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import virtuoel.pehkui.api.IScaleData;
import virtuoel.pehkui.capability.ScaleDataCapability;

/** Forge SimpleNetworkWrapper channel and scale snapshot helpers. */
public final class PehkuiNetwork {
    public static final SimpleNetworkWrapper CHANNEL =
        NetworkRegistry.INSTANCE.newSimpleChannel("pehkui");

    private PehkuiNetwork() {}

    public static void init() {
        CHANNEL.registerMessage(ScaleDataPacket.Handler.class, ScaleDataPacket.class,
            0, Side.CLIENT);
    }

    public static void syncTracking(Entity entity) {
        IScaleData data = ScaleDataCapability.get(entity);
        if (data != null) {
            CHANNEL.sendToAllTracking(new ScaleDataPacket(entity, data.serializeNBT()), entity);
        }
    }

    public static void syncTo(Entity entity, EntityPlayerMP player) {
        IScaleData data = ScaleDataCapability.get(entity);
        if (data != null) {
            CHANNEL.sendTo(new ScaleDataPacket(entity, data.serializeNBT()), player);
        }
    }
}
