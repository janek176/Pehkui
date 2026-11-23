package virtuoel.pehkui.api;

/** Immutable definition and bounds for one independently addressable scale. */
public final class ScaleType {
    private final float defaultScale;
    private final float minScale;
    private final float maxScale;
    private final int transitionTicks;

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

    public float clamp(float scale) {
        if (Float.isNaN(scale)) return defaultScale;
        if (scale == Float.POSITIVE_INFINITY) return maxScale;
        if (scale == Float.NEGATIVE_INFINITY) return minScale;
        return Math.max(minScale, Math.min(maxScale, scale));
    }
}
