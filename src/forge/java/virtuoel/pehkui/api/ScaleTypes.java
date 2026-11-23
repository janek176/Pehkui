package virtuoel.pehkui.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

/**
 * Built-in scale type registry for the Forge 1.12.2 port.
 * The registry intentionally uses ResourceLocation, matching the 1.12.2 API.
 */
public final class ScaleTypes {
    private static final Map<ResourceLocation, ScaleType> TYPES =
        new LinkedHashMap<ResourceLocation, ScaleType>();

    public static ScaleType BASE;
    public static ScaleType WIDTH;
    public static ScaleType HEIGHT;
    public static ScaleType EYE_HEIGHT;

    private ScaleTypes() {}

    public static void registerDefaults() {
        if (BASE != null) {
            return;
        }
        BASE = register("base", new ScaleType(1.0F, 0.0625F, 16.0F));
        WIDTH = register("width", new ScaleType(1.0F, 0.0625F, 16.0F));
        HEIGHT = register("height", new ScaleType(1.0F, 0.0625F, 16.0F));
        EYE_HEIGHT = register("eye_height", new ScaleType(1.0F, 0.0625F, 16.0F));
    }

    public static ScaleType register(String path, ScaleType type) {
        ResourceLocation id = new ResourceLocation("pehkui", path);
        if (TYPES.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate scale type: " + id);
        }
        TYPES.put(id, type);
        return type;
    }

    public static ScaleType get(ResourceLocation id) {
        return TYPES.get(id);
    }

    public static Map<ResourceLocation, ScaleType> values() {
        return Collections.unmodifiableMap(TYPES);
    }
}
