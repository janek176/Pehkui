package virtuoel.pehkui.capability;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.ResourceLocation;
import virtuoel.pehkui.Pehkui;
import virtuoel.pehkui.api.IScaleData;

@Mod.EventBusSubscriber(modid = Pehkui.MOD_ID)
public final class ScaleCapabilityEvents {
    private static final ResourceLocation KEY = new ResourceLocation(Pehkui.MOD_ID, "scale_data");

    private ScaleCapabilityEvents() {}

    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(KEY, new ScaleDataCapability.Provider());
    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        IScaleData oldData = event.getOriginal().getCapability(ScaleDataCapability.CAPABILITY, null);
        IScaleData newData = event.getEntity().getCapability(ScaleDataCapability.CAPABILITY, null);
        if (oldData != null && newData != null) {
            newData.deserializeNBT(oldData.serializeNBT());
        }
    }
}
