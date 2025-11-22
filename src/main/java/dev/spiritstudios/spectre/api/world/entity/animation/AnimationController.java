package dev.spiritstudios.spectre.api.world.entity.animation;

import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.impl.world.entity.animation.AnimationControllerManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class AnimationController {

	public static AnimationController create(ResourceLocation id, String name, Entity entity) {
		var controllers = AnimationControllerManager.INSTANCE.controllers.get(id);
		var desc = controllers.get(name);

		AnimationController controller = new AnimationController(
			id,
			name,
			desc.bake(),
			entity
		);

		AnimationControllerManager.CONTROLLERS.add(controller);

		return controller;
	}

	public final ResourceLocation location;
	public final String name;

	public final Entity entity;

	private @Nullable AnimationState previousState = null;
	private AnimationState state;

	private long prevStartTick;
	private long animStartTick;


	public AnimationController(ResourceLocation location, String name, AnimationState initialState, Entity entity) {
		this.location = location;
		this.name = name;
		this.entity = entity;

		this.state = initialState;
		this.animStartTick = entity.tickCount;
	}

	public void tick(Query query) {
		for (AnimationState.Transition transition : state.transitions()) {
			if (transition.condition().evaluate(query, null)) {
				transition(transition.state());
				break;
			}
		}
	}

	public void transition(AnimationState newState) {
		prevStartTick = animStartTick;
		animStartTick = entity.tickCount;
		previousState = state;
		state = newState;
	}

	public AnimationState getState() {
		return state;
	}

	public @Nullable AnimationState getPreviousState() {
		return previousState;
	}

	public long getAnimStartTick() {
		return animStartTick;
	}

	public long getPrevStartTick() {
		return prevStartTick;
	}
}
