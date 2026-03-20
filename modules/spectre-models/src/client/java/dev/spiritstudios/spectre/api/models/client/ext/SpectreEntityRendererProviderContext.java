package dev.spiritstudios.spectre.api.models.client.ext;

import dev.spiritstudios.spectre.api.models.client.animation.AnimationLocation;
import dev.spiritstudios.spectre.api.models.client.animation.EntityAnimationSet;
import dev.spiritstudios.spectre.api.models.client.animation.SpectreKeyframeAnimation;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.ApiStatus;

/**
 * This is an injected interface, don't use it directly
 */
public interface SpectreEntityRendererProviderContext {
	default EntityAnimationSet getAnimationSet() {
		throw new IllegalStateException("Implemented via mixin.");
	}

	default SpectreKeyframeAnimation bakeAnimation(AnimationLocation location, ModelPart part) {
		return getAnimationSet().bake(location, part);
	}
}
