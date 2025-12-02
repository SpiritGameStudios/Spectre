package dev.spiritstudios.spectre.api.core.registry.metatag.builtin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record StrippingData(Block result, StrippableBlockRegistry.StrippingTransformer transformer) {
	private static final BiMap<String, StrippableBlockRegistry.StrippingTransformer> TRANSFORMERS = Util.make(
		HashBiMap.create(),
		map -> {
			map.put("vanilla", StrippableBlockRegistry.StrippingTransformer.VANILLA);
			map.put("copy", StrippableBlockRegistry.StrippingTransformer.COPY);
			map.put("default_state", StrippableBlockRegistry.StrippingTransformer.DEFAULT_STATE);
		}
	);

	private static final BiMap<StrippableBlockRegistry.StrippingTransformer, String> TRANSFORMERS_INVERSE = TRANSFORMERS.inverse();

	public BlockState getStrippedBlockState(BlockState input) {
		return this.transformer.getStrippedBlockState(
			this.result,
			input
		);
	}

	public static final Codec<StrippingData> CODEC = Codec.withAlternative(
		RecordCodecBuilder.create(instance -> instance.group(
			BuiltInRegistries.BLOCK.byNameCodec().fieldOf("result").forGetter(StrippingData::result),
			Codec.STRING.xmap(
				TRANSFORMERS::get,
				TRANSFORMERS_INVERSE::get
			).fieldOf("transformer").forGetter(StrippingData::transformer)
		).apply(instance, StrippingData::new)),
		BuiltInRegistries.BLOCK.byNameCodec().xmap(
			block -> new StrippingData(block, StrippableBlockRegistry.StrippingTransformer.COPY),
			StrippingData::result
		)
	);

	// Transformer doesn't matter on the client
	public static final StreamCodec<RegistryFriendlyByteBuf, StrippingData> STREAM_CODEC = ByteBufCodecs.registry(Registries.BLOCK).map(
		block -> new StrippingData(block, StrippableBlockRegistry.StrippingTransformer.COPY),
		StrippingData::result
	);
}
