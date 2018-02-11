package com.jamieswhiteshirt.clothesline.common.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;

public class SetConnectorPosMessage implements IMessage {
    public int entityId;
    @Nullable
    public BlockPos fromPos;

    public SetConnectorPosMessage() { }

    public SetConnectorPosMessage(int entityId, @Nullable BlockPos fromPos) {
        this.entityId = entityId;
        this.fromPos = fromPos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        if (buf.readBoolean()) {
            fromPos = BlockPos.fromLong(buf.readLong());
        } else {
            fromPos = null;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        if (fromPos != null) {
            buf.writeBoolean(true);
            buf.writeLong(fromPos.toLong());
        } else {
            buf.writeBoolean(false);
        }
    }
}
