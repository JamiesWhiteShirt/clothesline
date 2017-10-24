package com.jamieswhiteshirt.clothesline.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.world.World")) {
            return transformWorld(basicClass);
        }
        return basicClass;
    }

    private boolean equalsEither(String name, String srgName, String mcpName) {
        return name.equals(srgName) || name.equals(mcpName);
    }

    private byte[] transformWorld(byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (equalsEither(methodNode.name, "func_190527_a", "mayPlace") && methodNode.desc.equals("(Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/util/EnumFacing;Lnet/minecraft/entity/Entity;)Z")) {
                transformMayPlace(methodNode);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private void transformMayPlace(MethodNode methodNode) {
        InsnList preInstructions = new InsnList();
        preInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        preInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
        preInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        preInstructions.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "com/jamieswhiteshirt/clothesline/core/Hooks",
                "onMayPlace",
                "(Lnet/minecraft/world/World;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)Z",
                false
        ));
        LabelNode labelNode = new LabelNode();
        preInstructions.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
        preInstructions.add(new InsnNode(Opcodes.ICONST_0));
        preInstructions.add(new InsnNode(Opcodes.IRETURN));
        preInstructions.add(labelNode);

        methodNode.instructions.insert(preInstructions);
    }
}
