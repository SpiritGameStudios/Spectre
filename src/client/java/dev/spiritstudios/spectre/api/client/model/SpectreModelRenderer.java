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
		int overlay,
		int light,
		Bone bone,
		BoneState state,
		BiConsumer<Bone, BoneState> applyAnimations
	) {
		state.pivot().set(bone.pivot);
		state.rotation().set(bone.rotation);
		state.scale().set(1F);
		state.offset().set(0F);

		applyAnimations.accept(bone, state);

		poseStack.pushPose();
		{
			poseStack.translate(state.offset().x, state.offset().y, state.offset().z);

			poseStack.translate(state.pivot().x, state.pivot().y, state.pivot().z);
			poseStack.mulPose(
				new Quaternionf()
					.rotateZYX(
						state.rotation().z,
						-state.rotation().y,
						-state.rotation().x
					)
			);
			poseStack.scale(state.scale().x, state.scale().y, state.scale().z);
			poseStack.translate(-state.pivot().x, -state.pivot().y, -state.pivot().z);

			for (SpectreCuboid cuboid : bone.cuboids) {
				queue.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(Spectre.id("textures/entity/bloomray.png")), (entry, vertexConsumer) -> {
					render(entry, vertexConsumer, overlay, light, cuboid);
				});
			}

			for (Bone child : bone.children) {
				render(poseStack, queue, overlay, light, child, state, applyAnimations);
			}
		}
		poseStack.popPose();
	}
}
