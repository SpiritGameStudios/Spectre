package dev.spiritstudios.spectre.impl.models.client;

import dev.spiritstudios.spectre.api.models.client.layer.LivingEntityLayer;
import dev.spiritstudios.spectre.api.models.client.layer.data.LayerDataType;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.Identifier;

public class SpectreModelsClientModInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LayerDataType.ID_MAPPER.put(
			Identifier.fromNamespaceAndPath("spectre", "living_entity"),
			LivingEntityLayer.Data.TYPE
		);
	}
}
