package dev.spiritstudios.spectre.api.client.model.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.spiritstudios.spectre.api.core.exception.ImpossibleStateException;
import net.minecraft.client.animation.AnimationChannel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

public record SpectreAnimationChannel(AnimationChannel.Target target, SpectreKeyframe... keyframes) {
	public static Codec<SpectreAnimationChannel> codec(AnimationChannel.Target target) {
		return Codec.withAlternative(
			Codec.dispatchedMap(
				// Json stores all string keys as strings so we need to do this
				Codec.STRING.comapFlatMap(
					str -> {
						try {
							return DataResult.success(Float.parseFloat(str));
						} catch (NumberFormatException e) {
							return DataResult.error(() -> "Not a valid number: " + str);
						}
					},
					Object::toString
				),
				SpectreKeyframe::codec
			).xmap(
				map -> new SpectreAnimationChannel(
					target,
					map.values()
						.stream()
						.sorted(Comparator.comparing(SpectreKeyframe::timestamp))
						.toArray(SpectreKeyframe[]::new)
				),
				channel -> Arrays.stream(channel.keyframes)
					.collect(Collectors.toMap(
						SpectreKeyframe::timestamp,
						Function.identity()
					))
			),
			Vector3fExpression.CODEC.xmap(
				exp -> new SpectreAnimationChannel(
					target,
					new SpectreKeyframe(
						0F,
						exp, exp,
						Interpolation.LINEAR
					)
				),
				channel -> {
					throw new ImpossibleStateException();
				}
			)
		);
	}
}
