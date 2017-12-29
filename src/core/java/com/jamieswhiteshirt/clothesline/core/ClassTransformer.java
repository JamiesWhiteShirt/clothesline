package com.jamieswhiteshirt.clothesline.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.function.Consumer;

public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
            case "net.minecraft.world.World":
                /*
                 * This mod disallows placement of blocks that intersect with clotheslines.
                 * This hook is needed to be able to properly prevent placement of blocks that intersect with clotheslines.
                 * BlockEvent.PlaceEvent only runs on the server, and therefore results in an uncanny interaction where
                 * block is visible until the server responds.
                 * As a bonus, this ensures that falling blocks will never land in a spot intersecting with a clothesline.
                 * Adding unreplaceable blocks intersecting with the clotheslines would be extremely fragile.
                 */
                return transformSingleMethod(
                        basicClass,
                        "func_190527_a",
                        "mayPlace",
                        "(Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/util/EnumFacing;Lnet/minecraft/entity/Entity;)Z",
                        methodNode -> {
                            InsnList preInstructions = new InsnList();
                            preInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            preInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                            preInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                            preInstructions.add(new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    "com/jamieswhiteshirt/clothesline/core/CommonHooks",
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
                );
            case "net.minecraft.client.renderer.EntityRenderer":
                /*
                 * Traditionally, the object the mouse is over may only be a block, an entity, or nothing. This mod allows
                 * it to be a clothesline or an attachment as well.
                 * This hook is needed to reliably insert the potential clothesline or attachment the mouse may be over.
                 */
                return transformSingleMethod(
                        basicClass,
                        "func_78473_a",
                        "getMouseOver",
                        "(F)V",
                        methodNode -> {
                            for (int i = 0; i < methodNode.instructions.size(); i++) {
                                AbstractInsnNode insnNode = methodNode.instructions.get(i);
                                if (insnNode.getOpcode() == Opcodes.RETURN) {
                                    InsnList insnList = new InsnList();
                                    insnList.add(new VarInsnNode(Opcodes.FLOAD, 1));
                                    insnList.add(new MethodInsnNode(
                                            Opcodes.INVOKESTATIC,
                                            "com/jamieswhiteshirt/clothesline/core/ClientHooks",
                                            "onGetMouseOver",
                                            "(F)V",
                                            false
                                    ));
                                    i += insnList.size();
                                    methodNode.instructions.insertBefore(insnNode, insnList);
                                }
                            }
                        }
                );
            case "net.minecraft.client.renderer.RenderGlobal":
                /*
                 * Clotheslines are neither Entities or TileEntities, but they are very much like them. Clotheslines
                 * should therefore be rendered around the time Entities and TileEntities are rendered. This should increase
                 * compatibility and also makes outline rendering possible.
                 * This hook allows rendering things right after tile entities are rendered.
                 */
                return transformSingleMethod(
                        basicClass,
                        "func_180446_a",
                        "renderEntities",
                        "(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V",
                        methodNode -> {
                            for (int i = 0; i < methodNode.instructions.size(); i++) {
                                AbstractInsnNode insnNode = methodNode.instructions.get(i);
                                if (insnNode instanceof MethodInsnNode) {
                                    MethodInsnNode mInsnNode = (MethodInsnNode) insnNode;
                                    if (
                                            mInsnNode.getOpcode() == Opcodes.INVOKESPECIAL &&
                                            mInsnNode.owner.equals("net/minecraft/client/renderer/RenderGlobal") &&
                                            equalsEither(mInsnNode.name, "func_180443_s", "preRenderDamagedBlocks") &&
                                            mInsnNode.desc.equals("()V")
                                    ) {
                                        InsnList insnList = new InsnList();
                                        insnList.add(new VarInsnNode(Opcodes.FLOAD, 3));
                                        insnList.add(new MethodInsnNode(
                                                Opcodes.INVOKESTATIC,
                                                "com/jamieswhiteshirt/clothesline/core/ClientHooks",
                                                "onRenderEntities",
                                                "(F)V",
                                                false
                                        ));
                                        i += insnList.size();
                                        methodNode.instructions.insertBefore(insnNode, insnList);
                                    }
                                }
                            }
                        }
                );
            case "net.minecraft.client.multiplayer.PlayerControllerMP":
                /*
                 * When the client stops using an item, it will only tell the server that it stopped using the item, but not
                 * what block the item is being used on at the moment.
                 * The server could raytrace it like raytracing is mirrored for buckets, but this can result in
                 * desynchronizations. In addition, the client's raytrace sees more objects such as entities, clotheslines
                 * and attachments which should not be done on the server.
                 * This hook allows intercepting the interaction to allow the client to provide the position of this block.
                 * This mirrors how the client tells the server which block it is interacting in onItemUse.
                 */
                return transformSingleMethod(
                        basicClass,
                        "func_78766_c",
                        "onStoppedUsingItem",
                        "(Lnet/minecraft/entity/player/EntityPlayer;)V",
                        methodNode -> {
                            for (int i = 0; i < methodNode.instructions.size(); i++) {
                                AbstractInsnNode insnNode = methodNode.instructions.get(i);
                                if (insnNode instanceof MethodInsnNode) {
                                    MethodInsnNode mInsnNode = (MethodInsnNode) insnNode;
                                    if (
                                            mInsnNode.getOpcode() == Opcodes.INVOKESPECIAL &&
                                            mInsnNode.owner.equals("net/minecraft/client/multiplayer/PlayerControllerMP") &&
                                            equalsEither(mInsnNode.name, "func_78750_j", "syncCurrentPlayItem") &&
                                            mInsnNode.desc.equals("()V")
                                    ) {
                                        InsnList insnList = new InsnList();
                                        insnList.add(new MethodInsnNode(
                                                Opcodes.INVOKESTATIC,
                                                "com/jamieswhiteshirt/clothesline/core/ClientHooks",
                                                "onStoppedUsingItem",
                                                "()Z",
                                                false
                                        ));
                                        LabelNode continueLabel = new LabelNode();
                                        insnList.add(new JumpInsnNode(Opcodes.IFEQ, continueLabel));
                                        insnList.add(new InsnNode(Opcodes.RETURN));
                                        insnList.add(continueLabel);
                                        i += insnList.size();
                                        methodNode.instructions.insert(insnNode, insnList);
                                    }
                                }
                            }
                        }
                );
        }
        return basicClass;
    }

    private boolean equalsEither(String name, String srgName, String mcpName) {
        return name.equals(srgName) || name.equals(mcpName);
    }

    private byte[] transformClass(byte[] basicClass, Consumer<ClassNode> transformer) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);

        transformer.accept(classNode);

        ClassWriter writer = new SafeClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private byte[] transformSingleMethod(byte[] basicClass, String srgName, String mcpName, String desc, Consumer<MethodNode> transformer) {
        return transformClass(basicClass, classNode -> {
            for (MethodNode methodNode : classNode.methods) {
                if (equalsEither(methodNode.name, srgName, mcpName) && methodNode.desc.equals(desc)) {
                    transformer.accept(methodNode);
                }
            }
        });
    }
}
