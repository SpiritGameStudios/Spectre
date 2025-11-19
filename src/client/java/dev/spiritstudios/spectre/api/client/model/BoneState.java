package dev.spiritstudios.spectre.api.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record BoneState(Vector3f pivot, Vector3f offset, Vector3f rotation, Vector3f scale) {
	public void transform(PoseStack poseStack) {
		poseStack.translate(offset.x, offset.y, offset.z);

		poseStack.translate(pivot.x, pivot.y, pivot.z);
		poseStack.mulPose(
			new Quaternionf()
				.rotateZYX(
					rotation.z,
					-rotation.y,
					-rotation.x
				)
		);
		poseStack.scale(scale.x, scale.y, scale.z);
		poseStack.translate(-pivot.x, -pivot.y, -pivot.z);
	}

	public void set(Bone bone) {
		pivot.set(bone.pivot);
		rotation.set(bone.rotation);
		scale.set(1F);
		offset.set(0F);
	}
}
