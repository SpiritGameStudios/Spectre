package dev.spiritstudios.spectre.mixin.client.screenshake;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.spiritstudios.spectre.api.client.SpectreScreenshake;
import dev.spiritstudios.spectre.api.core.math.SimplexNoise1D;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	@Final
	private RandomSource random;

	@Shadow
	@Final
	private Minecraft minecraft;
	@Unique
	private SimplexNoise1D noise;


	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(Minecraft client, ItemInHandRenderer firstPersonHeldItemRenderer, RenderBuffers buffers, BlockRenderDispatcher blockRenderManager, CallbackInfo ci) {
		noise = new SimplexNoise1D(random);
	}

	@ModifyExpressionValue(method = "renderLevel", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
	private PoseStack applyScreenshake(PoseStack original, @Local(argsOnly = true) DeltaTracker renderTickCounter) {
		assert minecraft.level != null;

		float time = minecraft.level.getGameTime() + renderTickCounter.getGameTimeDeltaPartialTick(true);

		// FIXME: This will need to be extracted whenever the thread split happens
		float shake = SpectreScreenshake.trauma * SpectreScreenshake.trauma;

		float yaw = (45F * noise.sample(time)) * shake;
		float pitch = 45F * noise.sample(time + 1000F) * shake;
		float roll = 45F * noise.sample(time + 2000F) * shake;

		original.mulPose(new Quaternionf().rotateYXZ(
			yaw * Mth.DEG_TO_RAD,
			pitch * Mth.DEG_TO_RAD,
			roll * Mth.DEG_TO_RAD
		));

		return original;
	}
}
