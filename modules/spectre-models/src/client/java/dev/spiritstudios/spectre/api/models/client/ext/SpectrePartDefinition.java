package dev.spiritstudios.spectre.api.models.client.ext;

import net.minecraft.client.model.geom.builders.PartDefinition;

public interface SpectrePartDefinition {
	default PartDefinition copyCubeless(PartDefinition newPart) {
		throw new IllegalStateException("Implemented via mixin.");
	}

	default PartDefinition copyCubeless() {
		throw new IllegalStateException("Implemented via mixin.");
	}
}
