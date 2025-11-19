package dev.spiritstudios.spectre.impl.client.serial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record GeoJson(
	String formatVersion,
	List<MinecraftGeometry> geometry
) {
	public static final Codec<GeoJson> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("format_version").forGetter(GeoJson::formatVersion),
		MinecraftGeometry.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("minecraft:geometry").forGetter(GeoJson::geometry)
	).apply(instance, GeoJson::new));
}
