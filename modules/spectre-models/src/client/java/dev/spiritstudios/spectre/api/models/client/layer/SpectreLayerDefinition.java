package dev.spiritstudios.spectre.api.models.client.layer;

import dev.spiritstudios.spectre.api.models.client.layer.data.LayerData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MaterialDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.function.Function;

public class SpectreLayerDefinition<
	Data extends LayerData<RenderState>,
	RenderState extends EntityRenderState
	> extends LayerDefinition {
	private final String name;
	private final MeshDefinition mesh;
	private final Data data;

	public SpectreLayerDefinition(
		String name,
		MeshDefinition mesh,
		Data data
	) {
		if (name.equals("all")) {
			throw new IllegalArgumentException("The layer name `all` is reserved and may not be used.");
		}

		super(mesh, new MaterialDefinition(data.textureSize().x(), data.textureSize().y()));
		this.name = name;
		this.mesh = mesh;
		this.data = data;
	}

	public <M extends EntityModel<RenderState>> RenderLayer<RenderState, M> bake(RenderLayerParent<RenderState, M> parent, Function<ModelPart, M> createModel) {
		var bakedModel = mesh.getRoot().bake(data.textureSize().x(), data.textureSize().y());
		M model = createModel.apply(bakedModel);
		return data.build(
			parent,
			model
		);
	}

	public String name() {
		return name;
	}

	public MeshDefinition mesh() {
		return mesh;
	}

	public Data data() {
		return data;
	}
}
