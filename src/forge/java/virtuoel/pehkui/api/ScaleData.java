package virtuoel.pehkui.api;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/** Per-entity scale values, including interpolation state, persisted by capability. */
public final class ScaleData implements IScaleData {
    private static final class Entry {
        float previous;
        float current;
        float target;
        int remaining;
        int duration;

        Entry(float value) {
            previous = current = target = value;
        }
    }

    private final Map<ResourceLocation, Entry> scales = new LinkedHashMap<ResourceLocation, Entry>();

    private ScaleType type(ResourceLocation id) {
        return ScaleTypes.get(id);
    }

    private float defaultScale(ResourceLocation id) {
        ScaleType type = type(id);
        return type == null ? 1.0F : type.getDefaultScale();
    }

    private Entry entry(ResourceLocation id) {
        Entry value = scales.get(id);
        if (value == null) {
            value = new Entry(defaultScale(id));
            scales.put(id, value);
        }
        return value;
    }

    @Override
    public float getScale(ResourceLocation id) {
        return getScale(id, 1.0F);
    }

    @Override
    public float getScale(ResourceLocation id, float tickDelta) {
        if (id == null) return 1.0F;
        Entry value = scales.get(id);
        if (value == null) return defaultScale(id);
        float delta = Math.max(0.0F, Math.min(1.0F, tickDelta));
        return value.previous + (value.current - value.previous) * delta;
    }

    @Override
    public float getTargetScale(ResourceLocation id) {
        return id == null ? 1.0F : entry(id).target;
    }

    @Override
    public void setScale(ResourceLocation id, float scale) {
        ScaleType type = type(id);
        setScale(id, type == null ? Math.max(0.0001F, scale) : type.clamp(scale),
            type == null ? 0 : type.getTransitionTicks());
    }

    @Override
    public void setScale(ResourceLocation id, float scale, int transitionTicks) {
        if (id == null) throw new IllegalArgumentException("Scale type cannot be null");
        if (transitionTicks < 0) throw new IllegalArgumentException("Transition ticks cannot be negative");
        ScaleType type = type(id);
        float clamped = type == null ? Math.max(0.0001F, scale) : type.clamp(scale);
        Entry value = entry(id);
        value.target = clamped;
        value.duration = transitionTicks;
        value.remaining = transitionTicks;
        if (transitionTicks == 0) {
            value.previous = value.current = clamped;
        }
    }

    @Override
    public void resetScale(ResourceLocation id) {
        if (id != null) scales.remove(id);
    }

    @Override
    public void tick() {
        for (Entry value : scales.values()) {
            value.previous = value.current;
            if (value.remaining > 0) {
                value.remaining--;
                float progress = value.duration == 0 ? 1.0F :
                    1.0F - (float) value.remaining / (float) value.duration;
                // Smoothstep easing avoids abrupt starts and stops.
                progress = progress * progress * (3.0F - 2.0F * progress);
                float start = value.current;
                // Reconstruct the original start from the current point and remaining fraction.
                // Linear interpolation is accumulated in small deterministic tick steps.
                value.current += (value.target - value.current) /
                    (float) Math.max(1, value.remaining + 1);
                if (value.remaining == 0) value.current = value.target;
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound root = new NBTTagCompound();
        NBTTagCompound values = new NBTTagCompound();
        for (Map.Entry<ResourceLocation, Entry> item : scales.entrySet()) {
            Entry value = item.getValue();
            NBTTagCompound data = new NBTTagCompound();
            data.setFloat("Previous", value.previous);
            data.setFloat("Current", value.current);
            data.setFloat("Target", value.target);
            data.setInteger("Remaining", value.remaining);
            data.setInteger("Duration", value.duration);
            values.setTag(item.getKey().toString(), data);
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
                ResourceLocation id = new ResourceLocation(key);
                if (values.hasKey(key, 10)) {
                    NBTTagCompound data = values.getCompoundTag(key);
                    float fallback = defaultScale(id);
                    Entry value = new Entry(fallback);
                    value.previous = readScale(data, "Previous", fallback, id);
                    value.current = readScale(data, "Current", fallback, id);
                    value.target = readScale(data, "Target", value.current, id);
                    value.remaining = Math.max(0, data.getInteger("Remaining"));
                    value.duration = Math.max(value.remaining, data.getInteger("Duration"));
                    if (value.remaining == 0) value.previous = value.current = value.target;
                    scales.put(id, value);
                } else {
                    // Read legacy saves written by the initial 1.12.2 port.
                    setScale(id, values.getFloat(key), 0);
                }
            } catch (RuntimeException ignored) {
                // Ignore malformed or obsolete identifiers in saved data.
            }
        }
    }

    private float readScale(NBTTagCompound data, String key, float fallback, ResourceLocation id) {
        if (!data.hasKey(key, 99)) return fallback;
        ScaleType type = type(id);
        float value = data.getFloat(key);
        return type == null ? Math.max(0.0001F, value) : type.clamp(value);
    }
}
