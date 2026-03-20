package dev.spiritstudios.spectre.impl.effects.client;

import dev.spiritstudios.spectre.api.effects.client.SpectreScreenshake;
import dev.spiritstudios.spectre.api.effects.network.ScreenshakeS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.SharedConstants;

public final class SpectreEffectsClientInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(
			ScreenshakeS2CPayload.TYPE,
			(payload, _) -> {
				SpectreScreenshake.addTrauma(payload.trauma());
			}
		);

		ClientTickEvents.END_CLIENT_TICK.register(_ -> {
			SpectreScreenshake.addTrauma(-(1F / SharedConstants.TICKS_PER_SECOND) / 2F);
		});
	}
}
