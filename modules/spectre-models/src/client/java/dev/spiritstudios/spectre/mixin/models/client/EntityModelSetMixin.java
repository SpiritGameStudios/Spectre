package dev.spiritstudios.spectre.mixin.models.client;

import dev.spiritstudios.spectre.api.models.client.ext.SpectreEntityModelSet;
import dev.spiritstudios.spectre.api.models.client.layer.SpectreLayerDefinition;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mixin(EntityModelSet.class)
public class EntityModelSetMixin implements SpectreEntityModelSet {
	@Shadow
	@Final
	private Map<ModelLayerLocation, LayerDefinition> roots;
	@Unique
	private final Map<Identifier, Map<String, LayerDefinition>> models = new HashMap<>();

	@Inject(method = "<init>", at = @At("RETURN"))
	private void addSetTable(Map<ModelLayerLocation, LayerDefinition> roots, CallbackInfo ci) {
		for (Map.Entry<ModelLayerLocation, LayerDefinition> entry : roots.entrySet()) {
			var location = entry.getKey();
			var layer = entry.getValue();

			var model = models.computeIfAbsent(location.model(), _ -> new HashMap<>());
			model.put(location.layer(), layer);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S extends EntityRenderState, M extends EntityModel<S>> RenderLayer<S, M> bakeRenderLayer(
		ModelLayerLocation location,
		RenderLayerParent<S, M> parent,
		Function<ModelPart, M> createModel,
		Class<S> renderStateClass
	) {
		var def = roots.get(location);

		if (!(def instanceof SpectreLayerDefinition<?, ?> layerDefinition)) {
			throw new IllegalArgumentException("Layer '" + location + "' is not a Spectre Layer!");
		}

		// same as `if (!(def instanceof SpectreLayerDefinition<S, ?>))`
		if (!layerDefinition.data().type().renderStateClass.isAssignableFrom(renderStateClass)) {
			throw new IllegalArgumentException("Layer '" + location + "' only applies to " + layerDefinition.data().type().renderStateClass);
		}

		// This is checked by the if statement above.
		return ((SpectreLayerDefinition<?, S>)layerDefinition).bake(
			parent,
			createModel
		);
	}

	@Override
	public <S extends EntityRenderState, M extends EntityModel<S>> List<RenderLayer<S, M>> bakeRenderLayers(
		Identifier model,
		RenderLayerParent<S, M> parent,
		Function<ModelPart, M> createModel,
		Class<S> renderStateClass
	)  {
		var modelMap = models.get(model);

		if (modelMap == null) {
			throw new IllegalArgumentException("No model for " + model);
		} else {
			return modelMap.keySet()
				.stream()
				.filter(name -> !name.equals("all"))
				.map(name -> bakeRenderLayer(
					new ModelLayerLocation(model, name),
					parent,
					createModel,
					renderStateClass
				))
				.toList();
		}
	}
}
