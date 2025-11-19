package dev.spiritstudios.spectre.api.world.entity.animation;

import java.util.List;

public record AnimationState(
	List<String> animations,
	List<Transition> transitions,
	float transitionLength
) {
	public record Transition(AnimationState state, BooleanExpression condition) {
	}
}
