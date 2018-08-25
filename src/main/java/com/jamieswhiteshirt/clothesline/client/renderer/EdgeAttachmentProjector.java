package com.jamieswhiteshirt.clothesline.client.renderer;

import com.jamieswhiteshirt.clothesline.api.INetworkState;
import com.jamieswhiteshirt.clothesline.api.Graph;
import com.jamieswhiteshirt.clothesline.api.Measurements;
import com.jamieswhiteshirt.clothesline.api.client.IClientNetworkEdge;
import com.jamieswhiteshirt.clothesline.api.client.LineProjection;
import com.jamieswhiteshirt.clothesline.api.util.MathUtil;
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

    private static float angleBetween(Graph.Edge a, Graph.Edge b) {
        float angleA = Measurements.calculateGlobalAngleY(BlockPos.ORIGIN, a.getKey().getDelta());
        float angleB = Measurements.calculateGlobalAngleY(BlockPos.ORIGIN, b.getKey().getDelta());
        return Measurements.floorModAngle(angleA - angleB);
    }

    public static EdgeAttachmentProjector build(IClientNetworkEdge edge) {
        INetworkState state = edge.getNetwork().getState();

        List<Graph.Edge> edges = state.getGraph().getEdges();
        Graph.Edge graphEdge = edge.getGraphEdge();
        Graph.Edge fromGraphEdge = edges.get(Math.floorMod(edge.getIndex() - 1, edges.size()));
        Graph.Edge toGraphEdge = edges.get(Math.floorMod(edge.getIndex() + 1, edges.size()));

        return new EdgeAttachmentProjector(
            edge.getGraphEdge().getFromOffset(),
            edge.getGraphEdge().getToOffset(),
            edge.getProjection(),
            Measurements.calculateGlobalAngleY(BlockPos.ORIGIN, graphEdge.getKey().getDelta()),
            angleBetween(fromGraphEdge, graphEdge),
            angleBetween(graphEdge, toGraphEdge)
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
        float speedRatio = (float) momentum / Measurements.UNIT_LENGTH;
        float swingMax = 3.0F * (float) angleDiff * speedRatio * speedRatio;

        return swingMax *
            (float)(Math.exp(-t / (Measurements.UNIT_LENGTH * 2.0D))) *
            MathHelper.sin((float)(Math.PI * t / Measurements.UNIT_LENGTH));
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
