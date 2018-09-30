package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.internal.INetworkProvider;
import com.jamieswhiteshirt.clothesline.internal.PersistentNetwork;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
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
        if (!stack.isEmpty() && world.getGameRules().getBoolean("doTileDrops")) {
            Vec3d pos = state.getPath().getPositionForOffset(state.attachmentKeyToOffset(attachmentKey));
            EntityItem entityitem = new EntityItem(world, pos.x, pos.y - 0.5D, pos.z, stack);
            entityitem.setDefaultPickupDelay();
            world.spawnEntity(entityitem);
        }
    }

    @Override
    protected void dropItems(INetworkState state) {
        for (MutableSortedIntMap.Entry<ItemStack> entry : state.getAttachments().entries()) {
            dropAttachment(state, entry.getValue(), entry.getKey());
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
