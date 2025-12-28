package dev.spiritstudios.spectre.mixin.client.world.level;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import dev.spiritstudios.spectre.impl.client.SpectreClient;
import dev.spiritstudios.spectre.api.client.world.level.render.CustomChunkSectionLayer;
import dev.spiritstudios.spectre.impl.client.world.level.render.SpectreSectionBufferBuilderPack;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(SectionBufferBuilderPack.class)
public class SectionBufferBuilderPackMixin implements SpectreSectionBufferBuilderPack {
	// TODO: Figure out how to handle TOTAL_BUFFERS_SIZE

	@Unique
	private final Map<CustomChunkSectionLayer, ByteBufferBuilder> customBuffers = Util.make(new IdentityHashMap<>(), map -> {
		for (CustomChunkSectionLayer layer : SpectreClient.CUSTOM_LAYERS) {
			map.put(layer, new ByteBufferBuilder(layer.bufferSize()));
		}
	});

	@Override
	public ByteBufferBuilder spectre$buffer(CustomChunkSectionLayer layer) {
		return this.customBuffers.get(layer);
	}

	@Inject(method = "clearAll", at = @At("RETURN"))
	public void clearAll(CallbackInfo ci) {
		this.customBuffers.values().forEach(ByteBufferBuilder::clear);
	}

	@Inject(method = "discardAll", at = @At("RETURN"))
	public void discardAll(CallbackInfo ci) {
		this.customBuffers.values().forEach(ByteBufferBuilder::discard);
	}

	@Inject(method = "close", at = @At("RETURN"))
	public void close(CallbackInfo ci) {
		this.customBuffers.values().forEach(ByteBufferBuilder::close);
	}
}
