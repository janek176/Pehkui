package virtuoel.pehkui.api;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/** Per-entity scale values persisted through Forge capabilities. */
public final class ScaleData implements IScaleData {
    private final Map<ResourceLocation, Float> scales = new LinkedHashMap<ResourceLocation, Float>();

    @Override
    public float getScale(ResourceLocation type) {
        ScaleType scaleType = ScaleTypes.get(type);
        float fallback = scaleType == null ? 1.0F : scaleType.getDefaultScale();
        Float value = scales.get(type);
        return value == null ? fallback : scaleType == null ? value : scaleType.clamp(value);
    }

    @Override
    public void setScale(ResourceLocation type, float scale) {
        if (type == null) throw new IllegalArgumentException("Scale type cannot be null");
        ScaleType scaleType = ScaleTypes.get(type);
        scales.put(type, scaleType == null ? Math.max(0.0001F, scale) : scaleType.clamp(scale));
    }

    @Override
    public void resetScale(ResourceLocation type) {
        scales.remove(type);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound root = new NBTTagCompound();
        NBTTagCompound values = new NBTTagCompound();
        for (Map.Entry<ResourceLocation, Float> entry : scales.entrySet()) {
            values.setFloat(entry.getKey().toString(), entry.getValue());
        }
        root.setTag("Scales", values);
        return root;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        scales.clear();
        if (!tag.hasKey("Scales", 10)) return;
        NBTTagCompound values = tag.getCompoundTag("Scales");
        for (String key : values.getKeySet()) {
            try {
                setScale(new ResourceLocation(key), values.getFloat(key));
            } catch (RuntimeException ignored) {
                // Ignore malformed or obsolete identifiers in saved data.
            }
        }
    }
}
