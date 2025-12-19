package dev.spiritstudios.spectre.impl.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.spiritstudios.spectre.impl.client.pond.SpectreModelPart;
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
	public static final boolean ENABLED = true;

	public static void debugModelPart(ModelPart part, PoseStack.Pose pose) {
		if (!ENABLED) return;

		var dir = pose.transformNormal(0, 0, 0, new Vector3f());
		var partPos = new Vec3(pose.pose().transformPosition(0, 0, 0, new Vector3f()));

		var name = ((SpectreModelPart) (Object) part).spectre$getName();
		if (name.equals("Unset")) return;
		int color = ARGB.opaque((name + "meowmeowmeow").hashCode());

		final var textStyle = TextGizmo.Style.forColorAndCentered(color).withScale(0.1F);

		Gizmos.point(partPos, color, 25F);
		Gizmos.billboardText(name, partPos.add(0, 0.35, 0), textStyle);
		Gizmos.billboardText("Pos(" + part.x + " " + part.y + " " + part.z + ")", partPos.add(0, 0.25, 0), textStyle);
		Gizmos.billboardText("Rot(" + part.xRot + " " + part.yRot + " " + part.zRot + ")", partPos.add(0, 0.15, 0), textStyle);

		Gizmos.arrow(
			partPos,
			partPos.add(dir.x, dir.y, dir.z),
			ARGB.opaque(part.hashCode())
		);
	}

	public static void debugCube(ModelPart part, PoseStack.Pose pose, ModelPart.Cube cube) {
		if (!ENABLED) return;

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

		var centre = aabb.getCenter();

		final var textStyle = TextGizmo.Style.forColorAndCentered(color).withScale(0.1F);

		Gizmos.billboardText("From(" + cube.minX + " " + cube.minY + " " + cube.minZ + ")", centre.add(0, 0.05F, 0), textStyle);
		Gizmos.billboardText("To(" + cube.maxX + " " + cube.maxY + " " + cube.maxZ + ")", centre.subtract(0, 0.05, 0), textStyle);

		Gizmos.point(centre, color, 5F);

		for (ModelPart.Polygon polygon : cube.polygons) {
			var vertices = polygon.vertices();

			var v0 = vertices[0];
			var v1 = vertices[1];
			var v2 = vertices[2];
			var v3 = vertices[3];

			var v0Pos = mat.transformPosition(v0.worldX(), v0.worldY(), v0.worldZ(), new Vector3f());
			var v1Pos = mat.transformPosition(v1.worldX(), v1.worldY(), v1.worldZ(), new Vector3f());
			var v2Pos = mat.transformPosition(v2.worldX(), v2.worldY(), v2.worldZ(), new Vector3f());
			var v3Pos = mat.transformPosition(v3.worldX(), v3.worldY(), v3.worldZ(), new Vector3f());

//			var faceCentre = v1Pos.lerp(v3Pos, 0.5F, new Vector3f());
//			Gizmos.point(new Vec3(faceCentre), color, 7.5F);

			Gizmos.point(new Vec3(v0Pos), color, 7.5F);
			Gizmos.point(new Vec3(v1Pos), color, 7.5F);
			Gizmos.point(new Vec3(v2Pos), color, 7.5F);
			Gizmos.point(new Vec3(v3Pos), color, 7.5F);

			Gizmos.rect(
				new Vec3(v0Pos),
				new Vec3(v1Pos),
				new Vec3(v2Pos),
				new Vec3(v3Pos),
				GizmoStyle.stroke(color)
			);
		}
	}
}
