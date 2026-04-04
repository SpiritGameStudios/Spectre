package dev.spiritstudios.spectre.api.models.client.ext;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.Function;

public interface SpectreEntityModelSet {
	default <S extends EntityRenderState, M extends EntityModel<S>> RenderLayer<S, M> bakeRenderLayer(
		ModelLayerLocation location,
		RenderLayerParent<S, M> parent,
		Function<ModelPart, M> createModel,
		Class<S> renderStateClass
	) {
		throw new IllegalStateException("Implemented via mixin.");
	}

	default <S extends EntityRenderState, M extends EntityModel<S>> List<RenderLayer<S, M>> bakeRenderLayers(
		Identifier model,
		RenderLayerParent<S, M> parent,
		Function<ModelPart, M> createModel,
		Class<S> renderStateClass
	) {
		throw new IllegalStateException("Implemented via mixin.");
	}
}
