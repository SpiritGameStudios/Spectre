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
	public final Class<RenderState> renderStateClass;

	public LayerDataType(Identifier id, MapCodec<Data> codec, LayerFactory<Data, RenderState> factory, Class<Data> dataClass, Class<RenderState> renderStateClass) {
		this.codec = codec;
		this.factory = factory;
		this.dataClass = dataClass;
		this.renderStateClass = renderStateClass;

		ID_MAPPER.put(id, this);
	}

	public <M extends EntityModel<RenderState>> RenderLayer<RenderState, M> build(
		RenderLayerParent<RenderState, M> parent,
		M model,
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
		<M extends EntityModel<RenderState>> RenderLayer<RenderState, M> build(
			RenderLayerParent<RenderState, M> parent,
			M model,
			Data data
		);

		@SuppressWarnings("unchecked")
		default <M extends EntityModel<RenderState>> RenderLayer<RenderState, M> buildUnsafe(
			RenderLayerParent<RenderState, M> parent,
			M model,
			LayerData<RenderState> data
		) {
			return build(parent, model, (Data) data);
		}
	}
}
