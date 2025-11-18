package dev.spiritstudios.spectre.api.client.model.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.api.client.model.serial.LerpMode;
import dev.spiritstudios.spectre.api.data.serialization.SpectreCodecs;

import java.util.Optional;

public record SpectreKeyframe(
	float timestamp,
	Vector3fExpression from,
	Vector3fExpression to,
	LerpMode lerpMode
) {
	public static Codec<SpectreKeyframe> codec(float timestamp, boolean rot) {
		return Codec.withAlternative(
			RecordCodecBuilder.create(instance -> instance.group(
				(rot ? Vector3fExpression.CODEC_SIXTEENTH : Vector3fExpression.CODEC).optionalFieldOf("pre").forGetter(k -> Optional.of(k.from)),
				(rot ? Vector3fExpression.CODEC_SIXTEENTH : Vector3fExpression.CODEC).fieldOf("post").forGetter(SpectreKeyframe::to),
				LerpMode.CODEC.optionalFieldOf("lerp_mode", LerpMode.LINEAR).forGetter(SpectreKeyframe::lerpMode)
			).apply(instance, (from, to, lerp) -> new SpectreKeyframe(
				timestamp,
				from.orElse(to),
				to,
				lerp
			))),
			SpectreCodecs.MOLANG.listOf(3, 3).xmap(
				list -> new SpectreKeyframe(
					timestamp,
					rot ? Vector3fExpression.ofSixteenth(list) : Vector3fExpression.of(list),
					rot ? Vector3fExpression.ofSixteenth(list) : Vector3fExpression.of(list),
					LerpMode.LINEAR
				),
				keyframe -> {
					throw new UnsupportedOperationException("Cannot encode SpectreKeyframe.");
				}
			)
		);
	}
}
