package dev.spiritstudios.spectre.test;

import dev.spiritstudios.spectre.api.client.SpectreChunkSectionLayers;
import dev.spiritstudios.spectre.api.client.world.level.render.CustomChunkSectionLayer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class SpectreTestmodClient implements ClientModInitializer {
	public static final CustomChunkSectionLayer TEST_LAYER = new CustomChunkSectionLayer(
		RenderPipelines.WIREFRAME,
		1 << 16, // 2^16
		false,
		ChunkSectionLayerGroup.OPAQUE,
		"Test Layer"
	);

	@Override
	public void onInitializeClient() {
		EntityRenderers.register(
			SpectreTestmod.BLOBABO,
			BlobaboRenderer::new
		);

		EntityRenderers.register(
			SpectreTestmod.ENTITY_DISPLAY,
			EntityDisplayRenderer::new
		);

		SpectreChunkSectionLayers.CHUNK_LAYER_CALLBACK.register(state -> TEST_LAYER);
	}
}
