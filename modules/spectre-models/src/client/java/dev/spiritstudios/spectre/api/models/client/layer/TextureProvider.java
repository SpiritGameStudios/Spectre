package dev.spiritstudios.spectre.api.models.client.layer;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public interface TextureProvider<S extends EntityRenderState> {
	ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<TextureProvider<?>>> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();

//	Codec<TextureProvider<?>> CODEC = Codec.withAlternative(
//		ID_MAPPER.codec(Identifier.CODEC).dispatch(
//			d -> ,
//		),
//		Identifier.CODEC.xmap(
//			id -> ,
//			s -> s.getTexture(null)
//		)
//	);

	static <S extends EntityRenderState> TextureProvider<S> create(Identifier createTexture) {
		return new TextureProvider<>() {
			@Override
			public MapCodec<TextureProvider<S>> codec() {
				return MapCodec.unit(this);
			}

			@Override
			public Identifier getTexture(S state) {
				return createTexture;
			}
		};
	}

	MapCodec<? extends TextureProvider<S>> codec();
	Identifier getTexture(S state);
}
