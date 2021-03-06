package com.jamieswhiteshirt.clothesline.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.Arrays;
import java.util.List;

public class NBTSerialization {
    public static NBTTagList writePersistentNetworks(List<BasicPersistentNetwork> networks) {
        NBTTagList nbt = new NBTTagList();
        for (BasicPersistentNetwork network : networks) {
            nbt.appendTag(writePersistentNetwork(network));
        }
        return nbt;
    }

    public static List<BasicPersistentNetwork> readPersistentNetworks(NBTTagList nbt) {
        BasicPersistentNetwork[] networks = new BasicPersistentNetwork[nbt.tagCount()];
        for (int i = 0; i < nbt.tagCount(); i++) {
            networks[i] = readPersistentNetwork(nbt.getCompoundTagAt(i));
        }
        return Arrays.asList(networks);
    }

    public static NBTTagCompound writePersistentNetwork(BasicPersistentNetwork network) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setUniqueId("Uuid", network.getUuid());
        nbt.setTag("State", writeNetworkState(network.getState()));
        return nbt;
    }

    public static BasicPersistentNetwork readPersistentNetwork(NBTTagCompound compound) {
        return new BasicPersistentNetwork(
            compound.getUniqueId("Uuid"),
            readNetworkState(compound.getCompoundTag("State"))
        );
    }

    public static NBTTagCompound writeNetworkState(BasicNetworkState state) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Shift", state.getShift());
        nbt.setInteger("Momentum", state.getMomentum());
        nbt.setTag("Tree", writeBasicTree(state.getTree()));
        nbt.setTag("Attachments", writeAttachments(state.getAttachments()));
        return nbt;
    }

    public static BasicNetworkState readNetworkState(NBTTagCompound nbt) {
        return new BasicNetworkState(
            nbt.getInteger("Shift"),
            nbt.getInteger("Momentum"),
            readBasicTree(nbt.getCompoundTag("Tree")),
            readAttachments(nbt.getTagList("Attachments", Constants.NBT.TAG_COMPOUND))
        );
    }

    public static NBTTagCompound writeBasicTree(BasicTree tree) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("x", tree.getPos().getX());
        nbt.setInteger("y", tree.getPos().getY());
        nbt.setInteger("z", tree.getPos().getZ());
        nbt.setTag("Children", writeBasicTreeEdges(tree.getEdges()));
        nbt.setInteger("BaseRotation", tree.getBaseRotation());
        return nbt;
    }

    public static BasicTree readBasicTree(NBTTagCompound nbt) {
        return new BasicTree(
            new BlockPos(
                nbt.getInteger("x"),
                nbt.getInteger("y"),
                nbt.getInteger("z")
            ),
            readBasicTreeEdges(nbt.getTagList("Children", Constants.NBT.TAG_COMPOUND)),
            nbt.getInteger("BaseRotation")
        );
    }

    public static NBTTagList writeBasicTreeEdges(List<BasicTree.Edge> edges) {
        NBTTagList nbt = new NBTTagList();
        for (BasicTree.Edge edge : edges) {
            nbt.appendTag(writeBasicTreeEdge(edge));
        }
        return nbt;
    }

    public static List<BasicTree.Edge> readBasicTreeEdges(NBTTagList nbt) {
        BasicTree.Edge[] edges = new BasicTree.Edge[nbt.tagCount()];
        for (int i = 0; i < nbt.tagCount(); i++) {
            edges[i] = readBasicTreeEdge(nbt.getCompoundTagAt(i));
        }
        return Arrays.asList(edges);
    }

    public static NBTTagCompound writeBasicTreeEdge(BasicTree.Edge edge) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Length", edge.getLength());
        nbt.setTag("Tree", writeBasicTree(edge.getTree()));
        return nbt;
    }

    public static BasicTree.Edge readBasicTreeEdge(NBTTagCompound nbt) {
        return new BasicTree.Edge(
            nbt.getInteger("Length"),
            readBasicTree(nbt.getCompoundTag("Tree"))
        );
    }

    public static NBTTagList writeAttachments(List<BasicAttachment> attachments) {
        NBTTagList nbt = new NBTTagList();
        for (BasicAttachment attachment : attachments) {
            nbt.appendTag(writeAttachment(attachment));
        }
        return nbt;
    }

    public static List<BasicAttachment> readAttachments(NBTTagList nbt) {
        BasicAttachment[] attachments = new BasicAttachment[nbt.tagCount()];
        for (int i = 0; i < nbt.tagCount(); i++) {
            attachments[i] = readAttachment(nbt.getCompoundTagAt(i));
        }
        return Arrays.asList(attachments);
    }

    public static NBTTagCompound writeAttachment(BasicAttachment attachment) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Offset", attachment.getKey());
        nbt.setTag("Stack", attachment.getStack().serializeNBT());
        return nbt;
    }

    public static BasicAttachment readAttachment(NBTTagCompound nbt) {
        return new BasicAttachment(
                nbt.getInteger("Offset"),
                new ItemStack(nbt.getCompoundTag("Stack"))
        );
    }
}
