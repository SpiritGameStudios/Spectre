package dev.spiritstudios.spectre.api.models.client.layer.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public interface LayerData<RenderState extends EntityRenderState> {
	Codec<LayerData<?>> CODEC = LayerDataType.ID_MAPPER.codec(Identifier.CODEC).dispatch(
		LayerData::type,
		type -> type.codec
	);

	static <Data extends LayerData<RenderState>, RenderState extends EntityRenderState> RecordCodecBuilder<Data, Vector2ic> textureSizeCodec() {
		return Codec.INT.listOf(2, 2)
				.<Vector2ic>xmap(
					list -> new Vector2i(list.getFirst(), list.get(1)),
					vec -> IntList.of(vec.x(), vec.y())
				)
				.fieldOf("texture_size")
				.forGetter(LayerData::textureSize);
	}

	default RenderLayer<RenderState, ?> build(
		RenderLayerParent<RenderState, EntityModel<RenderState>> parent,
		EntityModel<RenderState> model
	) {
		return build(
			parent,
			model
		);
	}

	Vector2ic textureSize();

	LayerDataType<?, RenderState> type();
}
