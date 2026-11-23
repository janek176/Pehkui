package virtuoel.pehkui;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;
import virtuoel.pehkui.api.ScaleTypes;
import virtuoel.pehkui.network.PehkuiNetwork;

@Mod(
    modid = Pehkui.MOD_ID,
    name = Pehkui.MOD_NAME,
    version = Pehkui.VERSION,
    acceptedMinecraftVersions = "[1.12.2]",
    dependencies = "required-after:forge"
)
public final class Pehkui {
    public static final String MOD_ID = "pehkui";
    public static final String MOD_NAME = "Pehkui";
    public static final String VERSION = "1.12.2-forge-dev";

    @Mod.Instance(MOD_ID)
    public static Pehkui INSTANCE;

    private static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        PehkuiNetwork.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ScaleTypes.registerDefaults();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        // Commands will be registered here after the scale API is ported.
    }

    public static Logger getLogger() {
        return logger;
    }
}
