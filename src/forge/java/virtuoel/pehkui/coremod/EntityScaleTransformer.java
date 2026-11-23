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

/**
 * Adds scale-aware dimensions to Entity#setSize in both development and
 * production environments. The hook only changes the two dimensions passed
 * to vanilla; vanilla remains responsible for rebuilding the bounding box.
 */
public final class EntityScaleTransformer implements IClassTransformer {
    private static final String ENTITY = "net.minecraft.entity.Entity";
    private static final String HOOKS = "virtuoel/pehkui/coremod/EntityScaleHooks";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !ENTITY.equals(transformedName)) return basicClass;

        ClassNode node = new ClassNode();
        new ClassReader(basicClass).accept(node, 0);
        boolean changed = false;

        for (MethodNode method : node.methods) {
            if (("setSize".equals(method.name) || "func_70105_a".equals(method.name))
                && "(FF)V".equals(method.desc)) {
                int dimensionsLocal = method.maxLocals;
                InsnList hook = new InsnList();
                hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
                hook.add(new VarInsnNode(Opcodes.FLOAD, 1));
                hook.add(new VarInsnNode(Opcodes.FLOAD, 2));
                hook.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC, HOOKS, "transformSize",
                    "(Lnet/minecraft/entity/Entity;FF)[F", false
                ));
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
                break;
            }
        }

        if (!changed) return basicClass;
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }
}
