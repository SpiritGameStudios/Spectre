package dev.spiritstudios.spectre.test;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.spiritstudios.spectre.api.client.model.EmptyEntityModel;
import dev.spiritstudios.spectre.api.client.model.SpectreModelRenderer;
import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.impl.Spectre;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.NotNull;

public class BlobaboRenderer extends LivingEntityRenderer<Blobabo, BlobaboEntityRenderState, EmptyEntityModel<BlobaboEntityRenderState>> {
	public BlobaboRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new EmptyEntityModel<>(), 1f);
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(BlobaboEntityRenderState state) {
		return Spectre.id("textures/entity/bloomray.png");
	}

	@Override
	public @NotNull BlobaboEntityRenderState createRenderState() {
		return new BlobaboEntityRenderState();
	}

	@Override
	public void extractRenderState(Blobabo entity, BlobaboEntityRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);

		state.query.set(entity, partialTick);
		state.movement.copyFrom(entity.movement);
		state.antenna.copyFrom(entity.antenna);
	}

	@Override
	public void submit(BlobaboEntityRenderState state, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraState) {
		poseStack.pushPose();

		poseStack.scale(state.scale, state.scale, state.scale);
		this.setupRotations(state, poseStack, state.bodyRot, state.scale);
		this.scale(state, poseStack);

		boolean bodyVisible = this.isBodyVisible(state);
		boolean translucent = !bodyVisible && !state.isInvisibleToPlayer;
		int overlay = getOverlayCoords(state, this.getWhiteOverlayProgress(state));

		SpectreModelRenderer.render(
			poseStack,
			nodeCollector,
			getRenderType(state, bodyVisible, translucent, state.appearsGlowing()),
			state.lightCoords,
			overlay,
			ARGB.multiply(translucent ? 0x26FFFFFF : CommonColors.WHITE, this.getModelTint(state)),
			state.query,
			state.ageInTicks,
			Spectre.id("bloomray/geometry.bloomray"),
			Spectre.id("bloomray"),
			state.movement, state.antenna
		);

		poseStack.popPose();

		super.submit(state, poseStack, nodeCollector, cameraState);
	}
}
