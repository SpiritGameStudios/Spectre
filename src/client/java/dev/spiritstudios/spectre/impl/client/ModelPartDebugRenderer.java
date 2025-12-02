package dev.spiritstudios.spectre.impl.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ModelPartDebugRenderer {
	public static void debugModelPart(ModelPart part, PoseStack.Pose pose) {
		var dir = pose.transformNormal(0, 0, 0, new Vector3f());
		var partPos = new Vec3(pose.pose().transformPosition(new Vector3f()));

		var name = ((SpectreModelPart) (Object) part).spectre$getName();
		if (name.equals("Unset")) return;
		int color = ARGB.opaque((name + "meowmeowmeow").hashCode());

		final var textStyle = TextGizmo.Style.forColorAndCentered(color).withScale(0.1F);

		Gizmos.point(partPos, color, 25F);
		Gizmos.billboardText(name, partPos.add(0, 0.25, 0), textStyle);

		Gizmos.arrow(
			partPos,
			partPos.add(dir.x, dir.y, dir.z),
			ARGB.opaque(part.hashCode())
		);
	}

	public static void debugCube(ModelPart part, PoseStack.Pose pose, ModelPart.Cube cube) {
		Matrix4f mat = pose.pose();
		var min = new Vector3f(cube.minX, cube.minY, cube.minZ).div(16F);
		var max = new Vector3f(cube.maxX, cube.maxY, cube.maxZ).div(16F);

		var name = ((SpectreModelPart) (Object) part).spectre$getName();
		if (name.equals("Unset")) return;
		int color = ARGB.opaque((name + "meowmeowmeow").hashCode());

		var aabb = new AABB(
			new Vec3(mat.transformPosition(min)),
			new Vec3(mat.transformPosition(max))
		);

		Gizmos.cuboid(
			aabb,
			GizmoStyle.stroke(color)
		);
	}
}
