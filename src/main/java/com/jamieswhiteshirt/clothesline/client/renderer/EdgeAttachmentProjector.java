package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.api.INetworkState;
import com.jamieswhiteshirt.clothesline.api.Path;
import com.jamieswhiteshirt.clothesline.api.AttachmentUnit;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.LineProjection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public final class EdgeAttachmentProjector {
    private final int fromOffset;
    private final int toOffset;
    private final LineProjection projection;
    private final float angleY;
    private final float fromAngleDiff;
    private final float toAngleDiff;

    private EdgeAttachmentProjector(int fromOffset, int toOffset, LineProjection projection, float angleY, float fromAngleDiff, float toAngleDiff) {
        this.fromOffset = fromOffset;
        this.toOffset = toOffset;
        this.projection = projection;
        this.angleY = angleY;
        this.fromAngleDiff = fromAngleDiff;
        this.toAngleDiff = toAngleDiff;
    }

    public static float floorModAngle(float angle) {
        if (angle >= 0.0F) {
            return angle % 360.0F;
        } else {
            return 360.0F + (angle % 360.0F);
        }
    }

    public static float calculateGlobalAngleY(BlockPos from, BlockPos to) {
        return floorModAngle((float)StrictMath.toDegrees(Math.atan2(to.getZ() - from.getZ(), to.getX() - from.getX())));
    }


    private static float angleBetween(Path.Edge a, Path.Edge b) {
        float angleA = calculateGlobalAngleY(BlockPos.ORIGIN, a.getDelta());
        float angleB = calculateGlobalAngleY(BlockPos.ORIGIN, b.getDelta());
        return floorModAngle(angleA - angleB);
    }

    public static EdgeAttachmentProjector build(IClientNetworkEdge edge) {
        INetworkState state = edge.getNetwork().getState();

        List<Path.Edge> edges = state.getPath().getEdges();
        Path.Edge pathEdge = edge.getPathEdge();
        Path.Edge fromPathEdge = edges.get(Math.floorMod(edge.getIndex() - 1, edges.size()));
        Path.Edge toPathEdge = edges.get(Math.floorMod(edge.getIndex() + 1, edges.size()));

        return new EdgeAttachmentProjector(
            edge.getPathEdge().getFromOffset(),
            edge.getPathEdge().getToOffset(),
            edge.getProjection(),
            calculateGlobalAngleY(BlockPos.ORIGIN, pathEdge.getDelta()),
            angleBetween(fromPathEdge, pathEdge),
            angleBetween(pathEdge, toPathEdge)
        );
    }

    private float calculateSwingAngle(double momentum, double offset) {
        if (momentum == 0.0D) {
            return 0.0F;
        }
        double t;
        double angleDiff;
        if (momentum > 0.0D) {
            t = offset - fromOffset;
            angleDiff = fromAngleDiff;
        } else {
            t = toOffset - offset;
            angleDiff = toAngleDiff;
        }
        float speedRatio = (float) momentum / AttachmentUnit.UNITS_PER_BLOCK;
        float swingMax = 3.0F * (float) angleDiff * speedRatio * speedRatio;

        return swingMax *
            (float)(Math.exp(-t / (AttachmentUnit.UNITS_PER_BLOCK * 2.0D))) *
            MathHelper.sin((float)(Math.PI * t / AttachmentUnit.UNITS_PER_BLOCK));
    }

    public Matrix4f getL2WForAttachment(double momentum, double offset, float partialTicks) {
        double relativeOffset = offset - fromOffset;
        double edgePosScalar = relativeOffset / (toOffset - fromOffset);
        Vec3d pos = projection.projectRUF(-2.0D / 16.0D, 0.0D, edgePosScalar);
        float swingAngle = calculateSwingAngle(momentum, offset);

        return new Matrix4f()
            .translate(new Vector3f((float) pos.x, (float) pos.y, (float) pos.z))
            .scale(new Vector3f(0.5F, 0.5F, 0.5F))
            .rotate((float) Math.toRadians(-angleY), new Vector3f(0.0F, 1.0F, 0.0F))
            .rotate((float) Math.toRadians(swingAngle), new Vector3f(1.0F, 0.0F, 0.0F))
            .translate(new Vector3f(0.0F, -0.5F, 0.0F));
    }
}
