package com.jamieswhiteshirt.clothesline.impl;

import com.jamieswhiteshirt.clothesline.api.INetworkEventListener;
import com.jamieswhiteshirt.clothesline.api.NetworkState;
import com.jamieswhiteshirt.clothesline.api.PersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.impl.Network;
import com.jamieswhiteshirt.clothesline.common.util.NetworkStateBuilder;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.UUID;

class NetworkTest {
    @BeforeAll
    static void bootstrap() {
        Bootstrap.register();
    }

    Network network;
    ResourceLocation eventListenerKey = new ResourceLocation("test", "test");
    @BeforeEach
    void resetNetwork() {
        BlockPos posA = new BlockPos(0, 0, 0);
        BlockPos posB = new BlockPos(1, 0, 0);
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, posA);
        stateBuilder.addEdge(posA, posB);
        network = new Network(0, new PersistentNetwork(new UUID(0, 0), stateBuilder.toAbsolute()));
    }

    void assertItemStacksEqual(ItemStack expected, ItemStack actual) {
        Assertions.assertTrue(ItemStack.areItemStacksEqual(expected, actual));
    }

    @Test
    void maxStackSizeIsZero() {
        ItemStack stack = new ItemStack(Items.STICK, 3);
        assertItemStacksEqual(new ItemStack(Items.STICK, 2), network.insertItem(0, stack, false));
        assertItemStacksEqual(new ItemStack(Items.STICK, 1), network.getAttachment(0));
        assertItemStacksEqual(stack, network.insertItem(0, stack, false));
    }

    @Test
    void simulationDoesNotChangeState() {
        ItemStack stack = new ItemStack(Items.STICK);
        assertItemStacksEqual(ItemStack.EMPTY, network.insertItem(0, stack, true));
        assertItemStacksEqual(ItemStack.EMPTY, network.getAttachment(0));

        network.insertItem(0, stack, false);

        assertItemStacksEqual(stack, network.extractItem(0, true));
        assertItemStacksEqual(stack, network.getAttachment(0));
    }

    @Test
    void simulationDoesNotFireEvents() {
        network.insertItem(1, new ItemStack(Items.STICK), false);

        INetworkEventListener eventListener = Mockito.mock(INetworkEventListener.class);
        network.addEventListener(eventListenerKey, eventListener);

        network.insertItem(0, new ItemStack(Items.STICK), true);
        network.extractItem(1, true);

        Mockito.verifyZeroInteractions(eventListener);
    }

    @Test
    void firesEventForStateChange() {
        INetworkEventListener eventListener = Mockito.mock(INetworkEventListener.class);
        network.addEventListener(eventListenerKey, eventListener);

        NetworkState stateA = network.getState();
        BlockPos posA = new BlockPos(0, 0, 0);
        BlockPos posB = new BlockPos(2, 0, 0);
        NetworkStateBuilder stateBuilder = NetworkStateBuilder.emptyRoot(0, posA);
        stateBuilder.addEdge(posA, posB);
        NetworkState stateB = stateBuilder.toAbsolute();

        network.setState(stateB);
        network.setState(stateA);
        Mockito.verify(eventListener).onStateChanged(network, stateA, stateB);
        Mockito.verify(eventListener).onStateChanged(network, stateB, stateA);

        network.removeEventListener(eventListenerKey);

        network.setState(stateB);
        Mockito.verifyZeroInteractions(eventListener);
    }

    ArgumentMatcher<ItemStack> itemStackEquals(ItemStack expected) {
        return actual -> ItemStack.areItemStacksEqual(actual, expected);
    }

    @Test
    void firesEventForAttachmentChange() {
        INetworkEventListener eventListener = Mockito.mock(INetworkEventListener.class);
        network.addEventListener(eventListenerKey, eventListener);

        ItemStack stack1 = new ItemStack(Items.APPLE);
        ItemStack stack2 = new ItemStack(Items.STICK);

        network.insertItem(0, stack1, false);
        network.insertItem(1, stack2, false);
        Mockito.verify(eventListener).onAttachmentChanged(
            ArgumentMatchers.eq(network),
            ArgumentMatchers.eq(0),
            ArgumentMatchers.argThat(itemStackEquals(ItemStack.EMPTY)),
            ArgumentMatchers.argThat(itemStackEquals(stack1))
        );
        Mockito.verify(eventListener).onAttachmentChanged(
            ArgumentMatchers.eq(network),
            ArgumentMatchers.eq(1),
            ArgumentMatchers.argThat(itemStackEquals(ItemStack.EMPTY)),
            ArgumentMatchers.argThat(itemStackEquals(stack2))
        );

        network.extractItem(0, false);
        Mockito.verify(eventListener).onAttachmentChanged(
            ArgumentMatchers.eq(network),
            ArgumentMatchers.eq(0),
            ArgumentMatchers.argThat(itemStackEquals(stack1)),
            ArgumentMatchers.argThat(itemStackEquals(ItemStack.EMPTY))
        );

        network.removeEventListener(eventListenerKey);

        network.extractItem(1, false);
        Mockito.verifyZeroInteractions(eventListener);
    }
}
