package dev.spiritstudios.spectre.impl.models.client.ext;

public interface SpectreModelPart {
	default String spectre$getName() {
		throw new IllegalStateException("Implemented via mixin");
	}

	default void spectre$setName(String name) {
		throw new IllegalStateException("Implemented via mixin");
	}
}
