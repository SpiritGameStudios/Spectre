package dev.spiritstudios.spectre.impl.registry;

import dev.spiritstudios.spectre.api.core.registry.metatag.MetatagKey;
import dev.spiritstudios.spectre.api.core.registry.SpectreRegistryKeys;
import dev.spiritstudios.spectre.impl.Spectre;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MetatagSyncS2CPayload(List<MetatagContents<?, ?>> contents) implements CustomPacketPayload {
	public static final Type<MetatagSyncS2CPayload> TYPE = new Type<>(Spectre.id("metatag_sync"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MetatagSyncS2CPayload> CODEC = ByteBufCodecs.registry(SpectreRegistryKeys.METATAG).<MetatagContents<?, ?>>dispatch(
		MetatagContents::metatag,
		MetatagSyncS2CPayload::contentsCodec
	).apply(ByteBufCodecs.list()).map(MetatagSyncS2CPayload::new, MetatagSyncS2CPayload::contents);

	private static <K, V> StreamCodec<RegistryFriendlyByteBuf, MetatagContents<K, V>> contentsCodec(MetatagKey<K, V> metatag) {
		return ByteBufCodecs.<RegistryFriendlyByteBuf, Holder<K>, V, Map<Holder<K>, V>>map(
			Object2ObjectLinkedOpenHashMap::new,
			ByteBufCodecs.holderRegistry(metatag.registry()),
			metatag.packetCodec()
		).map(
			map -> new MetatagContents<>(metatag, map),
			MetatagContents::entries
		);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
