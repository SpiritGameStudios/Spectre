package dev.spiritstudios.spectre.impl.client.serial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.api.data.serialization.SpectreCodecs;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import net.minecraft.util.ExtraCodecs;

public record ModelBone(
	String name,
	Optional<String> parent,
	Vector3f pivot,
	Vector3f rotation,
	boolean mirror,
	float inflate,
	List<Cube> cubes
) {
	public static final Codec<ModelBone> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("name").forGetter(ModelBone::name),
		Codec.STRING.optionalFieldOf("parent").forGetter(ModelBone::parent),
		SpectreCodecs.VECTOR_3F_SIXTEENTH.fieldOf("pivot").forGetter(ModelBone::pivot),
		ExtraCodecs.VECTOR3F.optionalFieldOf("rotation", new Vector3f()).forGetter(ModelBone::rotation),
		Codec.BOOL.optionalFieldOf("mirror", false).forGetter(ModelBone::mirror),
		Codec.FLOAT.optionalFieldOf("inflate", 0F).forGetter(ModelBone::inflate),
		Cube.CODEC.listOf().optionalFieldOf("cubes", List.of()).forGetter(ModelBone::cubes)
	).apply(instance, ModelBone::new));
}
