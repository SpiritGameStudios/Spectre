package dev.spiritstudios.spectre.mixin.client.world.level;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import dev.spiritstudios.spectre.impl.client.world.level.render.CustomChunkSectionLayer;
import dev.spiritstudios.spectre.impl.client.world.level.render.SpectreCompiledSectionMesh;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionBuffers;
import net.minecraft.core.SectionPos;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(CompiledSectionMesh.class)
public class CompiledSectionMeshMixin implements SpectreCompiledSectionMesh {
	@Unique
	private final Map<CustomChunkSectionLayer, SectionBuffers> customBuffers = new IdentityHashMap<>();

	@ModifyReturnValue(method = "hasRenderableLayers", at = @At("RETURN"))
	private boolean hasRenderableCustomBuffers(boolean original) {
		return original || !customBuffers.isEmpty();
	}

	@ModifyReturnValue(method = "hasRenderableLayers", at = @At("RETURN"))
	private boolean includeCustomBuffers(boolean original) {
		return original || !customBuffers.isEmpty();
	}

	@Inject(method = "close", at = @At("RETURN"))
	private void closeCustomBuffers(CallbackInfo ci) {
		this.customBuffers.values().forEach(SectionBuffers::close);
		this.customBuffers.clear();
	}

	@Override
	public @Nullable SectionBuffers spectre$getBuffers(CustomChunkSectionLayer layer) {
		return customBuffers.get(layer);
	}

	@Override
	public void spectre$uploadMeshLayer(CustomChunkSectionLayer layer, MeshData meshData, long sectionNode) {
		CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
		SectionBuffers sectionBuffers = this.spectre$getBuffers(layer);

		if (sectionBuffers != null) {
			if (sectionBuffers.getVertexBuffer().size() < meshData.vertexBuffer().remaining()) {
				sectionBuffers.getVertexBuffer().close();

				sectionBuffers.setVertexBuffer(
					RenderSystem.getDevice()
						.createBuffer(
							() -> "Spectre Section vertex buffer - layer: "
								+ layer.label()
								+ "; cords: "
								+ SectionPos.x(sectionNode)
								+ ", "
								+ SectionPos.y(sectionNode)
								+ ", "
								+ SectionPos.z(sectionNode),
							GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_VERTEX,
							meshData.vertexBuffer()
						)
				);
			} else if (!sectionBuffers.getVertexBuffer().isClosed()) {
				commandEncoder.writeToBuffer(sectionBuffers.getVertexBuffer().slice(), meshData.vertexBuffer());
			}

			ByteBuffer indexBuffer = meshData.indexBuffer();
			if (indexBuffer != null) {
				if (sectionBuffers.getIndexBuffer() != null && sectionBuffers.getIndexBuffer().size() >= indexBuffer.remaining()) {
					if (!sectionBuffers.getIndexBuffer().isClosed()) {
						commandEncoder.writeToBuffer(sectionBuffers.getIndexBuffer().slice(), indexBuffer);
					}
				} else {
					if (sectionBuffers.getIndexBuffer() != null) {
						sectionBuffers.getIndexBuffer().close();
					}

					sectionBuffers.setIndexBuffer(
						RenderSystem.getDevice()
							.createBuffer(
								() -> "Spectre Section index buffer - layer: "
									+ layer.label()
									+ "; cords: "
									+ SectionPos.x(sectionNode)
									+ ", "
									+ SectionPos.y(sectionNode)
									+ ", "
									+ SectionPos.z(sectionNode),
								GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_INDEX,
								indexBuffer
							)
					);
				}
			} else if (sectionBuffers.getIndexBuffer() != null) {
				sectionBuffers.getIndexBuffer().close();
				sectionBuffers.setIndexBuffer(null);
			}

			sectionBuffers.setIndexCount(meshData.drawState().indexCount());
			sectionBuffers.setIndexType(meshData.drawState().indexType());
		} else {
			GpuBuffer vertexBuffer = RenderSystem.getDevice()
				.createBuffer(
					() -> "Spectre Section vertex buffer - layer: "
						+ layer.label()
						+ "; cords: "
						+ SectionPos.x(sectionNode)
						+ ", "
						+ SectionPos.y(sectionNode)
						+ ", "
						+ SectionPos.z(sectionNode),
					GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_VERTEX,
					meshData.vertexBuffer()
				);

			ByteBuffer indexByteBuffer = meshData.indexBuffer();
			GpuBuffer indexBuffer = indexByteBuffer != null
				? RenderSystem.getDevice()
				.createBuffer(
					() -> "Section index buffer - layer: "
						+ layer.label()
						+ "; cords: "
						+ SectionPos.x(sectionNode)
						+ ", "
						+ SectionPos.y(sectionNode)
						+ ", "
						+ SectionPos.z(sectionNode),
					GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_INDEX,
					indexByteBuffer
				)
				: null;

			sectionBuffers = new SectionBuffers(vertexBuffer, indexBuffer, meshData.drawState().indexCount(), meshData.drawState().indexType());
			this.customBuffers.put(layer, sectionBuffers);
		}

	}

	@Override
	public void spectre$uploadLayerIndexBuffer(CustomChunkSectionLayer layer, ByteBufferBuilder.Result result, long sectionNode) {
		SectionBuffers sectionBuffers = this.spectre$getBuffers(layer);

		if (sectionBuffers != null) {
			if (sectionBuffers.getIndexBuffer() == null) {
				sectionBuffers.setIndexBuffer(
					RenderSystem.getDevice()
						.createBuffer(
							() -> "Spectre Section index buffer - layer: "
								+ layer.label()
								+ "; cords: "
								+ SectionPos.x(sectionNode)
								+ ", "
								+ SectionPos.y(sectionNode)
								+ ", "
								+ SectionPos.z(sectionNode),
							GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_INDEX,
							result.byteBuffer()
						)
				);
			} else {
				CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
				if (!sectionBuffers.getIndexBuffer().isClosed()) {
					commandEncoder.writeToBuffer(sectionBuffers.getIndexBuffer().slice(), result.byteBuffer());
				}
			}
		}
	}
}
