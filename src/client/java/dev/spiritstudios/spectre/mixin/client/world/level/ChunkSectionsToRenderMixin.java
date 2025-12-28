package dev.spiritstudios.spectre.mixin.client.world.level;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.spiritstudios.spectre.impl.client.SpectreClient;
import dev.spiritstudios.spectre.impl.client.world.level.render.CustomChunkSectionLayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ChunkSectionsToRender.class)
public class ChunkSectionsToRenderMixin {
	@Shadow
	@Final
	private GpuTextureView textureView;

	@Shadow
	@Final
	private GpuBufferSlice[] chunkSectionInfos;

	@Inject(method = "renderGroup", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;close()V"))
	private void renderCustomLayers(
		ChunkSectionLayerGroup chunkSectionLayerGroup, GpuSampler gpuSampler,
		CallbackInfo ci,
		@Local RenderPass pass,
		@Local GpuBuffer indexBuffer,
		@Local VertexFormat.IndexType indexType,
		@Local boolean debugWireframe
	) {
		Map<CustomChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>> customDraws = CustomChunkSectionLayer.CUSTOM_LAYER_DRAWS.get();

		for (CustomChunkSectionLayer layer : SpectreClient.CUSTOM_LAYERS) {
			List<RenderPass.Draw<GpuBufferSlice[]>> draws = customDraws.get(layer);
			if (!draws.isEmpty()) {
				// TODO: Support translucency maybe?
//				if (layer == ChunkSectionLayer.TRANSLUCENT) {
//					draws = draws.reversed();
//				}

				pass.setPipeline(debugWireframe ? RenderPipelines.WIREFRAME : layer.pipeline());
				pass.bindTexture("Sampler0", this.textureView, gpuSampler);
				pass.drawMultipleIndexed(draws, indexBuffer, indexType, List.of("ChunkSection"), this.chunkSectionInfos);
			}
		}
	}
}
