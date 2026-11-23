package virtuoel.pehkui.coremod;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import virtuoel.pehkui.api.IScaleData;
import virtuoel.pehkui.capability.ScaleDataCapability;

/** Runtime bridge called by the Entity#setSize transformer. */
public final class EntityScaleHooks {
    private static final ResourceLocation BASE = new ResourceLocation("pehkui", "base");
    private static final ResourceLocation WIDTH = new ResourceLocation("pehkui", "width");
    private static final ResourceLocation HEIGHT = new ResourceLocation("pehkui", "height");
    private static final Map<Entity, float[]> BASE_SIZE = new WeakHashMap<Entity, float[]>();
    private static final Map<Entity, float[]> APPLIED_SCALE = new WeakHashMap<Entity, float[]>();

    private EntityScaleHooks() {}

    public static float[] transformSize(Entity entity, float width, float height) {
        synchronized (BASE_SIZE) {
            BASE_SIZE.put(entity, new float[] {width, height});
        }

        IScaleData data = ScaleDataCapability.get(entity);
        if (data == null) {
            return new float[] {width, height};
        }

        float baseScale = safeScale(data.getScale(BASE));
        float widthScale = safeScale(data.getScale(WIDTH)) * baseScale;
        float heightScale = safeScale(data.getScale(HEIGHT)) * baseScale;
        synchronized (APPLIED_SCALE) {
            APPLIED_SCALE.put(entity, new float[] {widthScale, heightScale});
        }
        return new float[] {width * widthScale, height * heightScale};
    }

    /**
     * Re-applies the remembered vanilla dimensions after a scale transition.
     * Called from world tick on both logical sides.
     */
    public static void refresh(Entity entity) {
        if (entity == null || entity.isDead) return;
        float[] base;
        synchronized (BASE_SIZE) {
            base = BASE_SIZE.get(entity);
            if (base == null) return;
            base = base.clone();
        }

        IScaleData data = ScaleDataCapability.get(entity);
        if (data == null) return;

        float baseScale = safeScale(data.getScale(BASE));
        float widthScale = safeScale(data.getScale(WIDTH)) * baseScale;
        float heightScale = safeScale(data.getScale(HEIGHT)) * baseScale;
        synchronized (APPLIED_SCALE) {
            float[] previous = APPLIED_SCALE.get(entity);
            if (previous != null && Float.compare(previous[0], widthScale) == 0
                && Float.compare(previous[1], heightScale) == 0) {
                return;
            }
            APPLIED_SCALE.put(entity, new float[] {widthScale, heightScale});
        }
        entity.setSize(base[0], base[1]);
    }

    private static float safeScale(float value) {
        return Float.isNaN(value) || Float.isInfinite(value) || value <= 0.0F
            ? 1.0F : Math.max(0.0001F, Math.min(64.0F, value));
    }
}
