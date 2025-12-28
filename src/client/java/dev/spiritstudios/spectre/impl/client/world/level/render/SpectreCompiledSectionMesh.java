package dev.spiritstudios.spectre.impl.client.world.level.render;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.renderer.chunk.SectionBuffers;
import org.jspecify.annotations.Nullable;

public interface SpectreCompiledSectionMesh {
	default void spectre$uploadMeshLayer(CustomChunkSectionLayer layer, MeshData meshData, long sectionNode) {
		throw new IllegalStateException("Implemented via mixin");
	}

	default void spectre$uploadLayerIndexBuffer(CustomChunkSectionLayer layer, ByteBufferBuilder.Result result, long sectionNode) {
		throw new IllegalStateException("Implemented via mixin");
	}

	default @Nullable SectionBuffers spectre$getBuffers(CustomChunkSectionLayer layer) {
		throw new IllegalStateException("Implemented via mixin");
	}
}
