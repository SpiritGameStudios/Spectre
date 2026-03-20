package dev.spiritstudios.spectre.test.models.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import dev.spiritstudios.spectre.api.models.client.animation.AnimationLocation;
import dev.spiritstudios.spectre.test.models.EntityDisplay;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

public class EntityDisplayRenderer extends DisplayRenderer<EntityDisplay, EntityDisplay.RenderState, EntityDisplayRenderState> {
	private final EntityRendererProvider.Context context;
	private final Map<ModelLayerLocation, ModelPart> partCache = new HashMap<>();

	@Unique
	private static final Logger LOGGER = LogUtils.getLogger();

	protected EntityDisplayRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public EntityDisplayRenderState createRenderState() {
		return new EntityDisplayRenderState();
	}

	@Override
	public void extractRenderState(EntityDisplay entity, EntityDisplayRenderState state, float f) {
		super.extractRenderState(entity, state, f);
		state.query.set(entity, f);
		state.state = entity.state();
	}

	@Override
	public void submitInner(
		EntityDisplayRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, float f
	) {
		var modelId = state.state.model();
		var mHashIndex = modelId.getPath().indexOf('/');
		var modelLoc = modelId.withPath(path -> path.substring(0, mHashIndex));
		var mLayer = modelId.getPath().substring(mHashIndex + 1);

		var part = partCache.computeIfAbsent(
			new ModelLayerLocation(modelLoc, mLayer),
			id -> {
				try {
					return context.bakeLayer(id);
				} catch (IllegalArgumentException e) {
					LOGGER.error("No model for layer {}", id);
					return context.bakeLayer(ModelLayers.ZOMBIE);
				}
			}
		);

		var animId = state.state.anim();
		if (!animId.getPath().equals("none")) {
			var aHashIndex = animId.getPath().indexOf('/');
			var aLoc = animId.withPath(path -> path.substring(0, aHashIndex));
			var aLayer = animId.getPath().substring(aHashIndex + 1);


			var anim = context.bakeAnimation(
				new AnimationLocation(
					aLoc, aLayer
				),
				part
			);

			anim.apply(state.query, state.ageInTicks, 1F);
		} else {
			part.resetPose();
		}

		poseStack.pushPose();
		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.translate(0.0F, -1.501F, 0.0F);
		submitNodeCollector.submitModelPart(
			part,
			poseStack,
			RenderTypes.entityCutout(state.state.texture()),
			state.lightCoords,
			OverlayTexture.NO_OVERLAY,
			null
		);

		poseStack.popPose();
	}
}
