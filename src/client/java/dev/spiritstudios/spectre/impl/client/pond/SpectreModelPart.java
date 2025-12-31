package dev.spiritstudios.spectre.impl.client.pond;

public interface SpectreModelPart {
	default String spectre$getName() {
		throw new IllegalStateException("Implemented via mixin");
	}

	default void spectre$setName(String name) {
		throw new IllegalStateException("Implemented via mixin");
	}
}
