package dev.spiritstudios.spectre.test;

import dev.spiritstudios.spectre.impl.Spectre;
import dev.spiritstudios.spectre.impl.client.BloomrayModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class SpectreTestmodClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(
			new ModelLayerLocation(
				Spectre.id("bloomray"),
				"bb"
			),
			BloomrayModel::createBodyLayer
		);

		EntityRenderers.register(
			SpectreTestmod.BLOBABO,
			BlobaboRenderer::new
		);

		EntityRenderers.register(
			SpectreTestmod.ENTITY_DISPLAY,
			EntityDisplayRenderer::new
		);
	}
}
