package dev.spiritstudios.spectre.mixin.client.world.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.spiritstudios.spectre.api.client.model.animation.EntityAnimationSet;
import dev.spiritstudios.spectre.impl.client.pond.SpectreInternalEntityRenderDispatcher;
import dev.spiritstudios.spectre.impl.client.pond.SpectreInternalEntityRendererProviderContext;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin implements SpectreInternalEntityRenderDispatcher {
	@Unique
	private Supplier<EntityAnimationSet> entityAnimations;

	@Unique
	public void spectre$setEntityAnimations(Supplier<EntityAnimationSet> entityAnimations) {
		if (this.entityAnimations != null) throw new IllegalStateException("Attempted to set entityAnimations twice!");
		this.entityAnimations = entityAnimations;
	}

	@ModifyExpressionValue(method = "onResourceManagerReload", at = @At(value = "NEW", target = "(Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;Lnet/minecraft/client/renderer/item/ItemModelResolver;Lnet/minecraft/client/renderer/MapRenderer;Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/client/model/geom/EntityModelSet;Lnet/minecraft/client/resources/model/EquipmentAssetManager;Lnet/minecraft/client/resources/model/AtlasManager;Lnet/minecraft/client/gui/Font;Lnet/minecraft/client/renderer/PlayerSkinRenderCache;)Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;"))
	private EntityRendererProvider.Context addAnimationSet(EntityRendererProvider.Context original) {
		original.spectre$setAnimationSet(entityAnimations.get());
		return original;
	}
}
