package dev.spiritstudios.spectre.impl.client.serial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector2f;

import java.util.Optional;
import net.minecraft.util.ExtraCodecs;

public record Face(
	Vector2f uv,
	Vector2f uvSize,
	int uvRotation,
	Optional<String> material
) {
	public Face(float uMin, float vMin, float uMax, float vMax) {
		this(
			new Vector2f(uMin, vMin),
			new Vector2f(uMax, vMax),
			0,
			Optional.empty()
		);
	}

	public static final Codec<Face> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ExtraCodecs.VECTOR2F.fieldOf("uv").forGetter(Face::uv),
		ExtraCodecs.VECTOR2F.fieldOf("uv_size").forGetter(Face::uvSize),
		Codec.INT.optionalFieldOf("uv_rotation", 0).forGetter(Face::uvRotation),
		Codec.STRING.optionalFieldOf("material").forGetter(Face::material)
	).apply(instance, Face::new));
}
