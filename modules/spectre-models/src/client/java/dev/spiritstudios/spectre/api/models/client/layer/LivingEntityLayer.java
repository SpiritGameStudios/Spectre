package dev.spiritstudios.spectre.api.models.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.api.models.client.layer.data.LayerData;
import dev.spiritstudios.spectre.api.models.client.layer.data.LayerDataType;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import org.joml.Vector2ic;

public class LivingEntityLayer<S extends LivingEntityRenderState> extends RenderLayer<S, EntityModel<S>> {
	public record Data(
		Vector2ic textureSize,
		Identifier texture,
		SimpleBufferProvider bufferProvider,
		int tint
	) implements LayerData<LivingEntityRenderState> {
		public static final LayerDataType<Data, LivingEntityRenderState> TYPE = new LayerDataType<Data, LivingEntityRenderState>(
			Identifier.fromNamespaceAndPath("spectre", "living_entity"),
			RecordCodecBuilder.mapCodec(instance -> instance.group(
				LayerData.textureSizeCodec(),
				Identifier.CODEC.fieldOf("texture").forGetter(Data::texture),
				SimpleBufferProvider.CODEC.optionalFieldOf("render_type", RenderTypes::entityCutout).forGetter(Data::bufferProvider),
				// TODO: Proper color codec
				Codec.INT.optionalFieldOf("tint", CommonColors.WHITE).forGetter(Data::tint)
			).apply(instance, Data::new)),
			LivingEntityLayer::new,
			Data.class
		);

		@Override
		public LayerDataType<Data, LivingEntityRenderState> type() {
			return TYPE;
		}
	}

	private final EntityModel<S> model;
	private final Data data;

	public LivingEntityLayer(RenderLayerParent<S, EntityModel<S>> renderer, EntityModel<S> model, Data data) {
		super(renderer);

		this.model = model;
		this.data = data;
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords, S state, float yRot, float xRot) {
		if (!state.isInvisible) {
			RenderType renderType = data.bufferProvider.withTexture(data.texture);
			nodeCollector.order(1).submitModel(
				this.model,
				state,
				poseStack,
				renderType,
				lightCoords,
				LivingEntityRenderer.getOverlayCoords(state, 0.0F),
				data.tint,
				null,
				state.outlineColor,
				null
			);
		}
	}
}

