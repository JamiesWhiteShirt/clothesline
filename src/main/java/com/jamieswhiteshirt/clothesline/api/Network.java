package com.jamieswhiteshirt.clothesline.api;

import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

public final class Network {
    public static Network buildInitial(UUID uuid, BlockPos a, BlockPos b, Map<Integer, Attachment> attachments) {
        return new Network(uuid, NodeLoop.buildInitial(a, b), attachments);
    }

    private final UUID uuid;
    private NodeLoop nodeLoop;
    private RangeLookup offsetLookup;

    private HashMap<Integer, Attachment> attachments;

    private int previousOffset;
    private int offset;
    private int momentum;

    public Network(UUID uuid, NodeLoop nodeLoop, Map<Integer, Attachment> attachments) {
        this.uuid = uuid;
        reset(nodeLoop, attachments);
    }

    private void reset(NodeLoop nodeLoop, Map<Integer, Attachment> attachments) {
        setNodeLoop(nodeLoop);
        this.attachments = new HashMap<>(attachments);
    }

    public UUID getUuid() {
        return uuid;
    }

    public NodeLoop getNodeLoop() {
        return nodeLoop;
    }

    public void setNodeLoop(NodeLoop nodeLoop) {
        this.nodeLoop = nodeLoop;
        this.offsetLookup = RangeLookup.build(0, nodeLoop.getNodes().stream().map(Node::getOffset).collect(Collectors.toList()));
    }

    public Map<Integer, Attachment> getAttachments() {
        return attachments;
    }

    public void addAttachment(Attachment attachment) {
        this.attachments.put(attachment.getId(), attachment);
    }

    public void removeAttachment(int attachmentId) {
        this.attachments.remove(attachmentId);
    }

    public void update() {
        if (momentum > 0) {
            momentum -= 1;
        } else if (momentum < 0) {
            momentum += 1;
        }

        previousOffset = offset;
        offset += momentum;
    }

    public void addMomentum(int momentum) {
        this.momentum = Math.min(this.momentum + momentum, 25);
    }

    public int getPreviousOffset() {
        return previousOffset;
    }

    public int getOffset() {
        return offset;
    }

    public int getMinNodeIndexForOffset(int offset) {
        return offsetLookup.getMinIndex(offset);
    }
}
