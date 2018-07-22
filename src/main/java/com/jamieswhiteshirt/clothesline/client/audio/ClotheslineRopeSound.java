package com.jamieswhiteshirt.clothesline.client.audio;

import com.jamieswhiteshirt.clothesline.api.NetworkState;
import com.jamieswhiteshirt.clothesline.api.Graph;
import com.jamieswhiteshirt.clothesline.common.ClotheslineSoundEvents;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClotheslineRopeSound extends MovingSound {
    private final NetworkState state;
    private final Graph.Node node;

    public ClotheslineRopeSound(NetworkState state, Graph.Node node) {
        super(ClotheslineSoundEvents.BLOCK_CLOTHESLINE_ANCHOR_ROPE, SoundCategory.BLOCKS);
        this.state = state;
        this.node = node;

        this.repeat = true;
        this.xPosF = node.getKey().getX() + 0.5F;
        this.yPosF = node.getKey().getY() + 0.5F;
        this.zPosF = node.getKey().getZ() + 0.5F;

        // update();
    }

    @Override
    public void update() {
        float momentum = Math.abs((float) state.getMomentum()) / NetworkState.MAX_MOMENTUM;
        this.volume = (2 + node.getEdges().size()) * momentum * 0.2F;
        this.pitch = 0.25F + momentum * 0.75F;
    }

    @Override
    public boolean isDonePlaying() {
        return false;
    }
}
