package virtuoel.pehkui.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.nbt.NBTTagCompound;

public interface IScaleData {
    float getScale(ResourceLocation type);
    void setScale(ResourceLocation type, float scale);
    void resetScale(ResourceLocation type);
    NBTTagCompound serializeNBT();
    void deserializeNBT(NBTTagCompound tag);
}
