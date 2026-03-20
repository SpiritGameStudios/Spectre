package dev.spiritstudios.spectre.api.effects.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ScreenshakeS2CPayload(float trauma) implements CustomPacketPayload {
	public static final ScreenshakeS2CPayload NONE = new ScreenshakeS2CPayload(0);

	public static final Type<ScreenshakeS2CPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("spectre-effects", "screenshake"));
	public static final StreamCodec<ByteBuf, ScreenshakeS2CPayload> CODEC =
			StreamCodec.composite(
					ByteBufCodecs.FLOAT, ScreenshakeS2CPayload::trauma,
					ScreenshakeS2CPayload::new
			);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
