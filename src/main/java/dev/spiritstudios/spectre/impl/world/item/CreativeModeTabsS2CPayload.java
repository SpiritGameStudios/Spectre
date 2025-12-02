package dev.spiritstudios.spectre.impl.world.item;

import dev.spiritstudios.spectre.api.world.item.CreativeModeTabFile;
import dev.spiritstudios.spectre.impl.Spectre;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.Map;

public record CreativeModeTabsS2CPayload(
	Map<Identifier, CreativeModeTabFile> tabs) implements CustomPacketPayload {
	public static final Type<CreativeModeTabsS2CPayload> TYPE = new Type<>(Spectre.id("creative_mode_tabs"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CreativeModeTabsS2CPayload> CODEC = ByteBufCodecs.<RegistryFriendlyByteBuf, Identifier, CreativeModeTabFile, Map<Identifier, CreativeModeTabFile>>map( // I love java
		Object2ObjectOpenHashMap::new,
		Identifier.STREAM_CODEC,
		CreativeModeTabFile.STREAM_CODEC
	).map(CreativeModeTabsS2CPayload::new, CreativeModeTabsS2CPayload::tabs);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
