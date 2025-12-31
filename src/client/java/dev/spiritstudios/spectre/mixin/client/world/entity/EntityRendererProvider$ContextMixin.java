package dev.spiritstudios.spectre.mixin.client.world.entity;

import dev.spiritstudios.spectre.api.client.model.animation.EntityAnimationSet;
import dev.spiritstudios.spectre.api.client.model.ext.SpectreEntityRendererProviderContext;
import dev.spiritstudios.spectre.impl.client.pond.SpectreInternalEntityRendererProviderContext;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRendererProvider.Context.class)
public class EntityRendererProvider$ContextMixin implements SpectreEntityRendererProviderContext, SpectreInternalEntityRendererProviderContext {
	@Unique
	private EntityAnimationSet animationSet;

	@Override
	public void spectre$setAnimationSet(EntityAnimationSet animationSet) {
		if (this.animationSet != null) throw new IllegalStateException("Attempted to set animationSet twice!");
		this.animationSet = animationSet;
	}

	@Override
	public EntityAnimationSet getAnimationSet() {
		return animationSet;
	}
}
