package dev.spiritstudios.spectre.api.client.world.level.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import dev.spiritstudios.spectre.impl.client.SpectreClient;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;

import java.util.List;
import java.util.Map;

public record CustomChunkSectionLayer(RenderPipeline pipeline, int bufferSize, boolean sortOnUpload,
									  ChunkSectionLayerGroup group, String label) {
	public CustomChunkSectionLayer(RenderPipeline pipeline, int bufferSize, boolean sortOnUpload, ChunkSectionLayerGroup group, String label) {
		this.pipeline = pipeline;
		this.bufferSize = bufferSize;
		this.sortOnUpload = sortOnUpload;
		this.group = group;
		this.label = label;

		SpectreClient.CUSTOM_LAYERS.add(this);
	}

	public static final ThreadLocal<Map<CustomChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>>> CUSTOM_LAYER_DRAWS = new ThreadLocal<>();
}
