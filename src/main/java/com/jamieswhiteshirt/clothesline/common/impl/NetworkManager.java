package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.common.util.BasicTree;
import com.jamieswhiteshirt.clothesline.common.util.RelativeNetworkState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public final class NetworkManager implements INetworkManager {
    private final World world;
    private final List<INetworkManagerEventListener> eventListeners = new ArrayList<>();
    private HashMap<UUID, Network> networksByUuid = new HashMap<>();
    private HashMap<BlockPos, Network> networksByBlockPos = new HashMap<>();

    public NetworkManager(World world) {
        this.world = world;
    }

    @Override
    public final Collection<Network> getNetworks() {
        return networksByUuid.values();
    }

    @Nullable
    @Override
    public final Network getNetworkByUUID(UUID uuid) {
        return networksByUuid.get(uuid);
    }

    @Nullable
    @Override
    public final Network getNetworkByBlockPos(BlockPos pos) {
        return networksByBlockPos.get(pos);
    }

    @Override
    public void addNetwork(Network network) {
        if (network != networksByUuid.put(network.getUuid(), network)) {
            assignNetworkTree(network, network.getState().getTree());

            for (INetworkManagerEventListener eventListener : eventListeners) {
                eventListener.onNetworkAdded(network);
            }
        }
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

            for (INetworkManagerEventListener eventListener : eventListeners) {
                eventListener.onNetworkRemoved(network);
            }
        }
    }

    private void unassignTree(Network network, AbsoluteTree tree) {
        networksByBlockPos.remove(tree.getPos(), network);
        for (AbsoluteTree child : tree.getChildren()) {
            unassignTree(network, child);
        }
    }

    @Override
    public void setNetworks(Collection<Network> networks) {
        this.networksByUuid = new HashMap<>(networks.stream().collect(Collectors.toMap(
                Network::getUuid,
                network -> network
        )));
        this.networksByBlockPos = new HashMap<>();
        for (Network network : networks) {
            assignNetworkTree(network, network.getState().getTree());
        }

        for (INetworkManagerEventListener eventListener : eventListeners) {
            eventListener.onNetworksSet(getNetworks());
        }
    }

    @Override
    public final void update() {
        networksByUuid.values().forEach(Network::update);
    }

    private void extend(Network network, BlockPos fromPos, BlockPos toPos) {
        RelativeNetworkState relativeState = RelativeNetworkState.fromAbsolute(network.getState());
        relativeState.addEdge(fromPos, toPos);
        network.setState(relativeState.toAbsolute());
        assignNetworkTree(network, network.getState().getTree());
    }

    @Override
    public final boolean connect(BlockPos posA, BlockPos posB) {
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
                network.getState().setAttachment(0, new ItemStack(Items.LEATHER_CHESTPLATE));

                addNetwork(network);
            }
        }

        return true;
    }

    @Override
    public final void destroy(BlockPos pos) {
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
        if (!stack.isEmpty() && network.getState().getAttachment(offset).isEmpty()) {
            if (!simulate) {
                ItemStack insertedItem = stack.copy();
                insertedItem.setCount(1);
                setAttachment(network, offset, stack);
            }

            ItemStack returnedStack = stack.copy();
            returnedStack.shrink(1);
            return returnedStack;
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(Network network, int offset, boolean simulate) {
        ItemStack result = network.getState().getAttachment(offset);
        if (!result.isEmpty() && !simulate) {
            setAttachment(network, offset, ItemStack.EMPTY);
        }
        return result;
    }

    @Override
    public void setAttachment(Network network, int offset, ItemStack stack) {
        network.getState().setAttachment(offset, stack);

        for (INetworkManagerEventListener eventListener : eventListeners) {
            eventListener.onItemSet(network, offset, stack);
        }
    }

    @Override
    public void addMomentum(Network network, int momentum) {
        network.getState().addMomentum(momentum);
    }

    @Override
    public void addEventListener(INetworkManagerEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    @Override
    public void removeEventListener(INetworkManagerEventListener eventListener) {
        eventListeners.remove(eventListener);
    }
}
