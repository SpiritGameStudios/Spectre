package dev.spiritstudios.spectre.impl.client.world.level.render;

import com.mojang.blaze3d.vertex.MeshData;
import dev.spiritstudios.spectre.api.client.world.level.render.CustomChunkSectionLayer;

import net.minecraft.client.renderer.chunk.CompiledSectionMesh;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface SpectreRenderSection {
	default CompletableFuture<Void> spectre$upload(Map<CustomChunkSectionLayer, MeshData> renderedLayers, CompiledSectionMesh mesh) {
		throw new IllegalStateException("Implemented via mixin");
	}
}
