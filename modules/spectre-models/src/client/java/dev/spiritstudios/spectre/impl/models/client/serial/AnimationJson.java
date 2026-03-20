package dev.spiritstudios.spectre.impl.models.client.serial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.api.models.client.animation.SpectreAnimationDefinition;

import java.util.Map;

public record AnimationJson(
	String formatVersion,
	Map<String, SpectreAnimationDefinition> animations
) {
	public static final Codec<AnimationJson> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("format_version").forGetter(AnimationJson::formatVersion),
		Codec.unboundedMap(Codec.STRING, SpectreAnimationDefinition.CODEC).fieldOf("animations").forGetter(AnimationJson::animations)
	).apply(instance, AnimationJson::new));
}
