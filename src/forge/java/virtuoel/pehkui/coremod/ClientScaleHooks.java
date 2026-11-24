package virtuoel.pehkui.coremod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import virtuoel.pehkui.api.IScaleData;
import virtuoel.pehkui.capability.ScaleDataCapability;

/** Client-only hooks for player controller reach calculations. */
public final class ClientScaleHooks {
    private static final ResourceLocation BASE = new ResourceLocation("pehkui", "base");
    private static final ResourceLocation REACH = new ResourceLocation("pehkui", "reach");
    private static final ResourceLocation BLOCK_REACH = new ResourceLocation("pehkui", "block_reach");

    private ClientScaleHooks() {}

    public static float transformBlockReach(float vanillaReach) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return vanillaReach;
        IScaleData data = ScaleDataCapability.get(player);
        if (data == null) return vanillaReach;
        return vanillaReach * safe(data.getScale(BASE))
            * safe(data.getScale(REACH)) * safe(data.getScale(BLOCK_REACH));
    }

    private static float safe(float value) {
        return Float.isNaN(value) || Float.isInfinite(value) || value <= 0.0F
            ? 1.0F : Math.max(0.0001F, Math.min(64.0F, value));
    }
}
