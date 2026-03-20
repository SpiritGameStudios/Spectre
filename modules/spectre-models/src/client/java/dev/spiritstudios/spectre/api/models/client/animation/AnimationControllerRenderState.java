package dev.spiritstudios.spectre.api.models.client.animation;

import dev.spiritstudios.spectre.api.models.AnimationController;
import dev.spiritstudios.spectre.api.models.client.Query;
import net.minecraft.SharedConstants;
import net.minecraft.util.EasingType;

import java.util.Map;

public class AnimationControllerRenderState {
	private float previousStateBeginTime = -1;
	private AnimationController.State previousState = null;

	private float blendTime = 0;
	private EasingType blendFunction = EasingType.LINEAR;

	private float currentStateBeginTime = 0;
	private AnimationController.State currentState;

	public void copyFrom(AnimationController controller) {
		this.previousStateBeginTime = controller.getPreviousStateBeginTime();
		this.previousState = controller.getPreviousState();

		this.blendTime = controller.getBlendTime();
		this.blendFunction = controller.getBlendFunction();

		this.currentStateBeginTime = controller.getCurrentStateBeginTime();
		this.currentState = controller.getCurrentState();
	}

	public void apply(Map<String, SpectreKeyframeAnimation> animations, Query query, float time) {
		float stateTime = (time - currentStateBeginTime);
		float transitionProgress = previousState == null ? 1F :
			Math.min(stateTime / (blendTime * SharedConstants.TICKS_PER_SECOND), 1F);

		transitionProgress = blendFunction.apply(transitionProgress);

		if (transitionProgress != 1f) {
			for (String animationName : previousState.animations()) {
				var animation = animations.get(animationName);

				if (animation == null) return;

				animation.apply(
					query,
					time - previousStateBeginTime,
					1F - transitionProgress
				);
			}
		}

		for (String animationName : currentState.animations()) {
			var animation = animations.get(animationName);

			if (animation == null) return;

			animation.apply(
				query,
				time - currentStateBeginTime,
				transitionProgress
			);
		}
	}
}
