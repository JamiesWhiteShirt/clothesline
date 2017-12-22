package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.api.NetworkGraph;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEdge {
    private final BlockPos fromPos;
    private final BlockPos toPos;
    private final Vec3d from;
    private final double fromOffset;
    private final double toOffset;

    private final Vec3d right;
    private final Vec3d up;
    private final Vec3d forward;

    private final float angleY;

    private RenderEdge(BlockPos fromPos, BlockPos toPos, Vec3d from, double fromOffset, double toOffset, Vec3d right, Vec3d up, Vec3d forward, float angleY) {
        this.fromPos = fromPos;
        this.toPos = toPos;
        this.from = from;
        this.fromOffset = fromOffset;
        this.toOffset = toOffset;
        this.right = right;
        this.up = up;
        this.forward = forward;
        this.angleY = angleY;
    }

    public Vec3d projectVec(Vec3d vec) {
        return from.add(right.scale(vec.x).add(up.scale(vec.y)).add(forward.scale(vec.z)));
    }

    public Vec3d projectTangent(double r, double u) {
        return right.scale(r).add(up.scale(u));
    }

    public static RenderEdge create(NetworkGraph.Edge edge) {
        BlockPos fromPos = edge.getFromKey();
        BlockPos toPos = edge.getToKey();
        Vec3d fromVec = new Vec3d(fromPos).addVector(0.5D, 0.5D, 0.5D);
        Vec3d toVec = new Vec3d(toPos).addVector(0.5D, 0.5D, 0.5D);
        Vec3d forward = toVec.subtract(fromVec);

        // The normal vector facing from the from pos to the to pos
        Vec3d forwardNormal = forward.normalize();
        // The normal vector facing right to the forward normal (on the y plane)
        Vec3d rightNormal = forwardNormal.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D)).normalize();
        if (rightNormal.equals(Vec3d.ZERO)) {
            // We are looking straight up or down so the right normal is undefined
            // Let it be x instead
            rightNormal = new Vec3d(Math.signum(forward.y), 0.0D, 0.0D);
        }
        // The normal vector facing up from the forward normal (on the right normal plane)
        Vec3d upNormal = rightNormal.crossProduct(forwardNormal);


        return new RenderEdge(
                fromPos, toPos,
                fromVec,
                edge.getFromOffset(), edge.getToOffset(),
                rightNormal, upNormal, forward,
                Measurements.calculateGlobalAngle(fromPos, toPos)
        );
    }

    public double getFromOffset() {
        return fromOffset;
    }

    public double getToOffset() {
        return toOffset;
    }

    public float getAngleY() {
        return angleY;
    }

    public BlockPos getFromPos() {
        return fromPos;
    }

    public BlockPos getToPos() {
        return toPos;
    }
}
