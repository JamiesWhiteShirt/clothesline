package com.jamieswhiteshirt.clothesline.common.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageHitEdge implements IMessage {
    public BlockPos fromPos;
    public BlockPos toPos;

    public MessageHitEdge() { }

    public MessageHitEdge(BlockPos from, BlockPos to) {
        this.fromPos = from;
        this.toPos = to;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        fromPos = BlockPos.fromLong(buf.readLong());
        toPos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(fromPos.toLong());
        buf.writeLong(toPos.toLong());
    }
}
