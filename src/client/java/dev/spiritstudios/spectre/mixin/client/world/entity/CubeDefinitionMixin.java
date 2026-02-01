package dev.spiritstudios.spectre.mixin.client.world.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.spiritstudios.spectre.impl.client.pond.SpectreCubeDef;
import dev.spiritstudios.spectre.impl.client.serial.Face;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(CubeDefinition.class)
public class CubeDefinitionMixin implements SpectreCubeDef {
	@Unique
	private @Nullable Map<Direction, Face> faceUV;

	@Override
	public void spectre$setFaceUv(Map<Direction, Face> faceUV) {
		this.faceUV = faceUV;
	}

	@WrapMethod(method = "bake")
	private ModelPart.Cube addThreadLocalFaceUV(int texWidth, int texHeight, Operation<ModelPart.Cube> original) {
		SpectreCubeDef.FACE_UV.set(faceUV);
		ModelPart.Cube result = original.call(texWidth, texHeight);
		SpectreCubeDef.FACE_UV.remove();

		return result;
	}
}
