package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.INetworkState;
import com.jamieswhiteshirt.clothesline.api.INetwork;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;

public class Validation {
    public static boolean canReachAttachment(EntityPlayerMP player, INetwork network, int attachmentKey) {
        INetworkState state = network.getState();
        return canReachPos(player, state.getGraph().getPositionForOffset(state.attachmentKeyToOffset(attachmentKey)));
    }

    public static boolean canReachPos(EntityPlayerMP player, Vec3d pos) {
        Vec3d delta = pos.subtract(player.posX, player.posY + 1.5D, player.posZ);
        double maxDistance = player.interactionManager.getBlockReachDistance() + 1;
        return delta.lengthSquared() < maxDistance * maxDistance;
    }
}
