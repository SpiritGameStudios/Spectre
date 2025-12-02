package dev.spiritstudios.spectre.api.core.registry.metatag;

import com.mojang.serialization.Codec;
import dev.spiritstudios.spectre.api.core.registry.SpectreRegistries;
import dev.spiritstudios.spectre.api.core.registry.metatag.builtin.StrippingData;
import dev.spiritstudios.spectre.impl.Spectre;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public final class SpectreMetatags {
	public static final MetatagKey<Item, Float> COMPOSTING_CHANCE = register(
		"composting_chance",
		new MetatagKey.Builder<>(
			Registries.ITEM,
			Codec.floatRange(0F, 1F)
		).sync(ByteBufCodecs.FLOAT.cast()).build()
	);

	public static final MetatagKey<Block, StrippingData> STRIPPABLE = register(
		"strippable",
		new MetatagKey.Builder<>(
			Registries.BLOCK,
			StrippingData.CODEC
		).sync(StrippingData.STREAM_CODEC).build()
	);

	// This doesn't technically need to sync, but I could absolutely see a mod wanting this data on the client.
	public static final MetatagKey<Biome, Holder<VillagerType>> VILLAGER_TYPE = register(
		"villager_type",
		new MetatagKey.Builder<>(
			Registries.BIOME,
			RegistryFixedCodec.create(Registries.VILLAGER_TYPE)
		).sync(ByteBufCodecs.holderRegistry(Registries.VILLAGER_TYPE)).build()
	);

	private static <K, V> MetatagKey<K, V> register(String path, MetatagKey<K, V> metatag) {
		return Registry.register(
			SpectreRegistries.METATAG,
			Spectre.id(path),
			metatag
		);
	}
}
