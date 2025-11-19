package dev.spiritstudios.spectre.api.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.spiritstudios.spectre.impl.Spectre;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.BiConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;

public class SpectreModelRenderer {
	public static void render(PoseStack.Pose pose, VertexConsumer consumer, int overlay, int light, SpectreCuboid cuboid) {
		for (SpectreCuboid.Quad quad : cuboid.quads()) {
			var normal = pose.transformNormal(
				quad.normal(),
				new Vector3f()
			);

			for (SpectreCuboid.Vertex vertex : quad.vertices()) {
				var pos = pose.pose().transformPosition(
					vertex.pos().x, vertex.pos().y, vertex.pos().z,
					new Vector3f()
				);

				consumer.addVertex(
					pos.x, pos.y, pos.z,
					0xFFFFFFFF,
					vertex.u(), vertex.v(),
					overlay,
					light,
					normal.x, normal.y, normal.z
				);
			}
		}
	}

	public static void render(
		PoseStack poseStack,
		SubmitNodeCollector queue,
		RenderType renderType,
		int overlay,
		int light,
		Bone bone,
		BoneState state,
		BiConsumer<Bone, BoneState> applyAnimations
	) {
		state.set(bone);

		applyAnimations.accept(bone, state);

		poseStack.pushPose();
		{
			state.transform(poseStack);

			for (SpectreCuboid cuboid : bone.cuboids) {
				queue.submitCustomGeometry(poseStack, renderType, (entry, vertexConsumer) -> {
					render(entry, vertexConsumer, overlay, light, cuboid);
				});
			}

			for (Bone child : bone.children) {
				render(poseStack, queue, renderType, overlay, light, child, state, applyAnimations);
			}
		}
		poseStack.popPose();
	}
}
