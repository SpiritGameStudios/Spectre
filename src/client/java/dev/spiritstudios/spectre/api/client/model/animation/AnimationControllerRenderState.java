package dev.spiritstudios.spectre.api.client.model.animation;

import dev.spiritstudios.spectre.api.client.model.Bone;
import dev.spiritstudios.spectre.api.client.model.BoneState;
import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.api.world.entity.animation.AnimationController;
import dev.spiritstudios.spectre.api.world.entity.animation.AnimationState;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AnimationControllerRenderState {
	public long prevStartTick;
	public @Nullable AnimationState previousState;
	public AnimationState state;
	public long transitionStartTick;

	public void copyFrom(AnimationController controller) {
		this.previousState = controller.getPreviousState();
		this.state = controller.getState();
		this.transitionStartTick = controller.getAnimStartTick();
		this.prevStartTick = controller.getPrevStartTick();
	}

	public void apply(Bone bone, BoneState boneState, Map<String, ActorAnimation> animations, Query query, float time) {
		float stateTime = (time - transitionStartTick);
		float transitionProgress = previousState == null ? 1F :
			Math.min(stateTime / (previousState.transitionLength() * SharedConstants.TICKS_PER_SECOND), 1F);

		if (transitionProgress != 1f) {
			for (String animationName : previousState.animations()) {
				var animation = animations.get(animationName);

				if (animation == null) return;

				var boneAnim = animation.bones().get(bone.name);

				if (boneAnim == null) return;

				boneAnim.update(
					boneState,
					animation,
					query,
					time - prevStartTick,
					1F - transitionProgress
				);
			}
		}

		for (String animationName : state.animations()) {
			var animation = animations.get(animationName);

			if (animation == null) return;

			var boneAnim = animation.bones().get(bone.name);

			if (boneAnim == null) return;

			boneAnim.update(
				boneState,
				animation,
				query,
				time - transitionStartTick,
				transitionProgress
			);
		}
	}
}
