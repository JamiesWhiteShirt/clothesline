package com.jamieswhiteshirt.clothesline.common.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.Arrays;
import java.util.UUID;

public class ByteBufSerialization {
    public static void writeNetwork(ByteBuf buf, BasicNetwork network) {
        writeNetworkId(buf, network.getId());
        writePersistentNetwork(buf, network.getPersistentNetwork());
    }

    public static BasicNetwork readNetwork(ByteBuf buf) {
        return new BasicNetwork(readNetworkId(buf), readPersistentNetwork(buf));
    }

    public static void writePersistentNetwork(ByteBuf buf, BasicPersistentNetwork network) {
        writeNetworkUuid(buf, network.getUuid());
        writeNetworkState(buf, network.getState());
    }

    public static BasicPersistentNetwork readPersistentNetwork(ByteBuf buf) {
        return new BasicPersistentNetwork(readNetworkUuid(buf), readNetworkState(buf));
    }

    public static void writeNetworkUuid(ByteBuf buf, UUID networkUuid) {
        buf.writeLong(networkUuid.getMostSignificantBits());
        buf.writeLong(networkUuid.getLeastSignificantBits());
    }

    public static UUID readNetworkUuid(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeNetworkId(ByteBuf buf, int networkId) {
        ByteBufUtils.writeVarInt(buf, networkId, 4);
    }

    public static int readNetworkId(ByteBuf buf) {
        return ByteBufUtils.readVarInt(buf, 4);
    }

    public static void writeNetworkState(ByteBuf buf, BasicNetworkState state) {
        writeBasicTree(buf, state.getTree());
        buf.writeInt(state.getShift());
        buf.writeInt(state.getMomentum());
        buf.writeShort(state.getAttachments().size());
        for (BasicAttachment attachment : state.getAttachments()) {
            buf.writeInt(attachment.getKey());
            writeItemStack(buf, attachment.getStack());
        }
    }

    public static BasicNetworkState readNetworkState(ByteBuf buf) {
        BasicTree tree = readBasicTree(buf);
        int offset = buf.readInt();
        int momentum = buf.readInt();
        int numAttachments = buf.readUnsignedShort();
        BasicAttachment[] attachments = new BasicAttachment[numAttachments];
        for (int i = 0; i < numAttachments; i++) {
            attachments[i] = new BasicAttachment(buf.readInt(), readItemStack(buf));
        }
        return new BasicNetworkState(
            offset,
            momentum,
            tree,
            Arrays.asList(attachments)
        );
    }

    public static void writeBasicTree(ByteBuf buf, BasicTree tree) {
        buf.writeLong(tree.getPos().toLong());
        buf.writeByte(tree.getEdges().size());
        for (BasicTree.Edge edge : tree.getEdges()) {
            buf.writeShort(edge.getLength());
            writeBasicTree(buf, edge.getTree());
        }
        buf.writeInt(tree.getBaseRotation());
    }

    public static BasicTree readBasicTree(ByteBuf buf) {
        BlockPos pos = BlockPos.fromLong(buf.readLong());
        int numChildren = buf.readUnsignedByte();
        BasicTree.Edge[] edges = new BasicTree.Edge[numChildren];
        for (int i = 0; i < numChildren; i++) {
            edges[i] = new BasicTree.Edge(
                buf.readUnsignedShort(),
                readBasicTree(buf)
            );
        }
        return new BasicTree(pos, Arrays.asList(edges), buf.readInt());
    }

    public static void writeAttachment(ByteBuf buf, BasicAttachment attachment) {
        buf.writeInt(attachment.getKey());
        writeItemStack(buf, attachment.getStack());
    }

    public static BasicAttachment readAttachment(ByteBuf buf) {
        return new BasicAttachment(
            buf.readInt(),
            readItemStack(buf)
        );
    }

    public static void writeItemStack(ByteBuf buf, ItemStack stack) {
        ByteBufUtils.writeItemStack(buf, stack);
    }

    public static ItemStack readItemStack(ByteBuf buf) {
        return ByteBufUtils.readItemStack(buf);
    }
}
