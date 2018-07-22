package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.api.NetworkState;
import com.jamieswhiteshirt.clothesline.api.Graph;
import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.LineProjection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public final class EdgeAttachmentProjector {
    private final int fromOffset;
    private final int toOffset;
    private final LineProjection projection;
    private final float angleY;
    private final float angleDiff;

    private EdgeAttachmentProjector(int fromOffset, int toOffset, LineProjection projection, float angleY, float angleDiff) {
        this.fromOffset = fromOffset;
        this.toOffset = toOffset;
        this.projection = projection;
        this.angleY = angleY;
        this.angleDiff = angleDiff;
    }

    public static EdgeAttachmentProjector build(IClientNetworkEdge edge) {
        NetworkState state = edge.getNetwork().getState();
        Graph.Edge previousGraphEdge = state.getGraph().getEdgeForOffset(Math.floorMod(edge.getGraphEdge().getFromOffset() - 1, state.getLoopLength()));
        float angleY = edge.getGraphEdge().getKey().getAngle();
        float angleDiff = (angleY - previousGraphEdge.getKey().getAngle() + 360.0F) % 360.0F;
        return new EdgeAttachmentProjector(
            edge.getGraphEdge().getFromOffset(),
            edge.getGraphEdge().getToOffset(),
            edge.getProjection(),
            angleY,
            angleDiff
        );
    }

    public Matrix4f getL2WForAttachment(double momentum, double offset, float partialTicks) {
        float speedRatio = (float) momentum / NetworkState.MAX_MOMENTUM;
        float swingMax = angleDiff / 4.0F * speedRatio * speedRatio;
        double relativeOffset = offset - fromOffset;
        double edgePosScalar = relativeOffset / (toOffset - fromOffset);
        Vec3d pos = projection.projectRUF(-2.0D / 16.0D, 0.0D, edgePosScalar);

        float swingAngle = swingMax *
            (float)(Math.exp(-relativeOffset / (Measurements.UNIT_LENGTH * 2.0D))) *
            MathHelper.sin((float)(relativeOffset / (NetworkState.MAX_MOMENTUM * 2.0D)));

        return new Matrix4f()
            .translate(new Vector3f((float) pos.x, (float) pos.y, (float) pos.z))
            .scale(new Vector3f(0.5F, 0.5F, 0.5F))
            .rotate((float) Math.toRadians(-angleY), new Vector3f(0.0F, 1.0F, 0.0F))
            .rotate((float) Math.toRadians(swingAngle), new Vector3f(1.0F, 0.0F, 0.0F))
            .translate(new Vector3f(0.0F, -0.5F, 0.0F));
    }
}
