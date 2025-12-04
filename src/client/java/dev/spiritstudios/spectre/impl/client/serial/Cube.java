package dev.spiritstudios.spectre.impl.client.serial;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.api.client.model.ModelCodecs;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import org.apache.commons.lang3.NotImplementedException;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Map;

public record Cube(
	Vector3fc origin,
	Vector3fc size,
	Vector3fc rotation,
	Vector3fc pivot,
	float inflate,
	boolean mirror,
	Either<Vector2fc, Map<Direction, Face>> uv
) {
	public static final Codec<Cube> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ExtraCodecs.VECTOR3F.optionalFieldOf("origin", new Vector3f(0F)).forGetter(Cube::origin),
		ExtraCodecs.VECTOR3F.optionalFieldOf("size", new Vector3f(1F)).forGetter(Cube::size),
		ModelCodecs.ROTATION_VECTOR.optionalFieldOf("rotation", new Vector3f(0F)).forGetter(Cube::rotation),
		ExtraCodecs.VECTOR3F.<Vector3fc>xmap(
			vec -> vec.mul(-1F, 1F, 1F, new Vector3f()),
			vec -> vec.mul(-1F, 1F, 1F, new Vector3f())
		).optionalFieldOf("pivot", new Vector3f(0F)).forGetter(Cube::pivot),
		Codec.FLOAT.optionalFieldOf("inflate", 1F).forGetter(Cube::inflate),
		Codec.BOOL.optionalFieldOf("mirror", false).forGetter(Cube::mirror),
		Codec.either(
			ExtraCodecs.VECTOR2F,
			Codec.unboundedMap(Direction.CODEC, Face.CODEC)
		).fieldOf("uv").forGetter(Cube::uv)
	).apply(instance, Cube::new));

	public void bake(CubeListBuilder builder, Vector3fc boneOrigin) {
		var pos = new Vector3f(
			-(origin.x() + size.x()) - boneOrigin.x(),
			origin.y() - boneOrigin.y(),
			origin.z() - boneOrigin.z()
		);

		uv
			.ifLeft(offsets -> builder.texOffs((int) offsets.x(), (int) offsets.y()))
			.ifRight(faces -> {
				throw new NotImplementedException();
			});

		builder.mirror(mirror).addBox(
			pos.x, pos.y, pos.z,
			size.x(), size.y(), size.z(),
			new CubeDeformation(inflate() - 1F),
			1.0F, 1.0F
			);
	}

//	public SpectreCuboid bake(ModelBone bone, float textureWidth, float textureHeight) {
//		var inflate = this.inflate != 0F ?
//			this.inflate :
//			bone.inflate();
//
//		inflate /= 16F;
//
//		var min = new Vector3f(
//			-(origin.x + size.x),
//			origin.y,
//			origin.z
//		);
//
//		var max = min.add(size, new Vector3f());
//
//		var pivot = this.pivot.mul(-1F, 1F, 1F, new Vector3f());
//
//		PoseStack matrices = new PoseStack();
//		matrices.pushPose();
//
//		matrices.rotateAround(
//			new Quaternionf()
//				.rotateZYX(
//					this.rotation.z * Mth.DEG_TO_RAD,
//					-this.rotation.y * Mth.DEG_TO_RAD,
//					-this.rotation.x * Mth.DEG_TO_RAD
//				),
//			pivot.x, pivot.y, pivot.z
//		);
//
//
//		var transform = matrices.last();
//
//		Vector3f[] corners = {
//			vertex(0, min, max, transform.pose()),
//			vertex(1, min, max, transform.pose()),
//			vertex(2, min, max, transform.pose()),
//			vertex(3, min, max, transform.pose()),
//			vertex(4, min, max, transform.pose()),
//			vertex(5, min, max, transform.pose()),
//			vertex(6, min, max, transform.pose()),
//			vertex(7, min, max, transform.pose()),
//		};
//
//		SpectreCuboid.Quad[] quads = new SpectreCuboid.Quad[6];
//
//		Map<Direction, Face> faces = uv.map(
//			// I love box uv
//			uv -> {
//				var uvSize = size.mul(16F, new Vector3f());
//
//				return Map.of(
//					Direction.DOWN, new Face(
//						uv.x + uvSize.x + uvSize.z, uv.y + uvSize.z,
//						uvSize.x, -uvSize.z
//					),
//
//					Direction.UP, new Face(
//						uv.x + uvSize.z, uv.y,
//						uvSize.x, uvSize.z
//					),
//
//					Direction.NORTH, new Face(
//						uv.x + uvSize.x + uvSize.z, uv.y + uvSize.y + uvSize.z,
//						-uvSize.x, -uvSize.y
//					),
//
//					Direction.SOUTH, new Face(
//						uv.x + uvSize.z + uvSize.x + uvSize.x + uvSize.z, uv.y + uvSize.y + uvSize.z,
//						-uvSize.x, -uvSize.y
//					),
//
//					Direction.WEST, new Face(
//						uv.x + uvSize.z + uvSize.z + uvSize.x, uv.y + uvSize.y + uvSize.z,
//						-uvSize.z, -uvSize.y
//					),
//
//					Direction.EAST, new Face(
//						uv.x + uvSize.z, uv.y + uvSize.y + uvSize.z,
//						-uvSize.z, -uvSize.y
//					)
//				);
//			},
//			Function.identity()
//		);
//
//		faces.forEach((dir, face) -> {
//			var uMax = face.uv().x / textureWidth;
//			var vMax = face.uv().y / textureHeight;
//			var uMin = (face.uv().x + face.uvSize().x) / textureWidth;
//			var vMin = (face.uv().y + face.uvSize().y) / textureHeight;
//
//			int rotationIndex = face.uvRotation();
//			int[] ix = INDICES[dir.get3DDataValue()];
//			SpectreCuboid.Vertex[] vertices = {
//				new SpectreCuboid.Vertex(corners[ix[(rotationIndex) % 4]], uMin, vMin),
//				new SpectreCuboid.Vertex(corners[ix[(1 + rotationIndex) % 4]], uMax, vMin),
//				new SpectreCuboid.Vertex(corners[ix[(2 + rotationIndex) % 4]], uMax, vMax),
//				new SpectreCuboid.Vertex(corners[ix[(3 + rotationIndex) % 4]], uMin, vMax),
//			};
//			Vector3f normal = transform.transformNormal(dir.getUnitVec3f(), new Vector3f()).negate();
//			quads[dir.get3DDataValue()] = new SpectreCuboid.Quad(vertices, normal, dir);
//		});
//
//		return new SpectreCuboid(
//			quads,
//			pivot,
//			size,
//			inflate,
//			mirror
//		);
//	}
}
