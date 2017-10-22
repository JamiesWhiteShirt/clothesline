package com.jamieswhiteshirt.clothesline.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.Arrays;
import java.util.List;

public class NBTSerialization {
    public static NBTTagList writeNetworks(List<BasicNetwork> networks) {
        NBTTagList nbt = new NBTTagList();
        for (BasicNetwork network : networks) {
            nbt.appendTag(writeNetwork(network));
        }
        return nbt;
    }

    public static List<BasicNetwork> readNetworks(NBTTagList nbt) {
        BasicNetwork[] networks = new BasicNetwork[nbt.tagCount()];
        for (int i = 0; i < nbt.tagCount(); i++) {
            networks[i] = readNetwork(nbt.getCompoundTagAt(i));
        }
        return Arrays.asList(networks);
    }

    public static NBTTagCompound writeNetwork(BasicNetwork network) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setUniqueId("Uuid", network.getUuid());
        nbt.setTag("State", writeNetworkState(network.getState()));
        return nbt;
    }

    public static BasicNetwork readNetwork(NBTTagCompound compound) {
        return new BasicNetwork(
                compound.getUniqueId("Uuid"),
                readNetworkState(compound.getCompoundTag("State"))
        );
    }

    public static NBTTagCompound writeNetworkState(BasicNetworkState state) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Offset", state.getOffset());
        nbt.setInteger("Momentum", state.getMomentum());
        nbt.setTag("Tree", writeTree(state.getTree()));
        nbt.setTag("Attachments", writeAttachments(state.getAttachments()));
        return nbt;
    }

    public static BasicNetworkState readNetworkState(NBTTagCompound nbt) {
        return new BasicNetworkState(
                nbt.getInteger("Offset"),
                nbt.getInteger("Momentum"),
                readTree(nbt.getCompoundTag("Tree")),
                readAttachments(nbt.getTagList("Attachments", Constants.NBT.TAG_COMPOUND))
        );
    }

    public static NBTTagCompound writeTree(BasicTree tree) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("x", tree.getPos().getX());
        nbt.setInteger("y", tree.getPos().getY());
        nbt.setInteger("z", tree.getPos().getZ());
        nbt.setTag("Children", writeTrees(tree.getChildren()));
        return nbt;
    }

    public static BasicTree readTree(NBTTagCompound nbt) {
        return new BasicTree(
                new BlockPos(
                        nbt.getInteger("x"),
                        nbt.getInteger("y"),
                        nbt.getInteger("z")
                ),
                readTrees(nbt.getTagList("Children", Constants.NBT.TAG_COMPOUND))
        );
    }

    public static NBTTagList writeTrees(List<BasicTree> trees) {
        NBTTagList nbt = new NBTTagList();
        for (BasicTree tree : trees) {
            nbt.appendTag(writeTree(tree));
        }
        return nbt;
    }

    public static List<BasicTree> readTrees(NBTTagList nbt) {
        BasicTree[] trees = new BasicTree[nbt.tagCount()];
        for (int i = 0; i < nbt.tagCount(); i++) {
            trees[i] = readTree(nbt.getCompoundTagAt(i));
        }
        return Arrays.asList(trees);
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
        nbt.setInteger("Offset", attachment.getOffset());
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
