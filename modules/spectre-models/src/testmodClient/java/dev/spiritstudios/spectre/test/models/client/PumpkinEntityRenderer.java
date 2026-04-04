package dev.spiritstudios.spectre.test.models.client;

import dev.spiritstudios.spectre.test.models.PumpkinEntity;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.animation.definitions.CopperGolemAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class PumpkinEntityRenderer extends MobRenderer<PumpkinEntity, PumpkinRenderState, EntityModel<PumpkinRenderState>> {
	public static final Identifier MODEL = Identifier.fromNamespaceAndPath("spectre-testmod", "pumpkin");

	public static final ModelLayerLocation ALL = new ModelLayerLocation(MODEL, "all");

	public PumpkinEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new Model(context.bakeLayer(ALL)), 1.0F);

		context.bakeRenderLayers(
			MODEL,
			this,
			Model::new,
			PumpkinRenderState.class
		).forEach(this::addLayer);
	}

	@Override
	public Identifier getTextureLocation(PumpkinRenderState state) {
		return MissingTextureAtlasSprite.getLocation();
	}

	@Override
	public PumpkinRenderState createRenderState() {
		return new PumpkinRenderState();
	}

	@Override
	protected @Nullable RenderType getRenderType(PumpkinRenderState state, boolean isBodyVisible, boolean forceTransparent, boolean appearGlowing) {
		return null; // this will prevent the main model from rendering, it is useless anyway.
	}

	private static class Model extends EntityModel<PumpkinRenderState> {
		private final KeyframeAnimation walkAnimation;

		protected Model(ModelPart root) {
			super(root);

			this.walkAnimation = CopperGolemAnimation.COPPER_GOLEM_WALK.bake(root);
		}

		@Override
		public void setupAnim(PumpkinRenderState state) {
			super.setupAnim(state);

			this.walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2.0F, 2.5F);
		}
	}
}
