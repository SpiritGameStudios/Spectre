package dev.spiritstudios.spectre.impl.models.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.impl.models.client.serial.Cube;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

public record Bone(String name, List<Bone> children, List<Cube> cubes, Vector3fc offset, Vector3fc rotation) {
	public static final Codec<Bone> CODEC = Codec.recursive(
		"bone",
		boneCodec -> RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("name", "Bone").forGetter(b -> b.name),
			boneCodec.listOf().optionalFieldOf("children", List.of()).forGetter(b -> b.children),
			Cube.CODEC.listOf().optionalFieldOf("cubes", List.of()).forGetter(b -> b.cubes),
			ExtraCodecs.VECTOR3F.optionalFieldOf("offset", new Vector3f()).forGetter(b -> b.offset),
			ModelCodecs.ROTATION_VECTOR.optionalFieldOf("rotation", new Vector3f()).forGetter(b -> b.rotation)
		).apply(instance, Bone::new))
	);

	public void bake(PartDefinition parent, String layer) {
		var cubes = new CubeListBuilder();

		for (Cube cuboid : this.cubes) {
			if (cuboid.layer().equals(layer)) {
				cuboid.bake(cubes);
			}
		}

		var part = parent.addOrReplaceChild(
			name,
			cubes,
			PartPose.offsetAndRotation(
				offset.x(), offset.y(), offset.z(),
				rotation.x(), rotation.y(), rotation.z()
			)
		);

		for (Bone child : children) {
			child.bake(part, layer);
		}
	}
}
