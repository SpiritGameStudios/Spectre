package dev.spiritstudios.spectre.impl.client.serial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.api.client.model.ModelCodecs;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Optional;

public record ModelBone(
	String name,
	Optional<String> parent,
	Vector3fc pivot,
	Vector3fc rotation,
	boolean mirror,
	float inflate,
	List<Cube> cubes
) {
	public static final Codec<ModelBone> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("name").forGetter(ModelBone::name),
		Codec.STRING.optionalFieldOf("parent").forGetter(ModelBone::parent),
		ExtraCodecs.VECTOR3F.optionalFieldOf("pivot", new Vector3f(0F)).<Vector3fc>xmap(
			vec -> vec.mul(-1F, 1F, 1F, new Vector3f()),
			vec -> vec.mul(-1F, 1F, 1F, new Vector3f())
		).forGetter(ModelBone::pivot),
		ModelCodecs.ROTATION_VECTOR.optionalFieldOf("rotation", new Vector3f(0F)).forGetter(ModelBone::rotation),
		Codec.BOOL.optionalFieldOf("mirror", false).forGetter(ModelBone::mirror),
		Codec.FLOAT.optionalFieldOf("inflate", 0F).forGetter(ModelBone::inflate),
		Cube.CODEC.listOf().optionalFieldOf("cubes", List.of()).forGetter(ModelBone::cubes)
	).apply(instance, ModelBone::new));
}
