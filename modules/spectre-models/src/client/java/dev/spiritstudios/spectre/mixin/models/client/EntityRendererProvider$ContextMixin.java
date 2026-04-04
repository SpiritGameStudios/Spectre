package dev.spiritstudios.spectre.mixin.models.client;

import dev.spiritstudios.spectre.api.models.client.animation.EntityAnimationSet;
import dev.spiritstudios.spectre.api.models.client.ext.SpectreEntityRendererProviderContext;
import dev.spiritstudios.spectre.impl.models.client.ext.SpectreInternalEntityRendererProviderContext;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.function.Function;

@Mixin(EntityRendererProvider.Context.class)
public class EntityRendererProvider$ContextMixin implements SpectreEntityRendererProviderContext, SpectreInternalEntityRendererProviderContext {
	@Shadow
	@Final
	private EntityModelSet modelSet;
	@Unique
	private EntityAnimationSet animationSet;

	@Override
	public void spectre$setAnimationSet(EntityAnimationSet animationSet) {
		if (this.animationSet != null) throw new IllegalStateException("Attempted to set animationSet twice!");
		this.animationSet = animationSet;
	}

	@SuppressWarnings("AddedMixinMembersNamePattern")
	@Override
	public EntityAnimationSet getAnimationSet() {
		return animationSet;
	}

	@Override
	public <S extends EntityRenderState, M extends EntityModel<S>> RenderLayer<S, M> bakeRenderLayer(
		ModelLayerLocation location,
		RenderLayerParent<S, M> parent,
		Function<ModelPart, M> createModel,
		Class<S> renderStateClass
	) {
		return modelSet.bakeRenderLayer(location, parent, createModel, renderStateClass);
	}

	@Override
	public <S extends EntityRenderState, M extends EntityModel<S>> List<RenderLayer<S, M>> bakeRenderLayers(
		Identifier model,
		RenderLayerParent<S, M> parent,
		Function<ModelPart, M> createModel,
		Class<S> renderStateClass
	) {
		return modelSet.bakeRenderLayers(model, parent, createModel, renderStateClass);
	}
}
