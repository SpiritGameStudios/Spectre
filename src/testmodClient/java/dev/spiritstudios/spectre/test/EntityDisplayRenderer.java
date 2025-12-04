package dev.spiritstudios.spectre.test;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.spiritstudios.spectre.impl.Spectre;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class EntityDisplayRenderer extends DisplayRenderer<EntityDisplay, EntityDisplay.RenderState, EntityDisplayRenderState> {
	private final EntityModelSet modelSet;

	protected EntityDisplayRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.modelSet = context.getModelSet();
	}

	@Override
	public EntityDisplayRenderState createRenderState() {
		return new EntityDisplayRenderState();
	}

	@Override
	public void extractRenderState(EntityDisplay entity, EntityDisplayRenderState state, float f) {
		super.extractRenderState(entity, state, f);
		state.state = entity.state();
	}

	@Override
	public void submitInner(
		EntityDisplayRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, float f
	) {
		var id = state.state.model();
		var hashIndex = id.getPath().indexOf('/');
		var modelLoc = id.withPath(path -> path.substring(0, hashIndex));
		var layer = id.getPath().substring(hashIndex + 1);

		var part = modelSet.bakeLayer(new ModelLayerLocation(
			modelLoc, layer
		));

		submitNodeCollector.submitModelPart(
			part,
			poseStack,
			RenderTypes.entityCutoutNoCull(Spectre.id("invalid")),
			state.lightCoords,
			OverlayTexture.NO_OVERLAY,
			null
		);
	}
}
