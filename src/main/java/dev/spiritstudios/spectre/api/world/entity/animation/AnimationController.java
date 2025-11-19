package dev.spiritstudios.spectre.api.world.entity.animation;

import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.impl.world.entity.animation.AnimationControllerManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class AnimationController {
	public static AnimationController create(ResourceLocation id, String name, long time) {
		var controllers = AnimationControllerManager.INSTANCE.controllers.get(id);
		var desc = controllers.get(name);

		return desc.bake(time);
	}

	private @Nullable AnimationState previousState = null;
	private AnimationState state;

	private long prevStartTick;
	private long transitionStartTick;


	public AnimationController(AnimationState initialState, long time) {
		this.state = initialState;
		this.transitionStartTick = time;
	}

	public void tick(Query query, long time) {
		for (AnimationState.Transition transition : state.transitions()) {
			if (transition.condition().evaluate(query, null)) {
				transition(transition.state(), time);
				break;
			}
		}
	}

	public void transition(AnimationState newState, long currentTime) {
		prevStartTick = transitionStartTick;
		transitionStartTick = currentTime;
		previousState = state;
		state = newState;
	}

	public AnimationState getState() {
		return state;
	}

	public @Nullable AnimationState getPreviousState() {
		return previousState;
	}

	public long getTransitionStartTick() {
		return transitionStartTick;
	}

	public long getPrevStartTick() {
		return prevStartTick;
	}
}
