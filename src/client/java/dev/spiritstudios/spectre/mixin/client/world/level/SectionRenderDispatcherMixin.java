package dev.spiritstudios.spectre.mixin.client.world.level;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.MeshData;
import dev.spiritstudios.spectre.impl.client.world.level.render.CustomChunkSectionLayer;
import dev.spiritstudios.spectre.impl.client.world.level.render.SpectreCompiledSectionMesh;
import dev.spiritstudios.spectre.impl.client.world.level.render.SpectreRenderSection;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(SectionRenderDispatcher.class)
public class SectionRenderDispatcherMixin {
	@Mixin(SectionRenderDispatcher.RenderSection.class)
	public static class RenderSectionMixin implements SpectreRenderSection {
		@Shadow
		@Final
		SectionRenderDispatcher field_20833;

		@Shadow
		volatile long sectionNode;

		@Shadow
		private long uploadedTime;

		@Mixin(SectionRenderDispatcher.RenderSection.RebuildTask.class)
		public static class RebuildTaskMixin {
			@Shadow
			@Final
			SectionRenderDispatcher.RenderSection field_20839;

			@ModifyExpressionValue(method = "doTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection;upload(Ljava/util/Map;Lnet/minecraft/client/renderer/chunk/CompiledSectionMesh;)Ljava/util/concurrent/CompletableFuture;"))
			private CompletableFuture<Void> uploadCustomLayers(
				CompletableFuture<Void> original,
				@Local SectionCompiler.Results results,
				@Local CompiledSectionMesh mesh
			) {
				return CompletableFuture.allOf(
					original,
					field_20839.spectre$upload(results.spectre$renderedLayers(), mesh)
				);
			}
		}

		@Mixin(SectionRenderDispatcher.RenderSection.ResortTransparencyTask.class)
		public static class ResortTransparencyTaskMixin {

		}

		@Override
		public CompletableFuture<Void> spectre$upload(Map<CustomChunkSectionLayer, MeshData> renderedLayers, CompiledSectionMesh mesh) {
			if (field_20833.closed) {
				renderedLayers.values().forEach(MeshData::close);
				return CompletableFuture.completedFuture(null);
			} else {
				return CompletableFuture.runAsync(() -> renderedLayers.forEach((chunkSectionLayer, meshData) -> {
					Zone zone = Profiler.get().zone("Upload Section Layer");

					try {
						((SpectreCompiledSectionMesh) mesh).spectre$uploadMeshLayer(chunkSectionLayer, meshData, this.sectionNode);
						meshData.close();
					} catch (Throwable var8) {
						if (zone != null) {
							try {
								zone.close();
							} catch (Throwable var7) {
								var8.addSuppressed(var7);
							}
						}

						throw var8;
					}

					if (zone != null) {
						zone.close();
					}

					if (this.uploadedTime == 0L) {
						this.uploadedTime = Util.getMillis();
					}
				}), field_20833.mainThreadUploadExecutor);
			}
		}
	}
}
