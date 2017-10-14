package com.jamieswhiteshirt.clothesline.common.network.message;

import com.jamieswhiteshirt.clothesline.common.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.UUID;

public class MessageSetItem implements IMessage {
    public UUID networkUuid;
    public int offset;
    public ItemStack stack;

    public MessageSetItem() {

    }

    public MessageSetItem(UUID networkUuid, ItemStack stack) {
        this.networkUuid = networkUuid;
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.networkUuid = NetworkUtil.readNetworkUuidFromByteBuf(buf);
        this.offset = buf.readInt();
        this.stack = NetworkUtil.readItemStackFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkUtil.writeNetworkUuidToByteBuf(buf, networkUuid);
        buf.writeInt(offset);
        NetworkUtil.writeItemStackToByteBuf(buf, stack);
    }
}
