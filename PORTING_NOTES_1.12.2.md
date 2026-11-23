# Pehkui Forge 1.12.2 port — engineering notes

This branch is a porting workspace. The upstream implementation on the parent branch targets Minecraft 1.21 (with metadata covering 1.14.4–1.21) and Fabric; it is not a drop-in 1.12.2 Forge project.

## Preserve as much of the public API as practical

The first porting milestone should retain the conceptual API and behavior of:

- `ScaleData`: base/previous/initial/target scale, tick interpolation, easing, persistence, modifiers, NBT serialization.
- `ScaleType`: defaults, clamp functions, modifiers, and dimension-affecting behavior.
- `ScaleModifier`: priority ordering and current/previous-scale transformations.
- `ScaleRegistries` and `ScaleTypes`: identifiers and built-in scale categories.

Avoid replacing this model with a single scale field or potion effect. That would lose compatibility and important behavior.

## Minecraft 1.12.2 API differences confirmed from the MCP source

The 1.12.2 source uses:

- `net.minecraft.util.ResourceLocation` instead of `Identifier`.
- `net.minecraft.nbt.NBTTagCompound`, `NBTTagList`, and numeric tag classes instead of modern NBT names.
- `Entity.world` and `World.isRemote`; `Entity#getEntityWorld()` also exists.
- `Entity.width`, `Entity.height`, `Entity#setSize(float, float)`, `Entity#setPosition(double, double, double)`, and `Entity#setEntityBoundingBox(AxisAlignedBB)`.
- `Entity#onUpdate()` / `Entity#onEntityUpdate()` for entity ticking.
- `Entity#readFromNBT(NBTTagCompound)` and `Entity#writeToNBT(NBTTagCompound)` for persistence.
- `Entity#getEyeHeight()` returns `height * 0.85F` in the base class.

Relevant source: `src/minecraft/net/minecraft/entity/Entity.java` in the companion MCP 1.12.2 source repository.

## Loader integration plan

Fabric-specific event and networking calls must be replaced, not mechanically renamed.

Proposed Forge 1.12.2 integration:

1. Store per-entity scale state in an Entity Capability, attached to all entities.
2. Serialize capability state through Forge capability NBT hooks.
3. Tick scale interpolation from the Forge entity tick lifecycle, with careful server/client separation.
4. Synchronize scale state with a Forge `SimpleNetworkWrapper` channel and send updates to tracking clients.
5. Add a dedicated entity-size integration layer. It must update collision bounds and eye height consistently without repeatedly scaling an already-scaled bounding box.
6. Keep rendering, reach, movement, and other gameplay effects as separate integrations built on top of the scale API.

The exact bytecode hook strategy for collision/dimensions is not yet selected. Forge 1.12.2 does not provide the modern Fabric Mixin integration used by upstream, so this needs a deliberate compatibility decision (for example, a narrowly scoped coremod/ASM hook or a verified Mixin bootstrap arrangement).

## Initial implementation order

1. Establish a clean Forge 1.12.2 build and metadata.
2. Port the API/data model and registries with no Minecraft hooks.
3. Add entity capability storage and NBT persistence.
4. Add server-authoritative scale synchronization.
5. Implement and test bounding-box scaling.
6. Add eye height, movement, rendering, reach, and other scale types incrementally.

Do not claim feature parity until multiplayer, save/reload, collision, player dimensions, and third-party entity compatibility have been tested.
