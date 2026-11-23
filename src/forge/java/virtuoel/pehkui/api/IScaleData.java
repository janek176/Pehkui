package virtuoel.pehkui.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public interface IScaleData {
    float getScale(ResourceLocation type);
    float getScale(ResourceLocation type, float tickDelta);
    float getTargetScale(ResourceLocation type);
    void setScale(ResourceLocation type, float scale);
    void setScale(ResourceLocation type, float scale, int transitionTicks);
    void resetScale(ResourceLocation type);
    void tick();
    NBTTagCompound serializeNBT();
    void deserializeNBT(NBTTagCompound tag);
}
