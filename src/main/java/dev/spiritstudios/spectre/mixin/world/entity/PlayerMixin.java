package dev.spiritstudios.spectre.mixin.world.entity;

import dev.spiritstudios.spectre.api.world.entity.EntityPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public abstract class PlayerMixin {
	@ModifyVariable(method = "attack", at = @At("HEAD"), argsOnly = true)
	private Entity attack(Entity target) {
		return target instanceof EntityPart<?> part ? part.getOwner() : target;
	}
}
