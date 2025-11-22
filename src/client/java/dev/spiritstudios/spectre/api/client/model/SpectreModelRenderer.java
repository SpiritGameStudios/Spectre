package dev.spiritstudios.spectre.api.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.spiritstudios.spectre.api.client.model.animation.AnimationControllerRenderState;
import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.impl.client.SpectreClient;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.function.BiConsumer;

public final class SpectreModelRenderer {

	public static void render(
		PoseStack poseStack,
		SubmitNodeCollector nodeCollector,
		RenderType renderType,
		int packedLight,
		int packedOverlay,
		int tintColor,
		Query query,
		float ageInTicks,
		ResourceLocation modelLocation,
		ResourceLocation animationsLocation,
		AnimationControllerRenderState... controllers
	) {
		var animations = SpectreClient.ANIMATION_MANAGER.animations.get(animationsLocation);

		render(
			poseStack,
			nodeCollector,
			renderType,
			packedLight,
			packedOverlay,
			tintColor,
			modelLocation,
			(bone, state) -> {
				for (AnimationControllerRenderState controller : controllers) {
					controller.apply(
						bone,
						state,
						animations,
						query,
						ageInTicks
					);
				}
			}
		);
	}

	public static void render(
		PoseStack poseStack,
		SubmitNodeCollector nodeCollector,
		RenderType renderType,
		int packedLight,
		int packedOverlay,
		int tintColor,
		ResourceLocation modelLocation,
		BiConsumer<Bone, BoneState> applyAnimations
	) {
		var model = SpectreClient.MODEL_MANAGER.models.get(modelLocation);

		render(
			poseStack,
			nodeCollector,
			renderType,
			packedLight,
			packedOverlay,
			tintColor,
			model,
			applyAnimations
		);
	}

	public static void render(
		PoseStack poseStack,
		SubmitNodeCollector nodeCollector,
		RenderType renderType,
		int packedLight,
		int packedOverlay,
		int tintColor,
		SpectreModel model,
		BiConsumer<Bone, BoneState> applyAnimations
	) {
		var boneState = new BoneState(
			new Vector3f(),
			new Vector3f(),
			new Vector3f(),
			new Vector3f()
		);

		for (Bone bone : model.rootBones()) {
			SpectreModelRenderer.render(
				poseStack,
				nodeCollector,
				renderType,
				packedLight,
				packedOverlay,
				tintColor,
				bone,
				boneState,
				applyAnimations
			);
		}
	}

	public static void render(
		PoseStack.Pose pose,
		VertexConsumer consumer,
		int packedLight,
		int packedOverlay,
		int tintColor,
		SpectreCuboid cuboid
	) {
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
					tintColor,
					vertex.u(), vertex.v(),
					packedOverlay,
					packedLight,
					normal.x, normal.y, normal.z
				);
			}
		}
	}

	public static void render(
		PoseStack poseStack,
		SubmitNodeCollector queue,
		RenderType renderType,
		int packedLight,
		int packedOverlay,
		int tintColor,
		Bone bone,
		BoneState state,
		BiConsumer<Bone, BoneState> applyAnimations
	) {
		state.set(bone);

		state.offset().set(0F);
		state.pivot().set(bone.pivot);
		state.rotation().set(bone.rotation);
		state.scale().set(1F);

		applyAnimations.accept(bone, state);

		poseStack.pushPose();
		{
			state.transform(poseStack);

			for (SpectreCuboid cuboid : bone.cuboids) {
				queue.submitCustomGeometry(poseStack, renderType, (entry, vertexConsumer) -> {
					render(entry, vertexConsumer, packedLight, packedOverlay, tintColor, cuboid);
				});
			}

			for (Bone child : bone.children) {
				render(poseStack, queue, renderType, packedLight, packedOverlay, tintColor, child, state, applyAnimations);
			}
		}
		poseStack.popPose();
	}
}
