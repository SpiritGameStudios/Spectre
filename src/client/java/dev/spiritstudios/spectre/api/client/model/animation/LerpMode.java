package dev.spiritstudios.spectre.api.client.model.animation;

import com.mojang.serialization.Codec;
import dev.spiritstudios.spectre.api.core.math.Query;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public enum LerpMode implements StringRepresentable {
	LINEAR("linear") {
		@Override
		public Vector3f apply(Query query, float delta, SpectreKeyframe[] keyframes, int start, int end, float scale) {
			var vector3fc = keyframes[start].to().evaluate(query, null);
			var vector3fc2 = keyframes[end].from().evaluate(query, null);
			return vector3fc.lerp(vector3fc2, delta).mul(scale);
		}
	},
	CATMULL_ROM("catmullrom") {
		@Override
		public Vector3f apply(Query query, float delta, SpectreKeyframe[] keyframes, int start, int end, float scale) {
			Vector3fc p0 = keyframes[Math.max(0, start - 1)].to().evaluate(query, null);
			Vector3fc p1 = keyframes[start].to().evaluate(query, null);
			Vector3fc p2 = keyframes[end].to().evaluate(query, null);
			Vector3fc p3 = keyframes[Math.min(keyframes.length - 1, end + 1)].to().evaluate(query, null);

			return new Vector3f(
				Mth.catmullrom(delta, p0.x(), p1.x(), p2.x(), p3.x()) * scale,
				Mth.catmullrom(delta, p0.y(), p1.y(), p2.y(), p3.y()) * scale,
				Mth.catmullrom(delta, p0.z(), p1.z(), p2.z(), p3.z()) * scale
			);
		}
	};

	private final String name;

	LerpMode(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public abstract Vector3f apply(Query query, float delta, SpectreKeyframe[] keyframes, int start, int end, float scale);

	public static final Codec<LerpMode> CODEC = StringRepresentable.fromEnum(LerpMode::values);
}
