package virtuoel.pehkui.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

/** Built-in Pehkui scale type registry for Forge 1.12.2. */
public final class ScaleTypes {
    private static final Map<ResourceLocation, ScaleType> TYPES =
        new LinkedHashMap<ResourceLocation, ScaleType>();

    public static ScaleType BASE;
    public static ScaleType WIDTH;
    public static ScaleType HEIGHT;
    public static ScaleType EYE_HEIGHT;
    public static ScaleType HITBOX_WIDTH;
    public static ScaleType HITBOX_HEIGHT;
    public static ScaleType INTERACTION_BOX_WIDTH;
    public static ScaleType INTERACTION_BOX_HEIGHT;
    public static ScaleType MODEL_WIDTH;
    public static ScaleType MODEL_HEIGHT;
    public static ScaleType THIRD_PERSON;
    public static ScaleType MOTION;
    public static ScaleType FALLING;
    public static ScaleType STEP_HEIGHT;
    public static ScaleType VIEW_BOBBING;
    public static ScaleType VISIBILITY;
    public static ScaleType JUMP_HEIGHT;
    public static ScaleType FLIGHT;
    public static ScaleType REACH;
    public static ScaleType BLOCK_REACH;
    public static ScaleType ENTITY_REACH;
    public static ScaleType MINING_SPEED;
    public static ScaleType ATTACK_SPEED;
    public static ScaleType KNOCKBACK;
    public static ScaleType ATTACK;
    public static ScaleType DEFENSE;
    public static ScaleType HEALTH;
    public static ScaleType DROPS;
    public static ScaleType HELD_ITEM;
    public static ScaleType PROJECTILES;
    public static ScaleType EXPLOSIONS;

    private ScaleTypes() {}

    public static void registerDefaults() {
        if (BASE != null) return;
        BASE = register("base", new ScaleType(1.0F, 0.0625F, 16.0F));
        WIDTH = register("width", new ScaleType(1.0F, 0.0625F, 16.0F));
        HEIGHT = register("height", new ScaleType(1.0F, 0.0625F, 16.0F));
        EYE_HEIGHT = register("eye_height", new ScaleType(1.0F, 0.0625F, 16.0F));
        HITBOX_WIDTH = register("hitbox_width", new ScaleType(1.0F, 0.0625F, 16.0F));
        HITBOX_HEIGHT = register("hitbox_height", new ScaleType(1.0F, 0.0625F, 16.0F));
        INTERACTION_BOX_WIDTH = register("interaction_box_width", new ScaleType(1.0F, 0.0625F, 16.0F));
        INTERACTION_BOX_HEIGHT = register("interaction_box_height", new ScaleType(1.0F, 0.0625F, 16.0F));
        MODEL_WIDTH = register("model_width", new ScaleType(1.0F, 0.0625F, 16.0F));
        MODEL_HEIGHT = register("model_height", new ScaleType(1.0F, 0.0625F, 16.0F));
        THIRD_PERSON = register("third_person", new ScaleType(1.0F, 0.0625F, 16.0F));
        MOTION = register("motion", new ScaleType(1.0F, 0.0625F, 16.0F));
        FALLING = register("falling", new ScaleType(1.0F, 0.0625F, 16.0F));
        STEP_HEIGHT = register("step_height", new ScaleType(1.0F, 0.0625F, 16.0F));
        VIEW_BOBBING = register("view_bobbing", new ScaleType(1.0F, 0.0625F, 16.0F));
        VISIBILITY = register("visibility", new ScaleType(1.0F, 0.0625F, 16.0F));
        JUMP_HEIGHT = register("jump_height", new ScaleType(1.0F, 0.0625F, 16.0F));
        FLIGHT = register("flight", new ScaleType(1.0F, 0.0625F, 16.0F));
        REACH = register("reach", new ScaleType(1.0F, 0.0625F, 16.0F));
        BLOCK_REACH = register("block_reach", new ScaleType(1.0F, 0.0625F, 16.0F));
        ENTITY_REACH = register("entity_reach", new ScaleType(1.0F, 0.0625F, 16.0F));
        MINING_SPEED = register("mining_speed", new ScaleType(1.0F, 0.0625F, 16.0F));
        ATTACK_SPEED = register("attack_speed", new ScaleType(1.0F, 0.0625F, 16.0F));
        KNOCKBACK = register("knockback", new ScaleType(1.0F, 0.0625F, 16.0F));
        ATTACK = register("attack", new ScaleType(1.0F, 0.0625F, 16.0F));
        DEFENSE = register("defense", new ScaleType(1.0F, 0.0625F, 16.0F));
        HEALTH = register("health", new ScaleType(1.0F, 0.0625F, 16.0F));
        DROPS = register("drops", new ScaleType(1.0F, 0.0625F, 16.0F));
        HELD_ITEM = register("held_item", new ScaleType(1.0F, 0.0625F, 16.0F));
        PROJECTILES = register("projectiles", new ScaleType(1.0F, 0.0625F, 16.0F));
        EXPLOSIONS = register("explosions", new ScaleType(1.0F, 0.0625F, 16.0F));
    }

    public static ScaleType register(String path, ScaleType type) {
        if (path == null || type == null) throw new IllegalArgumentException("Scale id and type are required");
        ResourceLocation id = new ResourceLocation("pehkui", path);
        if (TYPES.containsKey(id)) throw new IllegalArgumentException("Duplicate scale type: " + id);
        TYPES.put(id, type);
        return type;
    }

    public static ScaleType get(ResourceLocation id) { return TYPES.get(id); }

    public static Map<ResourceLocation, ScaleType> values() {
        return Collections.unmodifiableMap(TYPES);
    }
}
