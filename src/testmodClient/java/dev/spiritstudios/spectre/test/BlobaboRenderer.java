package dev.spiritstudios.spectre.test;

import dev.spiritstudios.spectre.impl.Spectre;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class BlobaboRenderer extends LivingEntityRenderer<Blobabo, BlobaboEntityRenderState, EntityModel<BlobaboEntityRenderState>> {
	public BlobaboRenderer(EntityRendererProvider.Context ctx) {
		super(
			ctx,
			new Model(ctx.bakeLayer(new ModelLayerLocation(Spectre.id("bloomray"), "default"))),
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

	public static class Model extends EntityModel<BlobaboEntityRenderState> {
		protected Model(ModelPart root) {
			super(root);
		}
	}
}
