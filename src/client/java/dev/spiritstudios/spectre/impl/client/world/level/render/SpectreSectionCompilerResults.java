package dev.spiritstudios.spectre.impl.client.world.level.render;

import com.mojang.blaze3d.vertex.MeshData;
import dev.spiritstudios.spectre.api.client.world.level.render.CustomChunkSectionLayer;

import java.util.Map;

public interface SpectreSectionCompilerResults {
	default Map<CustomChunkSectionLayer, MeshData> spectre$renderedLayers() {
		throw new IllegalStateException("Implemented via mixin");
	}

}
