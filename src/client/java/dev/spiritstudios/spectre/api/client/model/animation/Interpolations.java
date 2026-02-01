package dev.spiritstudios.spectre.api.client.model.animation;

import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Vector3fc;

public final class Interpolations {
	public static final Interpolation LINEAR = (query, delta, keyframes, start, end, scale, destination) -> {
		var vector3fc = keyframes[start].postTarget().evaluate(query, null);
		var vector3fc2 = keyframes[end].preTarget().evaluate(query, null);
		vector3fc.lerp(vector3fc2, delta, destination).mul(scale);
	};

	public static final Interpolation CATMULL_ROM = (query, delta, keyframes, start, end, scale, destination) -> {
		Vector3fc p0 = keyframes[Math.max(0, start - 1)].postTarget().evaluate(query, null);
		Vector3fc p1 = keyframes[start].postTarget().evaluate(query, null);
		Vector3fc p2 = keyframes[end].postTarget().evaluate(query, null);
		Vector3fc p3 = keyframes[Math.min(keyframes.length - 1, end + 1)].postTarget().evaluate(query, null);

		destination.set(
			Mth.catmullrom(delta, p0.x(), p1.x(), p2.x(), p3.x()) * scale,
			Mth.catmullrom(delta, p0.y(), p1.y(), p2.y(), p3.y()) * scale,
			Mth.catmullrom(delta, p0.z(), p1.z(), p2.z(), p3.z()) * scale
		);
	};

	static {
		Interpolation.ID_MAPPER.put(Identifier.withDefaultNamespace("linear"), LINEAR);
		Interpolation.ID_MAPPER.put(Identifier.withDefaultNamespace("catmullrom"), CATMULL_ROM);
	}
}
