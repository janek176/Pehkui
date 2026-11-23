package virtuoel.pehkui.api;

/**
 * A composable transformation applied to a scale value.
 * Implementations should be deterministic and must not mutate their input.
 */
public interface ScaleModifier {
    float modify(float scale);

    default int getPriority() {
        return 0;
    }
}
