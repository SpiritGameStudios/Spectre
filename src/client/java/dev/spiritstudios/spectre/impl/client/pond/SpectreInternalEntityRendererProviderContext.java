package dev.spiritstudios.spectre.impl.client.pond;

import dev.spiritstudios.spectre.api.client.model.animation.EntityAnimationSet;

public interface SpectreInternalEntityRendererProviderContext {
	default void spectre$setAnimationSet(EntityAnimationSet animationSet) {
		throw new IllegalStateException("Implemented via mixin.");
	}
}
