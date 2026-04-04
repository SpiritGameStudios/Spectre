package dev.spiritstudios.spectre.test.models.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;

import static dev.spiritstudios.spectre.test.models.SpectreModelsTestmodInitializer.ENTITY_DISPLAY;
import static dev.spiritstudios.spectre.test.models.SpectreModelsTestmodInitializer.PUMPKIN;

public class SpectreModelsTestmodClientInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRenderers.register(ENTITY_DISPLAY, EntityDisplayRenderer::new);

		EntityRenderers.register(PUMPKIN, PumpkinEntityRenderer::new);
	}
}
