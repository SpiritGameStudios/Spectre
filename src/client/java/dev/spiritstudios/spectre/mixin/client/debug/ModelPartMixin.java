package dev.spiritstudios.spectre.mixin.client.debug;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.spiritstudios.spectre.impl.client.ModelPartDebugRenderer;
import dev.spiritstudios.spectre.impl.client.SpectreModelPart;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.class)
public class ModelPartMixin implements SpectreModelPart {
	@Unique
	private String name = "Unset";

	@Inject(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart$Cube;compile(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"))
	private void debug(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, int color, CallbackInfo ci, @Local ModelPart.Cube cube) {
		ModelPartDebugRenderer.debugCube((ModelPart) (Object) this, pose, cube);
	}

	@Override
	public String spectre$getName() {
		return name;
	}

	@Override
	public void spectre$setName(String name) {
		this.name = name;
	}
}
