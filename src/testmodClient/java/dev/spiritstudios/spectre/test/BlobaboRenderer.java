package dev.spiritstudios.spectre.test;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.spiritstudios.spectre.api.client.model.animation.AnimationLocation;
import dev.spiritstudios.spectre.api.client.model.animation.SpectreKeyframeAnimation;
import dev.spiritstudios.spectre.impl.Spectre;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BlobaboRenderer extends LivingEntityRenderer<Blobabo, BlobaboEntityRenderState, EntityModel<BlobaboEntityRenderState>> {
	public BlobaboRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new Model(
				ctx,
				new ModelLayerLocation(Spectre.id("bloomray"), "main"),
				Spectre.id("bloomray"),
				"swim", "idle",
				"crown_sway", "crown_wiggle"
			),
			1f
		);
	}

	@Override
	public @NotNull Identifier getTextureLocation(BlobaboEntityRenderState state) {
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
	protected void setupRotations(BlobaboEntityRenderState renderState, PoseStack poseStack, float bodyRot, float scale) {
		super.setupRotations(renderState, poseStack, bodyRot, scale);
		poseStack.mulPose(Axis.XN.rotationDegrees(renderState.xRot));
	}

	public static class Model extends EntityModel<BlobaboEntityRenderState> {
		private final Map<String, SpectreKeyframeAnimation> bakedAnimations = new HashMap<>();

		private final SpectreKeyframeAnimation swim;

		protected Model(
			EntityRendererProvider.Context context,
			ModelLayerLocation layerLocation,
			Identifier animationSet,
			String... animations
		) {
			super(context.bakeLayer(layerLocation));

			for (String animation : animations) {
				bakedAnimations.put(
					animation,
					context.bakeAnimation(new AnimationLocation(animationSet, animation), root())
				);
			}

			this.swim = context.bakeAnimation(new AnimationLocation(animationSet, "swim"), root());
		}

		@Override
		public void setupAnim(BlobaboEntityRenderState renderState) {
			super.setupAnim(renderState);

			var query = renderState.query;

			swim.applyWalk(query, renderState.walkAnimationPos, renderState.walkAnimationSpeed, 1F, 1F);
			renderState.antenna.apply(bakedAnimations, query, renderState.ageInTicks);
		}
	}
}
