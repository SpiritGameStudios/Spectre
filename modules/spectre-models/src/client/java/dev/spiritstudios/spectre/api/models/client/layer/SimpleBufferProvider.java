package dev.spiritstudios.spectre.api.models.client.layer;

import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

@FunctionalInterface
public interface SimpleBufferProvider {
	ExtraCodecs.LateBoundIdMapper<Identifier, SimpleBufferProvider> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper<Identifier, SimpleBufferProvider>()
		.put(Identifier.withDefaultNamespace("entity_solid"), RenderTypes::entitySolid)
		.put(Identifier.withDefaultNamespace("entity_solid_z_offset_forward"), RenderTypes::entitySolidZOffsetForward)
		.put(Identifier.withDefaultNamespace("entity_cutout_cull"), RenderTypes::entityCutoutCull)
		.put(Identifier.withDefaultNamespace("entity_cutout"), RenderTypes::entityCutout)
		.put(Identifier.withDefaultNamespace("entity_cutout_z_offset"), RenderTypes::entityCutoutZOffset);

	Codec<SimpleBufferProvider> CODEC = ID_MAPPER.codec(Identifier.CODEC);

	RenderType withTexture(Identifier texture);
}
