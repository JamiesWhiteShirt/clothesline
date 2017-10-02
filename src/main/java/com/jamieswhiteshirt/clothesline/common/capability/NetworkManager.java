package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.tileentity.TileEntityClotheslineAnchor;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class NetworkManager implements INetworkManager {
    private final World world;
    private HashMap<UUID, Network> networksByUuid = new HashMap<>();
    private HashMap<BlockPos, Network> networksByBlockPos = new HashMap<>();

    public NetworkManager(World world) {
        this.world = world;
    }

    @Override
    public Map<UUID, Network> getNetworks() {
        return networksByUuid;
    }

    @Nullable
    @Override
    public Network getNetworkByUUID(UUID uuid) {
        return networksByUuid.get(uuid);
    }

    @Nullable
    @Override
    public Network getNetworkByBlockPos(BlockPos pos) {
        return networksByBlockPos.get(pos);
    }

    @Override
    public void addNetwork(Network network) {
        networksByUuid.put(network.getUuid(), network);
        assignNodeLoop(network, network.getNodeLoop());
    }

    private void assignNodeLoop(Network network, NodeLoop nodeLoop) {
        for (BlockPos pos : nodeLoop.getPositions()) {
            networksByBlockPos.put(pos, network);
        }
    }

    @Override
    public void removeNetwork(UUID networkUuid) {
        Network network = networksByUuid.remove(networkUuid);
        if (network != null) {
            unassignNodeLoop(network, network.getNodeLoop());
        }
    }

    private void unassignNodeLoop(Network network, NodeLoop nodeLoop) {
        for (BlockPos pos : nodeLoop.getPositions()) {
            networksByBlockPos.remove(pos, network);
        }
    }

    @Override
    public void setNetworks(Map<UUID, Network> networks) {
        this.networksByUuid = new HashMap<>(networks);
    }

    @Override
    public void update() {
        networksByUuid.values().forEach(Network::update);
    }

    private void extend(Network network, BlockPos fromPos, BlockPos toPos) {
        NodeLoop extensionLoop = NodeLoop.buildInitial(fromPos, toPos);
        network.setNodeLoop(network.getNodeLoop().mergeWith(extensionLoop));
        assignNodeLoop(network, extensionLoop);
    }

    @Override
    public boolean connect(BlockPos posA, BlockPos posB) {
        if (posA.equals(posB)) {
            return false;
        }

        TileEntity tileEntityA = world.getTileEntity(posA);
        TileEntity tileEntityB = world.getTileEntity(posB);
        if (tileEntityA instanceof TileEntityClotheslineAnchor && tileEntityB instanceof TileEntityClotheslineAnchor) {
            TileEntityClotheslineAnchor anchorA = (TileEntityClotheslineAnchor) tileEntityA;
            TileEntityClotheslineAnchor anchorB = (TileEntityClotheslineAnchor) tileEntityB;


            Network networkA = getNetworkByBlockPos(posA);
            Network networkB = getNetworkByBlockPos(posB);

            Network network = null;
            if (networkA != null) {
                if (networkB != null) {
                    if (networkA == networkB) {
                        //TODO: Look into circular networks
                        return false;
                    }

                    removeNetwork(networkA.getUuid());
                    removeNetwork(networkB.getUuid());

                    NodeLoop nodeLoop =
                } else {
                    extend(networkA, posA, posB);
                    network = networkA;
                }
            } else {
                if (networkB != null) {
                    extend(networkB, posB, posA);
                    network = networkB;
                } else {
                    network = Network.buildInitial(UUID.randomUUID(), posA, posB, Collections.emptyMap());
                    network.addAttachment(new Attachment(0, 0, new ItemStack(Items.LEATHER_CHESTPLATE)));

                    addNetwork(network);
                }
            }

            anchorA.setNetwork(network);
            anchorB.setNetwork(network);

            return true;
        }
        return false;
    }
}
