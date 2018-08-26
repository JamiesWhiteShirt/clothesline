package com.jamieswhiteshirt.clothesline.api.client;

import com.jamieswhiteshirt.clothesline.api.INetworkEdge;

public interface IClientNetworkEdge extends INetworkEdge {
    LineProjection getProjection();

    EdgeAttachmentProjector getProjector();
}
