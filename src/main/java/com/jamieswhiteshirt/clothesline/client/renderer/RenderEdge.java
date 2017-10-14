package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.api.Node;
import net.minecraft.util.math.Vec3d;

public class RenderEdge {
    private final Vec3d from;
    private final double fromOffset;
    private final double toOffset;

    private final Vec3d right;
    private final Vec3d up;
    private final Vec3d forward;

    private final float angleY;

    private RenderEdge(Vec3d from, double fromOffset, double toOffset, Vec3d right, Vec3d up, Vec3d forward, float angleY) {
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

    public static RenderEdge create(Node from, Node to) {
        Vec3d fromVec = new Vec3d(from.getTree().getPos()).addVector(0.5D, 0.5D, 0.5D);
        Vec3d toVec = new Vec3d(to.getTree().getPos()).addVector(0.5D, 0.5D, 0.5D);
        Vec3d forward = toVec.subtract(fromVec);

        // The normal vector facing from the from pos to the to pos
        Vec3d forwardNormal = forward.normalize();
        // The normal vector facing right to the forward normal (on the y plane)
        Vec3d rightNormal = forwardNormal.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D)).normalize();
        if (rightNormal.equals(Vec3d.ZERO)) {
            // We are looking straight up or down so the right normal is undefined
            // Let it be x instead
            rightNormal = new Vec3d(1.0D, 0.0D, 0.0D);
        }
        // The normal vector facing up from the forward normal (on the right normal plane)
        Vec3d upNormal = rightNormal.crossProduct(forwardNormal);


        return new RenderEdge(fromVec, from.getOffset(), to.getOffset(), rightNormal, upNormal, forward, Measurements.calculateGlobalAngle(from.getTree().getPos(), to.getTree().getPos()));
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
}
