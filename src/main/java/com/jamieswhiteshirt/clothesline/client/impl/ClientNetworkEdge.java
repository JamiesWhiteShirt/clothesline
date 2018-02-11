package com.jamieswhiteshirt.clothesline.client.impl;

import com.jamieswhiteshirt.clothesline.api.Graph;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.api.client.LineProjection;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkEdge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientNetworkEdge extends NetworkEdge implements IClientNetworkEdge {
    private final LineProjection projection;

    public ClientNetworkEdge(INetwork network, Graph.Edge graphEdge) {
        super(network, graphEdge);
        projection = LineProjection.create(Measurements.midVec(graphEdge.getFromKey()), Measurements.midVec(graphEdge.getToKey()));
    }

    @Override
    public LineProjection getProjection() {
        return projection;
    }
}
