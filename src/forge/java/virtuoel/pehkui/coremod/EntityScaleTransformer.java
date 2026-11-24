package virtuoel.pehkui.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/** Adds scale-aware hitbox, eye-height and living-model hooks. */
public final class EntityScaleTransformer implements IClassTransformer {
    private static final String ENTITY = "net.minecraft.entity.Entity";
    private static final String LIVING_RENDERER = "net.minecraft.client.renderer.entity.RenderLivingBase";
    private static final String LIVING_BASE = "net.minecraft.entity.EntityLivingBase";
    private static final String PLAYER_CONTROLLER = "net.minecraft.client.multiplayer.PlayerControllerMP";
    private static final String HOOKS = "virtuoel/pehkui/coremod/EntityScaleHooks";
    private static final String ENTITY_DESC = "Lnet/minecraft/entity/Entity;";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return basicClass;
        if (!ENTITY.equals(transformedName) && !LIVING_RENDERER.equals(transformedName) && !LIVING_BASE.equals(transformedName) && !PLAYER_CONTROLLER.equals(transformedName)) return basicClass;

        ClassNode node = new ClassNode();
        new ClassReader(basicClass).accept(node, 0);
        boolean changed = false;

        if (ENTITY.equals(transformedName)) {
            for (MethodNode method : node.methods) {
                if (("setSize".equals(method.name) || "func_70105_a".equals(method.name))
                    && "(FF)V".equals(method.desc)) {
                    int dimensionsLocal = method.maxLocals;
                    InsnList hook = new InsnList();
                    hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    hook.add(new VarInsnNode(Opcodes.FLOAD, 1));
                    hook.add(new VarInsnNode(Opcodes.FLOAD, 2));
                    hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS, "transformSize",
                        "(" + ENTITY_DESC + "FF)[F", false));
                    hook.add(new VarInsnNode(Opcodes.ASTORE, dimensionsLocal));
                    hook.add(new VarInsnNode(Opcodes.ALOAD, dimensionsLocal));
                    hook.add(new InsnNode(Opcodes.ICONST_0));
                    hook.add(new InsnNode(Opcodes.FALOAD));
                    hook.add(new VarInsnNode(Opcodes.FSTORE, 1));
                    hook.add(new VarInsnNode(Opcodes.ALOAD, dimensionsLocal));
                    hook.add(new InsnNode(Opcodes.ICONST_1));
                    hook.add(new InsnNode(Opcodes.FALOAD));
                    hook.add(new VarInsnNode(Opcodes.FSTORE, 2));
                    method.instructions.insert(hook);
                    method.maxLocals = dimensionsLocal + 1;
                    changed = true;
                }

                if (("getEyeHeight".equals(method.name) || "func_70047_e".equals(method.name))
                    && "()F".equals(method.desc)) {
                    int valueLocal = method.maxLocals++;
                    for (AbstractInsnNode instruction = method.instructions.getFirst();
                         instruction != null; instruction = instruction.getNext()) {
                        if (instruction.getOpcode() == Opcodes.FRETURN) {
                            InsnList hook = new InsnList();
                            hook.add(new VarInsnNode(Opcodes.FSTORE, valueLocal));
                            hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            hook.add(new VarInsnNode(Opcodes.FLOAD, valueLocal));
                            hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS, "transformEyeHeight",
                                "(" + ENTITY_DESC + "F)F", false));
                            method.instructions.insertBefore(instruction, hook);
                            changed = true;
                        }
                    }
                }
            }
        } else if (PLAYER_CONTROLLER.equals(transformedName)) {
            for (MethodNode method : node.methods) {
                if (("getBlockReachDistance".equals(method.name) || "func_78757_d".equals(method.name))
                    && "()F".equals(method.desc)) {
                    int valueLocal = method.maxLocals++;
                    for (AbstractInsnNode instruction = method.instructions.getFirst();
                         instruction != null; instruction = instruction.getNext()) {
                        if (instruction.getOpcode() == Opcodes.FRETURN) {
                            InsnList hook = new InsnList();
                            hook.add(new VarInsnNode(Opcodes.FSTORE, valueLocal));
                            hook.add(new VarInsnNode(Opcodes.FLOAD, valueLocal));
                            hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "virtuoel/pehkui/coremod/ClientScaleHooks", "transformBlockReach", "(F)F", false));
                            method.instructions.insertBefore(instruction, hook);
                            changed = true;
                        }
                    }
                }
            }
        } else if (LIVING_BASE.equals(transformedName)) {
            for (MethodNode method : node.methods) {
                if (("getJumpUpwardsMotion".equals(method.name) || "func_70664_aZ".equals(method.name))
                    && "()F".equals(method.desc)) {
                    int valueLocal = method.maxLocals++;
                    for (AbstractInsnNode instruction = method.instructions.getFirst();
                         instruction != null; instruction = instruction.getNext()) {
                        if (instruction.getOpcode() == Opcodes.FRETURN) {
                            InsnList hook = new InsnList();
                            hook.add(new VarInsnNode(Opcodes.FSTORE, valueLocal));
                            hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            hook.add(new VarInsnNode(Opcodes.FLOAD, valueLocal));
                            hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS, "transformJumpHeight",
                                "(" + ENTITY_DESC + "F)F", false));
                            method.instructions.insertBefore(instruction, hook);
                            changed = true;
                        }
                    }
                }
            }
        } else {
            for (MethodNode method : node.methods) {
                if (!"doRender".equals(method.name) || !method.desc.startsWith(
                    "(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V")) continue;

                for (AbstractInsnNode instruction = method.instructions.getFirst();
                     instruction != null; instruction = instruction.getNext()) {
                    if (instruction instanceof MethodInsnNode) {
                        MethodInsnNode call = (MethodInsnNode) instruction;
                        if ("prepareScale".equals(call.name) && call.desc.endsWith(")F")) {
                            int scaleArrayLocal = method.maxLocals;
                            int renderScaleLocal = scaleArrayLocal + 1;
                            InsnList hook = new InsnList();
                            hook.add(new VarInsnNode(Opcodes.FSTORE, renderScaleLocal));
                            hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
                            hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOKS, "getModelScale",
                                "(" + ENTITY_DESC + ")[F", false));
                            hook.add(new VarInsnNode(Opcodes.ASTORE, scaleArrayLocal));
                            hook.add(new VarInsnNode(Opcodes.ALOAD, scaleArrayLocal));
                            hook.add(new InsnNode(Opcodes.ICONST_0));
                            hook.add(new InsnNode(Opcodes.FALOAD));
                            hook.add(new VarInsnNode(Opcodes.ALOAD, scaleArrayLocal));
                            hook.add(new InsnNode(Opcodes.ICONST_1));
                            hook.add(new InsnNode(Opcodes.FALOAD));
                            hook.add(new InsnNode(Opcodes.FCONST_1));
                            hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                "net/minecraft/client/renderer/GlStateManager", "scale", "(FFF)V", false));
                            hook.add(new VarInsnNode(Opcodes.FLOAD, renderScaleLocal));
                            method.instructions.insert(instruction, hook);
                            method.maxLocals = renderScaleLocal + 1;
                            changed = true;
                            break;
                        }
                    }
                }
            }
        }

        if (!changed) return basicClass;
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }
}
