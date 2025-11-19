package dev.spiritstudios.spectre.api.client.model.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;

public record ActorAnimation(
	LoopType loop,
	float length,
	Map<String, SpectreBoneAnimation> bones
) {
	public static final Codec<ActorAnimation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		LoopType.CODEC.optionalFieldOf("loop", LoopType.FALSE).forGetter(ActorAnimation::loop),
		Codec.floatRange(0F, Float.MAX_VALUE).optionalFieldOf("animation_length", -1F).forGetter(ActorAnimation::length),
		Codec.dispatchedMap(
			Codec.STRING,
			SpectreBoneAnimation::codec
		).fieldOf("bones").forGetter(ActorAnimation::bones)
	).apply(instance, (loop, length, bones) -> {
		if (length < 0F) {
			length = bones.values().stream()
				.map(SpectreBoneAnimation::lastKeyframe)
				.reduce((a, b) -> a > b ? a : b)
				.orElse(0F);
		}

		return new ActorAnimation(loop, length, bones);
	}));
}
