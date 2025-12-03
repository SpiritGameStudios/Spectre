package dev.spiritstudios.spectre.api.client.model;

import dev.spiritstudios.spectre.impl.client.serial.Cube;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

public class Bone {
	public final String name;

	public final List<Bone> children = new ArrayList<>(0);
	public final List<Cube> cuboids;

	public final Vector3fc rotation;
	public final Vector3fc pivot;

	public Bone(String name, List<Cube> cuboids, Vector3fc pivot, Vector3fc rotation) {
		this.name = name;

		this.cuboids = cuboids;
		this.pivot = pivot;
		this.rotation = rotation;
	}

	public void bake(PartDefinition parent, @Nullable Bone parentBone) {
		var cubes = new CubeListBuilder();
		List<Cube> deferred = new ArrayList<>();

		for (Cube cuboid : cuboids) {
			if (!cuboid.rotation().equals(0F, 0F, 0F) || !cuboid.pivot().equals(0F, 0F, 0F)) {
				deferred.add(cuboid);
			} else {
				cuboid.bake(cubes, pivot);
			}
		}

		var origin = new Vector3f(pivot);
		if (parentBone != null) origin.sub(parentBone.pivot);

		var part = parent.addOrReplaceChild(
			name,
			cubes,
			PartPose.offsetAndRotation(
				pivot.x(), pivot.y(), pivot.z(),
				rotation.x(), rotation.y(), rotation.z()
			)
		);

		for (int i = 0; i < deferred.size(); i++) {
			Cube cuboid = deferred.get(i);
			var builder = new CubeListBuilder();
			cuboid.bake(builder, pivot);

			part.addOrReplaceChild(
				"spectre:" + name + "_r" + i,
				builder,
				PartPose.offsetAndRotation(
					cuboid.pivot().x(), cuboid.pivot().y(), cuboid.pivot().z(),
					cuboid.rotation().x(), cuboid.rotation().y(), cuboid.rotation().z()
				)
			);
		}

		for (Bone child : children) {
			child.bake(part, this);
		}
	}
}
