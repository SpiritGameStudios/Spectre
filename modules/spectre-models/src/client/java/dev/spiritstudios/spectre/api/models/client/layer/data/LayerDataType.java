package dev.spiritstudios.spectre.api.models.client.layer.data;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public final class LayerDataType<Data extends LayerData<RenderState>, RenderState extends EntityRenderState> {
	public static final ExtraCodecs.LateBoundIdMapper<Identifier, LayerDataType<?, ?>> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();

	public final MapCodec<Data> codec;
	private final LayerFactory<Data, RenderState> factory;
	public final Class<Data> dataClass;

	public LayerDataType(Identifier id, MapCodec<Data> codec, LayerFactory<Data, RenderState> factory, Class<Data> dataClass) {
		this.codec = codec;
		this.factory = factory;
		this.dataClass = dataClass;

		ID_MAPPER.put(id, this);
	}

	public RenderLayer<RenderState, ?> build(
		RenderLayerParent<RenderState, EntityModel<RenderState>> parent,
		EntityModel<RenderState> model,
		LayerData<RenderState> data
	) {
		if (!dataClass.isAssignableFrom(data.getClass())) throw new ClassCastException("Cannot convert '" + data.getClass() + "' to '" + dataClass + "'");

		return factory.buildUnsafe(parent, model, data);
	}

	@FunctionalInterface
	public interface LayerFactory<
		Data extends LayerData<RenderState>,
		RenderState extends EntityRenderState
		> {
		RenderLayer<RenderState, ?> build(
			RenderLayerParent<RenderState, EntityModel<RenderState>> parent,
			EntityModel<RenderState> model,
			Data data
		);

		@SuppressWarnings("unchecked")
		default RenderLayer<RenderState, ?> buildUnsafe(
			RenderLayerParent<RenderState, EntityModel<RenderState>> parent,
			EntityModel<RenderState> model,
			LayerData<RenderState> data
		) {
			return build(parent, model, (Data) data);
		}
	}
}
