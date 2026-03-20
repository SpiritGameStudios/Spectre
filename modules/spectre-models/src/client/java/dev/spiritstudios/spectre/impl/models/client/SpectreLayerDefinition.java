package dev.spiritstudios.spectre.impl.models.client;

import dev.spiritstudios.spectre.api.models.client.layer.data.LayerData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.function.Function;

public record SpectreLayerDefinition<
	Data extends LayerData<RenderState>,
	RenderState extends EntityRenderState
	>(
	String name,
	MeshDefinition mesh,
	Data data
) {
	public RenderLayer<RenderState, ?> bake(RenderLayerParent<RenderState, EntityModel<RenderState>> parent, Function<ModelPart, EntityModel<RenderState>> createModel) {
		var bakedModel = mesh.getRoot().bake(data.textureSize().x(), data.textureSize().y());
		return data.build(
			parent,
			createModel.apply(bakedModel)
		);
	}
}
