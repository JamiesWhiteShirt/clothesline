package com.jamieswhiteshirt.clothesline.common.capability;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.BasicTree;
import com.jamieswhiteshirt.clothesline.common.RelativeNetworkState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
    public Collection<Network> getNetworks() {
        return networksByUuid.values();
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
        assignNetworkTree(network, network.getState().getTree());
    }

    private void assignNetworkTree(Network network, AbsoluteTree tree) {
        networksByBlockPos.put(tree.getPos(), network);
        for (AbsoluteTree child : tree.getChildren()) {
            assignNetworkTree(network, child);
        }
    }

    @Override
    public void removeNetwork(UUID networkUuid) {
        Network network = networksByUuid.remove(networkUuid);
        if (network != null) {
            unassignTree(network, network.getState().getTree());
        }
    }

    private void unassignTree(Network network, AbsoluteTree tree) {
        networksByBlockPos.remove(tree.getPos(), network);
        for (AbsoluteTree child : tree.getChildren()) {
            unassignTree(network, child);
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
        RelativeNetworkState relativeState = RelativeNetworkState.fromAbsolute(network.getState());
        relativeState.addEdge(fromPos, toPos);
        network.setState(relativeState.toAbsolute());
        assignNetworkTree(network, network.getState().getTree());
    }

    @Override
    public boolean connect(BlockPos posA, BlockPos posB) {
        Network networkA = getNetworkByBlockPos(posA);
        Network networkB = getNetworkByBlockPos(posB);

        if (posA.equals(posB)) {
            if (networkA != null) {
                RelativeNetworkState relativeState = RelativeNetworkState.fromAbsolute(networkA.getState());
                relativeState.reroot(posB);
                networkA.setState(relativeState.toAbsolute());
                return true;
            }
            return false;
        }

        if (networkA != null) {
            if (networkB != null) {
                if (networkA == networkB) {
                    //TODO: Look into circular networks
                    return false;
                }

                removeNetwork(networkA.getUuid());
                removeNetwork(networkB.getUuid());

                RelativeNetworkState stateA = RelativeNetworkState.fromAbsolute(networkA.getState());
                RelativeNetworkState stateB = RelativeNetworkState.fromAbsolute(networkB.getState());
                stateB.reroot(posB);
                stateA.addSubState(posA, stateB);
                Network network = new Network(UUID.randomUUID(), stateA.toAbsolute());

                addNetwork(network);
            } else {
                extend(networkA, posA, posB);
            }
        } else {
            if (networkB != null) {
                extend(networkB, posB, posA);
            } else {
                AbsoluteNetworkState state = AbsoluteNetworkState.createInitial(BasicTree.createInitial(posA, posB).toAbsolute());
                Network network = new Network(UUID.randomUUID(), state);
                network.getState().insertItem(0, new ItemStack(Items.LEATHER_CHESTPLATE), false);

                addNetwork(network);
            }
        }

        return true;
    }

    @Override
    public void destroy(BlockPos pos) {
        Network network = getNetworkByBlockPos(pos);
        if (network != null) {
            removeNetwork(network.getUuid());
            RelativeNetworkState state = RelativeNetworkState.fromAbsolute(network.getState());
            state.reroot(pos);
            RelativeNetworkState.SplitResult splitResult = state.splitRoot();
            for (RelativeNetworkState subState : splitResult.getSubStates()) {
                Network newNetwork = new Network(UUID.randomUUID(), subState.toAbsolute());
                addNetwork(newNetwork);
            }
        }
    }

    @Override
    public ItemStack insertItem(Network network, int offset, ItemStack stack, boolean simulate) {
        return network.getState().insertItem(offset, stack, simulate);
    }

    @Override
    public ItemStack extractItem(Network network, int offset, boolean simulate) {
        return network.getState().extractItem(offset, simulate);
    }

    @Override
    public void setItem(Network network, int offset, ItemStack stack) {
        network.getState().setItem(offset, stack);
    }

    @Override
    public void removeItem(Network network, int offset) {
        network.getState().removeItem(offset);
    }

    @Override
    public void addMomentum(Network network, int momentum) {
        network.getState().addMomentum(momentum);
    }
}
