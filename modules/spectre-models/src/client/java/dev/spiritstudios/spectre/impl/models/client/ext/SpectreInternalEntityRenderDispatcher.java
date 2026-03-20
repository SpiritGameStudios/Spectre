package dev.spiritstudios.spectre.impl.models.client.ext;

import dev.spiritstudios.spectre.api.models.client.animation.EntityAnimationSet;

import java.util.function.Supplier;

public interface SpectreInternalEntityRenderDispatcher {
	default void spectre$setEntityAnimations(Supplier<EntityAnimationSet> entityAnimations) {
		throw new IllegalStateException("Implemented via mixin");
	}
}
