package com.jamieswhiteshirt.clothesline.common.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SetAnchorHasCrankMessage implements IMessage {
    public BlockPos pos;
    public boolean hasCrank;

    public SetAnchorHasCrankMessage() {

    }

    public SetAnchorHasCrankMessage(BlockPos pos, boolean hasCrank) {
        this.pos = pos;
        this.hasCrank = hasCrank;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        hasCrank = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeBoolean(hasCrank);
    }
}
