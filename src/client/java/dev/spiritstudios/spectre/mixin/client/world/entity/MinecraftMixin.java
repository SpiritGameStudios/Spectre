package dev.spiritstudios.spectre.mixin.client.world.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.spiritstudios.spectre.impl.client.SpectreClient;
import dev.spiritstudios.spectre.impl.client.pond.SpectreInternalEntityRenderDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@ModifyExpressionValue(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/item/ItemModelResolver;Lnet/minecraft/client/renderer/MapRenderer;Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;Lnet/minecraft/client/resources/model/AtlasManager;Lnet/minecraft/client/gui/Font;Lnet/minecraft/client/Options;Ljava/util/function/Supplier;Lnet/minecraft/client/resources/model/EquipmentAssetManager;Lnet/minecraft/client/renderer/PlayerSkinRenderCache;)Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;"))
	private EntityRenderDispatcher addAnimations(EntityRenderDispatcher original) {
		original.spectre$setEntityAnimations(SpectreClient.ANIMATION_MANAGER.entityAnimations());
		return original;
	}
}
