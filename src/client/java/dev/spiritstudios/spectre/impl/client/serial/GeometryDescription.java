package dev.spiritstudios.spectre.impl.client.serial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3fc;

public record GeometryDescription(
	String id,
	float visibleBoundsWidth,
	float visibleBoundsHeight,
	Vector3fc visibleBoundsOffset,
	int textureWidth,
	int textureHeight
) {
	public static final Codec<GeometryDescription> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("identifier").forGetter(GeometryDescription::id),
		Codec.FLOAT.fieldOf("visible_bounds_width").forGetter(GeometryDescription::visibleBoundsWidth),
		Codec.FLOAT.fieldOf("visible_bounds_height").forGetter(GeometryDescription::visibleBoundsHeight),
		ExtraCodecs.VECTOR3F.fieldOf("visible_bounds_offset").forGetter(GeometryDescription::visibleBoundsOffset),
		Codec.INT.fieldOf("texture_width").forGetter(GeometryDescription::textureWidth),
		Codec.INT.fieldOf("texture_height").forGetter(GeometryDescription::textureHeight)
	).apply(instance, GeometryDescription::new));
}
