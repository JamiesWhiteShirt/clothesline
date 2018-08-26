package com.jamieswhiteshirt.clothesline.client.impl;

import com.jamieswhiteshirt.clothesline.api.Path;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.LineProjection;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkEdge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientNetworkEdge extends NetworkEdge implements IClientNetworkEdge {
    private final LineProjection projection;

    public ClientNetworkEdge(INetwork network, Path.Edge pathEdge, int index) {
        super(network, pathEdge, index);
        projection = LineProjection.create(pathEdge.getLine());
    }

    @Override
    public LineProjection getProjection() {
        return projection;
    }
}
