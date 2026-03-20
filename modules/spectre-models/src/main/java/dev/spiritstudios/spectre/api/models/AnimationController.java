package dev.spiritstudios.spectre.api.models;

import net.minecraft.util.EasingType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class AnimationController {
	private final List<State> states;

	private float previousStateBeginTime = -1;
	private @Nullable State previousState = null;

	private float blendTime = 0;
	private EasingType blendFunction = EasingType.LINEAR;

	private float currentStateBeginTime = 0;
	private State currentState;

	private AnimationController(List<State> states, State initialState) {
		this.states = states;
		this.currentState = initialState;
	}

	public void tick(float time) {
		for (TransitionSupplier supplier : currentState.transitions) {
			Transition transition = supplier.pickTransition();

			if (transition != null) {
				this.previousStateBeginTime = currentStateBeginTime;
				this.previousState = this.currentState;

				this.blendTime = transition.blendTime;
				this.blendFunction = transition.blendFunction;

				this.currentStateBeginTime = time;
				this.currentState = transition.newState;
			}
		}
	}

	public float getPreviousStateBeginTime() {
		return previousStateBeginTime;
	}

	public @Nullable State getPreviousState() {
		return previousState;
	}

	public float getCurrentStateBeginTime() {
		return currentStateBeginTime;
	}

	public @Nullable State getCurrentState() {
		return currentState;
	}

	public float getBlendTime() {
		return blendTime;
	}

	public EasingType getBlendFunction() {
		return blendFunction;
	}

	public record Transition(State newState, float blendTime, EasingType blendFunction) {
		public Transition(State newState) {
			this(newState, 0, EasingType.LINEAR);
		}

		public Transition(State newState, float blendTime) {
			this(newState, blendTime, EasingType.LINEAR);
		}
	}

	public interface TransitionSupplier {
		@Nullable Transition pickTransition();
	}

	public record State(String name, List<TransitionSupplier> transitions, List<String> animations) {

	}

	public static class Builder {
		private final List<State> states = new ArrayList<>();

		public StateBuilder createState(String name) {
			State state = new State(name, new ArrayList<>(1), new ArrayList<>());
			states.add(state);

			return new StateBuilder(state);
		}

		public AnimationController build(StateBuilder initialState) {
			return new AnimationController(states, initialState.state);
		}

		public static class StateBuilder {
			public final State state;

			private StateBuilder(State state) {
				this.state = state;
			}

			public StateBuilder transition(TransitionSupplier supplier) {
				state.transitions.add(supplier);
				return this;
			}

			public StateBuilder animation(String name) {
				state.animations.add(name);
				return this;

			}

		}
	}
}
