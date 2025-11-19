package dev.spiritstudios.spectre.test;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.spiritstudios.spectre.api.client.model.Bone;
import dev.spiritstudios.spectre.api.client.model.BoneState;
import dev.spiritstudios.spectre.api.client.model.SpectreModelRenderer;
import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.impl.Spectre;
import dev.spiritstudios.spectre.impl.client.SpectreClient;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.function.BiConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.ResourceLocation;

public class BlobaboRenderer extends LivingEntityRenderer<Blobabo, BlobaboEntityRenderState, BlobaboRenderer.Model> {
	public BlobaboRenderer(EntityRendererProvider.Context ctx, float shadowRadius) {
		super(ctx, new Model(), shadowRadius);
		this.addLayer(new RenderLayer<>(this) {
			@Override
			public void submit(PoseStack matrices, SubmitNodeCollector queue, int light, BlobaboEntityRenderState state, float limbAngle, float limbDistance) {
				matrices.pushPose();
				matrices.scale(-1F, -1F, 1f);
				matrices.translate(0F, -1.501F, 0F);

				var animations = SpectreClient.ANIMATION_MANAGER.animations
					.get(Spectre.id("bloomray"));

				var model = SpectreClient.MODEL_MANAGER.models.get(
					Spectre.id("bloomray")
				).get("geometry.bloomray");

				Query query = new Query();

				BiConsumer<Bone, BoneState> animationApplicator = (bone, boneState) -> {
					state.movement.apply(
						bone,
						boneState,
						animations,
						query,
						state.ageInTicks
					);

					state.antenna.apply(
						bone,
						boneState,
						animations,
						query,
						state.ageInTicks
					);
				};

				var boneState = new BoneState(
					new Vector3f(),
					new Vector3f(),
					new Vector3f(),
					new Vector3f()
				);

				for (Bone bone : model.rootBones()) {
					SpectreModelRenderer.render(
						matrices,
						queue,
						RenderType.entityCutoutNoCull(getTextureLocation(state)),
						OverlayTexture.NO_OVERLAY,
						light,
						bone,
						boneState,
						animationApplicator
					);
				}

				matrices.popPose();
			}
		});
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
	public void extractRenderState(Blobabo entity, BlobaboEntityRenderState state, float tickProgress) {
		super.extractRenderState(entity, state, tickProgress);

		state.movement.copyFrom(entity.movement);
		state.antenna.copyFrom(entity.antenna);
	}

	@Override
	public void submit(BlobaboEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
		super.submit(state, matrices, queue, cameraState);
	}

	public static class Model extends EntityModel<BlobaboEntityRenderState> {
		protected Model() {
			super(new ModelPart(Collections.emptyList(), Collections.emptyMap()));
		}
	}
}
