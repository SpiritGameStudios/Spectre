package dev.spiritstudios.spectre.api.models.client.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record SpectreAnimationDefinition(
	float lengthInSeconds,
	LoopType loop,
	Map<String, List<SpectreAnimationChannel>> boneAnimations
) {
	public SpectreKeyframeAnimation bake(ModelPart root) {
		return SpectreKeyframeAnimation.bake(root, this);
	}

	public static final Codec<SpectreAnimationDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.floatRange(0F, Float.MAX_VALUE).optionalFieldOf("animation_length", -1F).forGetter(SpectreAnimationDefinition::lengthInSeconds),
		LoopType.CODEC.optionalFieldOf("loop", LoopType.FALSE).forGetter(SpectreAnimationDefinition::loop),
		Codec.unboundedMap(
			Codec.STRING,
			Codec.dispatchedMap(
				AnimationTargets.CODEC,
				SpectreAnimationChannel::codec
			).xmap(
				map -> List.copyOf(map.values()),
				values -> values.stream().collect(Collectors.toMap(
					SpectreAnimationChannel::target,
					Function.identity()
				))
			)
		).fieldOf("bones").forGetter(SpectreAnimationDefinition::boneAnimations)
	).apply(instance, (length, loop, bones) -> {
		if (length < 0F) {
			length = bones.values().stream()
				.map(list -> {
					float lastTimestamp = -1F;

					for (SpectreAnimationChannel channel : list) {
						var last = channel.keyframes()[channel.keyframes().length - 1];
						if (last.timestamp() > lastTimestamp) lastTimestamp = last.timestamp();
					}

					return lastTimestamp;
				})
				.reduce((a, b) -> a > b ? a : b)
				.orElse(0F);
		}

		return new SpectreAnimationDefinition(length, loop, bones);
	}));
}
