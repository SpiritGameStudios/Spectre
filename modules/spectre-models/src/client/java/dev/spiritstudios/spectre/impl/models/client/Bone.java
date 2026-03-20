package dev.spiritstudios.spectre.impl.models.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.impl.models.client.serial.Cube;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

public record Bone(String name, List<Bone> children, List<Cube> cubes, Vector3fc pivot, Vector3fc rotation) {
	public static final Codec<Bone> CODEC = Codec.recursive(
		"bone",
		boneCodec -> RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("name", "Bone").forGetter(b -> b.name),
			boneCodec.listOf().optionalFieldOf("children", List.of()).forGetter(b -> b.children),
			Cube.CODEC.listOf().optionalFieldOf("cubes", List.of()).forGetter(b -> b.cubes),
			ExtraCodecs.VECTOR3F.optionalFieldOf("pivot", new Vector3f()).forGetter(b -> b.pivot),
			ModelCodecs.ROTATION_VECTOR.optionalFieldOf("rotation", new Vector3f()).forGetter(b -> b.pivot)
		).apply(instance, Bone::new))
	);

	public void bake(PartDefinition parent, @Nullable Bone parentBone, String layer) {
		var cubes = new CubeListBuilder();

		Vector3f pivot = new Vector3f(this.pivot);

		boolean hasParent = parentBone != null;

		var origin = new Vector3f(pivot);
		if (hasParent) origin.sub(parentBone.pivot);

		for (Cube cuboid : this.cubes) {
			if (cuboid.layer().equals(layer)) {
				cuboid.bake(cubes);
			}
		}

		var part = parent.addOrReplaceChild(
			name,
			cubes,
			PartPose.offsetAndRotation(
				origin.x(), origin.y(), origin.z(),
				rotation.x(), rotation.y(), rotation.z()
			)
		);

		for (Bone child : children) {
			child.bake(part, this, layer);
		}
	}
}
