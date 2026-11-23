package virtuoel.pehkui.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Definition, bounds and ordered value modifiers for one scale. */
public final class ScaleType {
    private final float defaultScale;
    private final float minScale;
    private final float maxScale;
    private final int transitionTicks;
    private final List<ScaleModifier> modifiers = new ArrayList<ScaleModifier>();

    public ScaleType(float defaultScale, float minScale, float maxScale) {
        this(defaultScale, minScale, maxScale, 10);
    }

    public ScaleType(float defaultScale, float minScale, float maxScale, int transitionTicks) {
        if (!Float.isFinite(defaultScale) || !Float.isFinite(minScale) ||
            !Float.isFinite(maxScale) || minScale <= 0.0F || maxScale < minScale ||
            defaultScale < minScale || defaultScale > maxScale || transitionTicks < 0) {
            throw new IllegalArgumentException("Invalid scale type definition");
        }
        this.defaultScale = defaultScale;
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.transitionTicks = transitionTicks;
    }

    public float getDefaultScale() { return defaultScale; }
    public float getMinScale() { return minScale; }
    public float getMaxScale() { return maxScale; }
    public int getTransitionTicks() { return transitionTicks; }

    public synchronized ScaleType addModifier(ScaleModifier modifier) {
        if (modifier == null) throw new IllegalArgumentException("Modifier cannot be null");
        modifiers.add(modifier);
        Collections.sort(modifiers, new Comparator<ScaleModifier>() {
            @Override
            public int compare(ScaleModifier a, ScaleModifier b) {
                return Integer.compare(a.getPriority(), b.getPriority());
            }
        });
        return this;
    }

    public synchronized List<ScaleModifier> getModifiers() {
        return Collections.unmodifiableList(new ArrayList<ScaleModifier>(modifiers));
    }

    public float applyModifiers(float scale) {
        float result = scale;
        for (ScaleModifier modifier : getModifiers()) {
            result = modifier.modify(result);
            if (!Float.isFinite(result)) return defaultScale;
        }
        return clamp(result);
    }

    public float clamp(float scale) {
        if (Float.isNaN(scale)) return defaultScale;
        if (scale == Float.POSITIVE_INFINITY) return maxScale;
        if (scale == Float.NEGATIVE_INFINITY) return minScale;
        return Math.max(minScale, Math.min(maxScale, scale));
    }
}
