package dev.spiritstudios.spectre.mixin.client.world.entity;

import dev.spiritstudios.spectre.api.world.entity.EntityPart;
import dev.spiritstudios.spectre.api.world.entity.PartHolder;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHitboxDebugRenderer.class)
public final class EntityHitboxDebugRendererMixin {
	@Inject(method = "showHitboxes", at = @At(value = "INVOKE", target = "Lnet/minecraft/gizmos/Gizmos;arrow(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;I)Lnet/minecraft/gizmos/GizmoProperties;"))
	private void addParts(Entity entity, float partialTick, boolean bl, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.getSubEntities()) {
				Gizmos.cuboid(
					part.getBoundingBox().move(part.getPosition(partialTick).subtract(part.position())),
					GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.25F, 1.0F, 0.0F))
				);
			}
		}
	}
}
