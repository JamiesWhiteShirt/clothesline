package com.jamieswhiteshirt.clothesline.common.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.Arrays;
import java.util.UUID;

public class ByteBufSerialization {
    public static void writeNetwork(ByteBuf buf, BasicNetwork network) {
        writeNetworkUuid(buf, network.getUuid());
        writeNetworkId(buf, network.getId());
        writeNetworkState(buf, network.getState());
    }

    public static BasicNetwork readNetwork(ByteBuf buf) {
        return new BasicNetwork(readNetworkUuid(buf), readNetworkId(buf), readNetworkState(buf));
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
        writeTree(buf, state.getTree());
        buf.writeInt(state.getShift());
        buf.writeInt(state.getMomentum());
        buf.writeShort(state.getAttachments().size());
        for (BasicAttachment attachment : state.getAttachments()) {
            buf.writeInt(attachment.getKey());
            writeItemStack(buf, attachment.getStack());
        }
    }

    public static BasicNetworkState readNetworkState(ByteBuf buf) {
        BasicTree tree = readTree(buf);
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

    public static void writeTree(ByteBuf buf, BasicTree tree) {
        buf.writeLong(tree.getPos().toLong());
        buf.writeByte(tree.getChildren().size());
        for (BasicTree child : tree.getChildren()) {
            writeTree(buf, child);
        }
    }

    public static BasicTree readTree(ByteBuf buf) {
        BlockPos pos = BlockPos.fromLong(buf.readLong());
        int numChildren = buf.readUnsignedByte();
        BasicTree[] children = new BasicTree[numChildren];
        for (int i = 0; i < numChildren; i++) {
            children[i] = readTree(buf);
        }
        return new BasicTree(pos, Arrays.asList(children));
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
