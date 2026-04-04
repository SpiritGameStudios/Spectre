package dev.spiritstudios.spectre.impl.models.client.serial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.api.models.client.animation.SpectreAnimationDefinition;

import java.util.Map;

public record SpectreAnimationFile(
	String formatVersion,
	Map<String, SpectreAnimationDefinition> animations
) {
	public static final Codec<SpectreAnimationFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("format_version").forGetter(SpectreAnimationFile::formatVersion),
		Codec.unboundedMap(Codec.STRING, SpectreAnimationDefinition.CODEC).fieldOf("animations").forGetter(SpectreAnimationFile::animations)
	).apply(instance, SpectreAnimationFile::new));
}
