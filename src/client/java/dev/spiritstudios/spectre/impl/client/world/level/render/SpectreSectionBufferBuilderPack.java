package dev.spiritstudios.spectre.impl.client.world.level.render;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;

public interface SpectreSectionBufferBuilderPack {
	default ByteBufferBuilder spectre$buffer(CustomChunkSectionLayer layer) {
		throw new IllegalStateException("Implemented via mixin");
	}
}
