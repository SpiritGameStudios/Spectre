package dev.spiritstudios.spectre.impl.world.entity.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;

public record AnimationControllersJson(
	String formatVersion,
	Map<String, AnimationControllerDesc> controllers
) {
	public static final Codec<AnimationControllersJson> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("format_version").forGetter(AnimationControllersJson::formatVersion),
		Codec.unboundedMap(Codec.STRING, AnimationControllerDesc.CODEC).fieldOf("animation_controllers").forGetter(AnimationControllersJson::controllers)
	).apply(instance, AnimationControllersJson::new));
}
