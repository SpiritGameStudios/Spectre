package dev.spiritstudios.spectre.mixin.client.world.level;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.spiritstudios.spectre.impl.client.SpectreClient;
import dev.spiritstudios.spectre.impl.client.world.level.render.CustomChunkSectionLayer;
import dev.spiritstudios.spectre.impl.client.world.level.render.SpectreCompiledSectionMesh;
import net.minecraft.client.renderer.DynamicUniforms;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.chunk.SectionBuffers;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static dev.spiritstudios.spectre.impl.client.world.level.render.CustomChunkSectionLayer.CUSTOM_LAYER_DRAWS;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Inject(method = "prepareChunkRenders", at = @At(value = "NEW", target = "(Ljava/lang/Class;)Ljava/util/EnumMap;"))
	private void addCustomMap(
		Matrix4fc frustumMatrix, double x, double y, double z, CallbackInfoReturnable<ChunkSectionsToRender> cir,
		@Share("customLayerDraws") LocalRef<Map<CustomChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>>> customLayerDrawsRef
	) {
		Map<CustomChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>> customLayerDraws = new IdentityHashMap<>();
		customLayerDrawsRef.set(customLayerDraws);

		for (CustomChunkSectionLayer layer : SpectreClient.CUSTOM_LAYERS) {
			customLayerDraws.put(layer, new ArrayList<>());
		}
	}

	// TODO: Less brittle mixin
	@Inject(method = "prepareChunkRenders", at = @At(value = "JUMP", opcode = Opcodes.GOTO, ordinal = 4))
	private void cursedAsf(
		Matrix4fc frustumMatrix, double x, double y, double z, CallbackInfoReturnable<ChunkSectionsToRender> cir,
		@Local SectionRenderDispatcher.RenderSection renderSection,
		@Local(ordinal = 0) LocalIntRef maxIndexCount,
		@Local(ordinal = 1) int blockAtlasWidth,
		@Local(ordinal = 2) int blockAtlasHeight,
		@Local(ordinal = 3) LocalIntRef sectionInfosCount,
		@Local SectionMesh sectionMesh,
		@Local BlockPos blockPos,
		@Local List<DynamicUniforms.ChunkSectionInfo> sectionInfos,
		@Local long measuringTime,
		@Share("customLayerDraws") LocalRef<Map<CustomChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>>> customLayerDrawsRef
	) {
		Map<CustomChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>> customLayerDraws = customLayerDrawsRef.get();

		for (CustomChunkSectionLayer layer : SpectreClient.CUSTOM_LAYERS) {
			SectionBuffers sectionBuffers = sectionMesh instanceof SpectreCompiledSectionMesh spectreMesh ?
				spectreMesh.spectre$getBuffers(layer) :
				null;

			if (sectionBuffers != null) {
				if (sectionInfosCount.get() == -1) {
					sectionInfosCount.set(sectionInfos.size());
					sectionInfos.add(
						new DynamicUniforms.ChunkSectionInfo(
							new Matrix4f(frustumMatrix), blockPos.getX(), blockPos.getY(), blockPos.getZ(), renderSection.getVisibility(measuringTime), blockAtlasWidth, blockAtlasHeight
						)
					);
				}

				GpuBuffer indexBuffer;
				VertexFormat.IndexType indexType;
				if (sectionBuffers.getIndexBuffer() == null) {
					if (sectionBuffers.getIndexCount() > maxIndexCount.get()) {
						maxIndexCount.set(sectionBuffers.getIndexCount());
					}

					indexBuffer = null;
					indexType = null;
				} else {
					indexBuffer = sectionBuffers.getIndexBuffer();
					indexType = sectionBuffers.getIndexType();
				}

				int n = sectionInfosCount.get();
				customLayerDraws.get(layer).add(
					new RenderPass.Draw<>(
						0,
						sectionBuffers.getVertexBuffer(),
						indexBuffer,
						indexType,
						0,
						sectionBuffers.getIndexCount(),
						(gpuBufferSlicesx, uniformUploader) -> uniformUploader.upload("ChunkSection", gpuBufferSlicesx[n])
					)
				);
			}
		}
	}

	@Inject(method = "prepareChunkRenders", at = @At("RETURN"))
	private void addCustomLayerDraws(
		Matrix4fc frustumMatrix, double x, double y, double z,
		CallbackInfoReturnable<ChunkSectionsToRender> cir,
		@Share("customLayerDraws") LocalRef<Map<CustomChunkSectionLayer, List<RenderPass.Draw<GpuBufferSlice[]>>>> customLayerDrawsRef

	) {
		CUSTOM_LAYER_DRAWS.set(customLayerDrawsRef.get());
	}
}
