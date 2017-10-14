package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.SortedIntShiftMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class NetworkUtil {
    public static void writeNetworkToByteBuf(ByteBuf buf, Network network) {
        writeNetworkUuidToByteBuf(buf, network.getUuid());
        writeNetworkStateToByteBuf(buf, network.getState());
    }

    public static Network readNetworkFromByteBuf(ByteBuf buf) {
        UUID uuid = readNetworkUuidFromByteBuf(buf);
        return new Network(uuid, readNetworkStateFromByteBuf(buf));
    }

    public static void writeNetworkUuidToByteBuf(ByteBuf buf, UUID networkUuid) {
        buf.writeLong(networkUuid.getMostSignificantBits());
        buf.writeLong(networkUuid.getLeastSignificantBits());
    }

    public static UUID readNetworkUuidFromByteBuf(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public static void writeNetworkStateToByteBuf(ByteBuf buf, AbsoluteNetworkState state) {
        writeBasicTreeToByteBuf(buf, BasicTree.fromAbsolute(state.getTree()));
        buf.writeInt(state.getPreviousOffset());
        buf.writeInt(state.getOffset());
        buf.writeInt(state.getMomentum());
        buf.writeShort(state.getStacks().size());
        for (SortedIntShiftMap.Entry<ItemStack> entry : state.getStacks().entries()) {
            buf.writeInt(entry.getKey());
            writeItemStackToByteBuf(buf, entry.getValue());
        }
    }

    public static AbsoluteNetworkState readNetworkStateFromByteBuf(ByteBuf buf) {
        AbsoluteTree tree = readBasicTreeFromByteBuf(buf).toAbsolute();
        int previousOffset = buf.readInt();
        int offset = buf.readInt();
        int momentum = buf.readInt();
        int numItemStacks = buf.readUnsignedShort();
        ArrayList<SortedIntShiftMap.Entry<ItemStack>> entries = new ArrayList<>(numItemStacks);
        for (int i = 0; i < numItemStacks; i++) {
            entries.add(new SortedIntShiftMap.Entry<>(buf.readInt(), readItemStackFromByteBuf(buf)));
        }
        return new AbsoluteNetworkState(
                previousOffset,
                offset,
                momentum,
                tree,
                new SortedIntShiftMap<>(entries, tree.getLoopLength())
        );
    }

    public static void writeBasicTreeToByteBuf(ByteBuf buf, BasicTree tree) {
        buf.writeLong(tree.getPos().toLong());
        buf.writeByte(tree.getChildren().size());
        for (BasicTree child : tree.getChildren()) {
            writeBasicTreeToByteBuf(buf, child);
        }
    }

    public static BasicTree readBasicTreeFromByteBuf(ByteBuf buf) {
        BlockPos pos = BlockPos.fromLong(buf.readLong());
        int numChildren = buf.readUnsignedByte();
        BasicTree[] children = new BasicTree[numChildren];
        for (int i = 0; i < numChildren; i++) {
            children[i] = readBasicTreeFromByteBuf(buf);
        }
        return new BasicTree(pos, Arrays.asList(children));
    }
    public static void writeItemStackToByteBuf(ByteBuf buf, ItemStack stack) {
        ByteBufUtils.writeItemStack(buf, stack);
    }

    public static ItemStack readItemStackFromByteBuf(ByteBuf buf) {
        return ByteBufUtils.readItemStack(buf);
    }
}
