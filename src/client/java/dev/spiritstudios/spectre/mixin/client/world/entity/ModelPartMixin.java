package dev.spiritstudios.spectre.mixin.client.world.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.spiritstudios.spectre.impl.client.ModelPartDebugRenderer;
import dev.spiritstudios.spectre.impl.client.pond.SpectreCubeDef;
import dev.spiritstudios.spectre.impl.client.pond.SpectreModelPart;
import dev.spiritstudios.spectre.impl.client.serial.Face;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelPart.class)
public class ModelPartMixin {
	@Inject(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart$Cube;compile(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
	private void debug(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, int color, CallbackInfo ci, @Local ModelPart.Cube cube) {
		ModelPartDebugRenderer.debugCube((ModelPart) (Object) this, pose, cube);
	}

	@Mixin(ModelPart.Cube.class)
	public static class CubeMixin {
		@WrapOperation(method = "<init>", at = @At(value = "NEW", target = "([Lnet/minecraft/client/model/geom/ModelPart$Vertex;FFFFFFZLnet/minecraft/core/Direction;)Lnet/minecraft/client/model/geom/ModelPart$Polygon;"))
		private ModelPart.Polygon addFaceUVs(ModelPart.Vertex[] vertices, float u1, float v1, float u2, float v2, float textureWidth, float textureHeight, boolean mirror, Direction direction, Operation<ModelPart.Polygon> original) {
			Map<Direction, Face> faceUV = SpectreCubeDef.FACE_UV.get();
			if (faceUV == null) return original.call(vertices, u1, v1, u2, v2, textureWidth, textureHeight, mirror, direction);

			Face face = faceUV.get(direction);
			if (face == null) return original.call(vertices, u1, v1, u2, v2, textureWidth, textureHeight, mirror, direction);

			if (face.uvRotation() != 0) {
				throw new NotImplementedException("Cannot rotate faces yet!");
			}

			u1 = face.uv().x();
			v1 = face.uv().y();

			u2 = face.uv().x() + face.uvSize().x();
			v2 = face.uv().y() + face.uvSize().y();

			return original.call(vertices, u1, v1, u2, v2, textureWidth, textureHeight, mirror, direction);
		}
	}
}
