package virtuoel.pehkui.api;

/**
 * Immutable scale type definition. Modifiers and easing are added in subsequent
 * porting steps; min/max/default semantics are established here.
 */
public final class ScaleType {
    private final float defaultScale;
    private final float minScale;
    private final float maxScale;

    public ScaleType(float defaultScale, float minScale, float maxScale) {
        if (!Float.isFinite(defaultScale) || !Float.isFinite(minScale) ||
            !Float.isFinite(maxScale) || minScale <= 0.0F || maxScale < minScale ||
            defaultScale < minScale || defaultScale > maxScale) {
            throw new IllegalArgumentException("Invalid scale bounds");
        }
        this.defaultScale = defaultScale;
        this.minScale = minScale;
        this.maxScale = maxScale;
    }

    public float getDefaultScale() { return defaultScale; }
    public float getMinScale() { return minScale; }
    public float getMaxScale() { return maxScale; }

    public float clamp(float scale) {
        if (Float.isNaN(scale)) return defaultScale;
        return Math.max(minScale, Math.min(maxScale, scale));
    }
}
