package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.common.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.internal.INetworkProvider;
import com.jamieswhiteshirt.clothesline.internal.PersistentNetwork;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

import java.util.UUID;

public final class ServerNetworkManager extends NetworkManager {
    private final WorldServer world;
    private final INetworkProvider provider;

    public ServerNetworkManager(WorldServer world, INetworkCollection networks, INetworkProvider provider) {
        super(world, networks);
        this.world = world;
        this.provider = provider;
    }

    private void dropAttachment(INetworkState state, ItemStack stack, int attachmentKey) {
        if (!stack.isEmpty()) {
            Vec3d pos = state.getPath().getPositionForOffset(state.attachmentKeyToOffset(attachmentKey));
            EntityItem entityitem = new EntityItem(world, pos.x, pos.y - 0.5D, pos.z, stack);
            entityitem.setDefaultPickupDelay();
            world.spawnEntity(entityitem);
        }
    }

    private void dropTreeItems(Tree tree) {
        BlockPos from = tree.getPos();
        for (Tree.Edge edge : tree.getEdges()) {
            BlockPos to = edge.getTree().getPos();
            EntityItem entityitem = new EntityItem(
                world,
                (1 + from.getX() + to.getX()) / 2.0D,
                (1 + from.getY() + to.getY()) / 2.0D,
                (1 + from.getZ() + to.getZ()) / 2.0D,
                new ItemStack(ClotheslineItems.CLOTHESLINE)
            );
            entityitem.setDefaultPickupDelay();
            world.spawnEntity(entityitem);
            dropTreeItems(edge.getTree());
        }
    }

    @Override
    protected void dropItems(INetworkState state, boolean dropClotheslines) {
        if (world.getGameRules().getBoolean("doTileDrops")) {
            for (MutableSortedIntMap.Entry<ItemStack> entry : state.getAttachments().entries()) {
                dropAttachment(state, entry.getValue(), entry.getKey());
            }
            if (dropClotheslines) {
                dropTreeItems(state.getTree());
            }
        }
    }

    @Override
    protected void createNetwork(INetworkState state) {
        provider.addNetwork(new PersistentNetwork(UUID.randomUUID(), state));
    }

    @Override
    protected void deleteNetwork(INetwork network) {
        provider.removeNetwork(network.getUuid());
    }
}
