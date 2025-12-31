package dev.spiritstudios.spectre.api.client;

import dev.spiritstudios.spectre.api.client.world.level.render.CustomChunkSectionLayer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class SpectreChunkSectionLayers {
	public static final Event<GetChunkLayerCallback> CHUNK_LAYER_CALLBACK = EventFactory.createArrayBacked(
		GetChunkLayerCallback.class,
		callbacks -> state -> {
			for (GetChunkLayerCallback callback : callbacks) {
				CustomChunkSectionLayer layer = callback.getChunkSectionLayer(state);
				if (layer != null) return layer;
			}

			return null;
		}
	);

	@FunctionalInterface
	public interface GetChunkLayerCallback {
		@Nullable CustomChunkSectionLayer getChunkSectionLayer(BlockState state);
	}
}
