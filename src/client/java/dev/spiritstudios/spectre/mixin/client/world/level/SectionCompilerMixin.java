package dev.spiritstudios.spectre.mixin.client.world.level;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.*;
import dev.spiritstudios.spectre.api.client.SpectreChunkSectionLayers;
import dev.spiritstudios.spectre.impl.client.SpectreClient;
import dev.spiritstudios.spectre.impl.client.world.level.render.CustomChunkSectionLayer;
import dev.spiritstudios.spectre.impl.client.world.level.render.SpectreSectionCompilerResults;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = SectionCompiler.class, priority = -1)
public class SectionCompilerMixin {
	@Shadow
	@Final
	private BlockRenderDispatcher blockRenderer;

	@Inject(method = "compile", at = @At(value = "NEW", target = "(Ljava/lang/Class;)Ljava/util/EnumMap;"))
	private void createCustomBuildersMap(
		SectionPos sectionPos, RenderSectionRegion region, VertexSorting vertexSorting, SectionBufferBuilderPack sectionBufferBuilderPack,
		CallbackInfoReturnable<SectionCompiler.Results> cir,
		@Share("customBuilders") LocalRef<Map<CustomChunkSectionLayer, BufferBuilder>> customBuildersRef) {
		customBuildersRef.set(new IdentityHashMap<>());
	}

	@Definition(id = "blockState", local = @Local(type = BlockState.class))
	@Definition(id = "getRenderShape", method = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;")
	@Definition(id = "MODEL", field = "Lnet/minecraft/world/level/block/RenderShape;MODEL:Lnet/minecraft/world/level/block/RenderShape;")
	@Expression("blockState.getRenderShape() == MODEL")
	@ModifyExpressionValue(method = "compile", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean compileCustomLayer(
		boolean original,
		@Local(argsOnly = true) SectionBufferBuilderPack pack,
		@Local(argsOnly = true) RenderSectionRegion region,
		@Local RandomSource random,
		@Local PoseStack poseStack,
		@Local BlockState state,
		@Local(ordinal = 2) BlockPos pos,
		@Local List<BlockModelPart> modelParts,
		@Share("customBuilders") LocalRef<Map<CustomChunkSectionLayer, BufferBuilder>> customBuildersRef
	) {
		CustomChunkSectionLayer layer = SpectreChunkSectionLayers.CHUNK_LAYER_CALLBACK.invoker().getChunkSectionLayer(state);

		// No custom layer, just render it normally
		if (layer == null) {
			return original;
		}

		Map<CustomChunkSectionLayer, BufferBuilder> customBuilders = customBuildersRef.get();

		BufferBuilder bufferBuilder = this.getOrBeginCustomLayer(customBuilders, pack, layer);
		random.setSeed(state.getSeed(pos));
		this.blockRenderer.getBlockModel(state).collectParts(random, modelParts);

		poseStack.pushPose();
		poseStack.translate(
			(float)SectionPos.sectionRelative(pos.getX()),
			(float)SectionPos.sectionRelative(pos.getY()),
			(float)SectionPos.sectionRelative(pos.getZ())
		);

		this.blockRenderer.renderBatched(state, pos, region, poseStack, bufferBuilder, true, modelParts);
		poseStack.popPose();

		modelParts.clear();

		// Since we compiled this on a custom layer, it won't have a vanilla one, and trying to draw it will crash the game, so cancel it
		return false;
	}

	@Inject(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;clearCache()V"))
	private void buildCustomBuffers(
		SectionPos sectionPos, RenderSectionRegion region, VertexSorting vertexSorting, SectionBufferBuilderPack sectionBufferBuilderPack,
		CallbackInfoReturnable<SectionCompiler.Results> cir,
		@Local SectionCompiler.Results results,
		@Share("customBuilders") LocalRef<Map<CustomChunkSectionLayer, BufferBuilder>> customBuildersRef
	) {
		Map<CustomChunkSectionLayer, BufferBuilder> customBuilders = customBuildersRef.get();

		for (Map.Entry<CustomChunkSectionLayer, BufferBuilder> entry : customBuilders.entrySet()) {
			CustomChunkSectionLayer layer = entry.getKey();
			MeshData meshData = entry.getValue().build();

			if (meshData != null) {
				results.spectre$renderedLayers().put(layer, meshData);
			}
		}
	}

	@Unique
	private BufferBuilder getOrBeginCustomLayer(
		Map<CustomChunkSectionLayer, BufferBuilder> builders,
		SectionBufferBuilderPack sectionBufferBuilderPack,
		CustomChunkSectionLayer layer
	) {
		BufferBuilder bufferBuilder = builders.get(layer);
		if (bufferBuilder == null) {
			ByteBufferBuilder byteBufferBuilder = sectionBufferBuilderPack.spectre$buffer(layer);
			bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
			builders.put(layer, bufferBuilder);
		}

		return bufferBuilder;
	}

	@Mixin(SectionCompiler.Results.class)
	public static class ResultsMixin implements SpectreSectionCompilerResults {
		@Unique
		public final Map<CustomChunkSectionLayer, MeshData> renderedCustomLayers = new IdentityHashMap<>();

		@Override
		public Map<CustomChunkSectionLayer, MeshData> spectre$renderedLayers() {
			return renderedCustomLayers;
		}

		@Inject(method = "release", at = @At("RETURN"))
		private void releaseCustomMeshes(CallbackInfo ci) {
			this.renderedCustomLayers.values().forEach(MeshData::close);
		}
	}
}
