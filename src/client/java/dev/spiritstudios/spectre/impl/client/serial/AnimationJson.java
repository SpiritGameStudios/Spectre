package dev.spiritstudios.spectre.impl.client.serial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.api.client.model.animation.ActorAnimation;

import java.util.Map;

public record AnimationJson(
	String formatVersion,
	Map<String, ActorAnimation> animations
) {
	public static final Codec<AnimationJson> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("format_version").forGetter(AnimationJson::formatVersion),
		Codec.unboundedMap(Codec.STRING, ActorAnimation.CODEC).fieldOf("animations").forGetter(AnimationJson::animations)
	).apply(instance, AnimationJson::new));
}
