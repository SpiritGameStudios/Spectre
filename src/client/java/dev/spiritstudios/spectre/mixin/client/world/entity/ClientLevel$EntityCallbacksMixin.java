package dev.spiritstudios.spectre.mixin.client.world.entity;


import dev.spiritstudios.spectre.api.world.entity.EntityPart;
import dev.spiritstudios.spectre.api.world.entity.PartHolder;
import dev.spiritstudios.spectre.impl.world.entity.EntityPartLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/client/multiplayer/ClientLevel$EntityCallbacks")
public abstract class ClientLevel$EntityCallbacksMixin {
	@Shadow
	@Final
	ClientLevel field_27735;

	@Inject(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
	private void startTracking(Entity entity, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.getSubEntities()) {
				((EntityPartLevel) this.field_27735).specter$getParts().put(part.getId(), part);
			}
		}
	}

	@Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
	private void stopTracking(Entity entity, CallbackInfo ci) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.getSubEntities()) {
				((EntityPartLevel) this.field_27735).specter$getParts().remove(part.getId(), part);
			}
		}
	}
}
