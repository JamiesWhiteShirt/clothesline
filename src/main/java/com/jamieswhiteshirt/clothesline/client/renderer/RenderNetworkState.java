package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.api.AbsoluteNetworkState;
import com.jamieswhiteshirt.clothesline.api.AbsoluteTree;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.api.util.RangeLookup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class RenderNetworkState {
    private static List<RenderEdge> createRenderEdges(NodeLoop nodeLoop) {
        RenderEdge[] renderEdges = new RenderEdge[nodeLoop.size()];
        for (int i = 0; i < renderEdges.length; i++) {
            renderEdges[i] = RenderEdge.create(nodeLoop.get(i), nodeLoop.get(i + 1));
        }
        return Arrays.asList(renderEdges);
    }

    public static RenderNetworkState fromNetworkState(AbsoluteNetworkState state) {
        NodeLoop nodeLoop = NodeLoop.fromTree(state.getTree());
        return new RenderNetworkState(
                state,
                nodeLoop,
                RangeLookup.build(0, nodeLoop.getNodes().stream().map(Node::getOffset).collect(Collectors.toList())),
                createRenderEdges(nodeLoop)
        );
    }

    private final AbsoluteNetworkState state;
    private final NodeLoop nodeLoop;
    private final RangeLookup offsetLookup;
    private final List<RenderEdge> edges;

    private RenderNetworkState(AbsoluteNetworkState state, NodeLoop nodeLoop, RangeLookup offsetLookup, List<RenderEdge> edges) {
        this.state = state;
        this.nodeLoop = nodeLoop;
        this.offsetLookup = offsetLookup;
        this.edges = edges;
    }

    public AbsoluteTree getTree() {
        return state.getTree();
    }

    public MutableSortedIntMap<ItemStack> getStacks() {
        return state.getStacks();
    }

    public int getMinNodeIndexForOffset(int offset) {
        return offsetLookup.getMinIndex(offset);
    }

    public NodeLoop getNodeLoop() {
        return nodeLoop;
    }

    public RangeLookup getOffsetLookup() {
        return offsetLookup;
    }

    public List<RenderEdge> getEdges() {
        return edges;
    }

    public double getOffset(float partialTicks) {
        return state.getOffset() * partialTicks + state.getPreviousOffset() * (1.0F - partialTicks);
    }
}
