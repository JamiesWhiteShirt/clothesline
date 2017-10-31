package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.api.AbsoluteNetworkState;
import com.jamieswhiteshirt.clothesline.api.AbsoluteTree;
import com.jamieswhiteshirt.clothesline.api.NetworkGraph;
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
    private static List<RenderEdge> createRenderEdges(NetworkGraph graph) {
        return graph.getAllEdges().stream().map(RenderEdge::create).collect(Collectors.toList());
    }

    public static RenderNetworkState fromNetworkState(AbsoluteNetworkState state) {
        return new RenderNetworkState(
                state,
                RangeLookup.build(0, state.getGraph().getAllEdges().stream().map(NetworkGraph.Edge::getFromOffset).collect(Collectors.toList())),
                createRenderEdges(state.getGraph())
        );
    }

    private final AbsoluteNetworkState state;
    private final RangeLookup offsetLookup;
    private final List<RenderEdge> edges;

    private RenderNetworkState(AbsoluteNetworkState state, RangeLookup offsetLookup, List<RenderEdge> edges) {
        this.state = state;
        this.offsetLookup = offsetLookup;
        this.edges = edges;
    }

    public AbsoluteTree getTree() {
        return state.getTree();
    }

    public MutableSortedIntMap<ItemStack> getStacks() {
        return state.getAttachments();
    }

    public int getMinNodeIndexForOffset(int offset) {
        return offsetLookup.getMinIndex(offset);
    }

    public List<RenderEdge> getEdges() {
        return edges;
    }

    public double getOffset(float partialTicks) {
        return state.getOffset() * partialTicks + state.getPreviousOffset() * (1.0F - partialTicks);
    }
}
