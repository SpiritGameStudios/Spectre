package dev.spiritstudios.spectre.impl.models.client.ext;

import dev.spiritstudios.spectre.api.models.client.animation.EntityAnimationSet;

public interface SpectreInternalEntityRendererProviderContext {
	default void spectre$setAnimationSet(EntityAnimationSet animationSet) {
		throw new IllegalStateException("Implemented via mixin.");
	}
}
