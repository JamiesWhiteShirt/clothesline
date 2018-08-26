package com.jamieswhiteshirt.clothesline.client.impl;

import com.jamieswhiteshirt.clothesline.api.Path;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.client.EdgeAttachmentProjector;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.LineProjection;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkEdge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientNetworkEdge extends NetworkEdge implements IClientNetworkEdge {
    private final LineProjection projection;
    private final EdgeAttachmentProjector projector;

    public ClientNetworkEdge(INetwork network, Path.Edge pathEdge, int index) {
        super(network, pathEdge, index);
        projection = LineProjection.create(pathEdge.getLine());

        List<Path.Edge> edges = network.getState().getPath().getEdges();
        Path.Edge fromPathEdge = edges.get(Math.floorMod(index - 1, edges.size()));
        Path.Edge toPathEdge = edges.get(Math.floorMod(index + 1, edges.size()));
        projector = EdgeAttachmentProjector.build(fromPathEdge, pathEdge, toPathEdge, projection);
    }

    @Override
    public LineProjection getProjection() {
        return projection;
    }

    @Override
    public EdgeAttachmentProjector getProjector() {
        return projector;
    }
}
