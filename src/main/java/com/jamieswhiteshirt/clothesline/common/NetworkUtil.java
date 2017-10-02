package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.api.Attachment;
import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.Node;
import com.jamieswhiteshirt.clothesline.api.NodeLoop;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NetworkUtil {
    public static void writeNetworkToByteBuf(ByteBuf buf, Network network) {
        writeNetworkUuidToByteBuf(buf, network.getUuid());
        writeNodeLoopToByteBuf(buf, network.getNodeLoop());
        buf.writeShort(network.getAttachments().size());
        for (Attachment attachment : network.getAttachments().values()) {
            writeAttachmentToByteBuf(buf, attachment);
        }
    }

    public static Network readNetworkFromByteBuf(ByteBuf buf) {
        UUID uuid = readNetworkUuidFromByteBuf(buf);
        NodeLoop nodeLoop = readNodeLoopFromByteBuf(buf);
        int numAttachments = buf.readUnsignedShort();
        Attachment[] attachments = new Attachment[numAttachments];
        for (int i = 0; i < numAttachments; i++) {
            attachments[i] = readAttachmentFromByteBuf(buf);
        }
        return new Network(uuid, nodeLoop, Arrays.stream(attachments).collect(Collectors.toMap(
                Attachment::getId,
                attachment -> attachment
        )));
    }

    public static void writeNetworkUuidToByteBuf(ByteBuf buf, UUID networkUuid) {
        buf.writeLong(networkUuid.getMostSignificantBits());
        buf.writeLong(networkUuid.getLeastSignificantBits());
    }

    public static UUID readNetworkUuidFromByteBuf(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeNodeLoopToByteBuf(ByteBuf buf, NodeLoop nodeLoop) {
        List<Node> nodes = nodeLoop.getNodes();
        buf.writeShort(nodes.size());
        for (Node node : nodes) {
            writeNodeToByteBuf(buf, node);
        }
        buf.writeInt(nodeLoop.getLoopLength());
    }

    public static NodeLoop readNodeLoopFromByteBuf(ByteBuf buf) {
        int numNodes = buf.readShort();
        Node[] nodes = new Node[numNodes];
        for (int i = 0; i < numNodes; i++) {
            nodes[i] = readNodeFromByteBuf(buf);
        }
        return new NodeLoop(Arrays.asList(nodes), buf.readInt());
    }

    public static void writeNodeToByteBuf(ByteBuf buf, Node node) {
        buf.writeLong(node.getPos().toLong());
        buf.writeShort(node.getOffset());
        buf.writeFloat(node.getAngleY());
    }

    public static Node readNodeFromByteBuf(ByteBuf buf) {
        return new Node(BlockPos.fromLong(buf.readLong()), buf.readUnsignedShort(), buf.readFloat());
    }

    public static void writeAttachmentToByteBuf(ByteBuf buf, Attachment attachment) {
        writeAttachmentIdToByteBuf(buf, attachment.getId());
        buf.writeInt(attachment.getOffset());
        ByteBufUtils.writeItemStack(buf, attachment.getStack());
    }

    public static Attachment readAttachmentFromByteBuf(ByteBuf buf) {
        return new Attachment(readAttachmentIdFromByteBuf(buf), buf.readInt(), ByteBufUtils.readItemStack(buf));
    }

    public static void writeAttachmentIdToByteBuf(ByteBuf buf, int attachmentId) {
        buf.writeInt(attachmentId);
    }

    public static int readAttachmentIdFromByteBuf(ByteBuf buf) {
        return buf.readInt();
    }
}
