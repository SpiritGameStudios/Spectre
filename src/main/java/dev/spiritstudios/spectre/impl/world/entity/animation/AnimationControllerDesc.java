package dev.spiritstudios.spectre.impl.world.entity.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.api.world.entity.animation.AnimationController;
import dev.spiritstudios.spectre.api.world.entity.animation.AnimationState;
import dev.spiritstudios.spectre.api.world.entity.animation.BooleanExpression;
import net.minecraft.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record AnimationControllerDesc(
	String initialState,
	Map<String, State> states
) {
	public static final Codec<AnimationControllerDesc> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("initial_state").forGetter(AnimationControllerDesc::initialState),
		Codec.unboundedMap(Codec.STRING, State.CODEC).fieldOf("states").forGetter(AnimationControllerDesc::states)
	).apply(instance, AnimationControllerDesc::new));

	public record State(
		List<String> animations,
		List<Transition> transitions,
		float blendTransition
	) {
		public static final Codec<State> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().optionalFieldOf("animations", Collections.emptyList()).forGetter(State::animations),
			Transition.CODEC.listOf().optionalFieldOf("transitions", Collections.emptyList()).forGetter(State::transitions),
			ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("blend_transition", 0F).forGetter(State::blendTransition)
		).apply(instance, State::new));

		public record Transition(String state, BooleanExpression condition) {
			public static final Codec<Transition> CODEC = Codec.unboundedMap(Codec.STRING, BooleanExpression.CODEC).comapFlatMap(
				map -> {
					if (map.size() != 1) return DataResult.error(() -> "Transition map must have exactly 1 entry!");
					return map.entrySet().stream().findFirst()
						.map(entry -> DataResult.success(
							new Transition(entry.getKey(), entry.getValue())
						))
						.orElse(DataResult.error(() -> "This should be impossible. Submit a bug report to Spirit Studios if you see this."));
				},
				transition -> Map.of(transition.state, transition.condition)
			);
		}
	}

	public AnimationController bake(long time) {
		Map<String, AnimationState> states = this.states.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> {
					var state = entry.getValue();
					return new AnimationState(
						state.animations(),
						new ArrayList<>(state.transitions().size()),
						state.blendTransition
					);
				}
			));

		states.forEach((name, state) -> {
			var unbaked = this.states.get(name);

			for (State.Transition transition : unbaked.transitions) {
				state.transitions().add(new AnimationState.Transition(
					states.get(transition.state),
					transition.condition
				));
			}
		});

		return new AnimationController(states.get(initialState), time);
	}
}
