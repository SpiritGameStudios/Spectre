package dev.spiritstudios.spectre.mixin.world.entity;

import dev.spiritstudios.spectre.api.world.entity.EntityPart;
import dev.spiritstudios.spectre.api.world.entity.PartHolder;
import dev.spiritstudios.spectre.impl.world.entity.EntityPartLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(targets = "net/minecraft/server/level/ServerLevel$EntityCallbacks")
public abstract class ServerLevel$EntityCallbacksMixin {
	@Shadow
	@Final
	ServerLevel field_26936;

	@Inject(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;updateDynamicGameEventListener(Ljava/util/function/BiConsumer;)V"))
	private void startTracking(Entity entity, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.getSubEntities()) {
				((EntityPartLevel) this.field_26936).specter$getParts().put(part.getId(), part);
			}
		}
	}

	@Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;updateDynamicGameEventListener(Ljava/util/function/BiConsumer;)V"))
	private void stopTracking(Entity entity, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.getSubEntities()) {
				((EntityPartLevel) this.field_26936).specter$getParts().remove(part.getId(), part);
			}
		}
	}
}
