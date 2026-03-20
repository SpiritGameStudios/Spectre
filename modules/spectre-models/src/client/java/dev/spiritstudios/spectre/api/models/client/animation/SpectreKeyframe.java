package dev.spiritstudios.spectre.api.models.client.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.impl.models.client.serial.CompilingCodec;

import java.util.Optional;

public record SpectreKeyframe(
	float timestamp,
	Vector3fExpression preTarget,
	Vector3fExpression postTarget,
	Interpolation interpolation
) {
	public static Codec<SpectreKeyframe> codec(float timestamp) {
		return Codec.withAlternative(
			RecordCodecBuilder.create(instance -> instance.group(
				Vector3fExpression.CODEC.optionalFieldOf("pre").forGetter(k -> Optional.of(k.preTarget)),
				Vector3fExpression.CODEC.fieldOf("post").forGetter(SpectreKeyframe::postTarget),
				Interpolation.CODEC.optionalFieldOf("lerp_mode", Interpolations.LINEAR).forGetter(SpectreKeyframe::interpolation)
			).apply(instance, (from, to, interpolation) -> new SpectreKeyframe(
				timestamp,
				from.orElse(to),
				to,
				interpolation
			))),
			CompilingCodec.MOLANG.listOf(3, 3).xmap(
				list -> new SpectreKeyframe(
					timestamp,
					Vector3fExpression.of(list),
					Vector3fExpression.of(list),
					Interpolations.LINEAR
				),
				keyframe -> {
					throw new UnsupportedOperationException("Cannot encode SpectreKeyframe.");
				}
			)
		);
	}
}
