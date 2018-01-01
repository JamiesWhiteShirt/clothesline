package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.api.NetworkGraph;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderEdge {
    private final BlockPos fromPos;
    private final BlockPos toPos;
    private final double fromOffset;
    private final double toOffset;
    private final LineProjection projection;

    private final float angleY;

    private RenderEdge(BlockPos fromPos, BlockPos toPos, double fromOffset, double toOffset, LineProjection projection, float angleY) {
        this.fromPos = fromPos;
        this.toPos = toPos;
        this.fromOffset = fromOffset;
        this.toOffset = toOffset;
        this.projection = projection;
        this.angleY = angleY;
    }

    public static RenderEdge create(NetworkGraph.Edge edge) {
        BlockPos fromPos = edge.getFromKey();
        BlockPos toPos = edge.getToKey();
        Vec3d fromVec = Measurements.midVec(fromPos);
        Vec3d toVec = Measurements.midVec(toPos);


        return new RenderEdge(
                fromPos, toPos,
                edge.getFromOffset(), edge.getToOffset(),
                LineProjection.create(fromVec, toVec),
                Measurements.calculateGlobalAngleY(fromPos, toPos)
        );
    }

    public BlockPos getFromPos() {
        return fromPos;
    }

    public BlockPos getToPos() {
        return toPos;
    }

    public double getFromOffset() {
        return fromOffset;
    }

    public double getToOffset() {
        return toOffset;
    }

    public LineProjection getProjection() {
        return projection;
    }

    public float getAngleY() {
        return angleY;
    }
}
