package dev.spiritstudios.spectre.impl.client.world.level.render;

import com.mojang.blaze3d.vertex.MeshData;

import java.util.Map;

public interface SpectreSectionCompilerResults {
	default Map<CustomChunkSectionLayer, MeshData> spectre$renderedLayers() {
		throw new IllegalStateException("Implemented via mixin");
	}

}
