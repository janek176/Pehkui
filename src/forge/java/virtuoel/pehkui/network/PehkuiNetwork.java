package virtuoel.pehkui.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/** Forge SimpleNetworkWrapper channel. Packet registration is added with sync. */
public final class PehkuiNetwork {
    public static final SimpleNetworkWrapper CHANNEL =
        NetworkRegistry.INSTANCE.newSimpleChannel("pehkui");

    private PehkuiNetwork() {}

    public static void init() {
        // Keep channel creation centralized; message IDs are registered in the
        // synchronization implementation to keep them stable and collision-free.
    }
}
