package com.jamieswhiteshirt.clothesline.api.client;

import com.jamieswhiteshirt.clothesline.api.Line;
import net.minecraft.util.math.Vec3d;

public final class LineProjection {
    private final Vec3d origin;
    private final Vec3d right;
    private final Vec3d up;
    private final Vec3d forward;

    public LineProjection(Vec3d origin, Vec3d right, Vec3d up, Vec3d forward) {
        this.origin = origin;
        this.right = right;
        this.up = up;
        this.forward = forward;
    }

    public Vec3d projectRUF(double r, double u, double f) {
        return origin.add(right.scale(r).add(up.scale(u)).add(forward.scale(f)));
    }

    public Vec3d projectTangentRU(double r, double u) {
        return right.scale(r).add(up.scale(u));
    }

    public static LineProjection create(Line line) {
        return create(line.getFromVec(), line.getToVec());
    }

    public static LineProjection create(Vec3d from, Vec3d to) {
        Vec3d forward = to.subtract(from);

        // The normal vector facing from the from pos to the to pos
        Vec3d forwardNormal = forward.normalize();
        // The normal vector facing right to the forward normal (on the y plane)
        Vec3d rightNormal = forwardNormal.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D)).normalize();
        if (rightNormal.equals(Vec3d.ZERO)) {
            // We are looking straight up or down so the right normal is undefined
            // Let it be x if we are looking straight up or -x if we are looking straight down
            rightNormal = new Vec3d(Math.signum(forward.y), 0.0D, 0.0D);
        }
        // The normal vector facing up from the forward normal (on the right normal plane)
        Vec3d upNormal = rightNormal.crossProduct(forwardNormal);

        return new LineProjection(from, rightNormal, upNormal, forward);
    }
}
