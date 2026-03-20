package dev.spiritstudios.spectre.impl.effects;

import dev.spiritstudios.spectre.api.effects.network.ScreenshakeS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class SpectreEffectsInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		PayloadTypeRegistry.clientboundPlay().register(
			ScreenshakeS2CPayload.TYPE,
			ScreenshakeS2CPayload.CODEC
		);

		CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
			ScreenshakeCommand.register(dispatcher);
		});
	}
}
