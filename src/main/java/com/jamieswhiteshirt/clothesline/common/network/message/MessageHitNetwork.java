package com.jamieswhiteshirt.clothesline.common.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageHitNetwork implements IMessage {
    public BlockPos posA;
    public BlockPos posB;

    public MessageHitNetwork() { }

    public MessageHitNetwork(BlockPos posA, BlockPos posB) {
        this.posA = posA;
        this.posB = posB;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        posA = BlockPos.fromLong(buf.readLong());
        posB = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(posA.toLong());
        buf.writeLong(posB.toLong());
    }
}
