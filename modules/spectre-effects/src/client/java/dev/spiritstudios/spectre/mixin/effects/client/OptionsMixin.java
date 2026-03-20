package dev.spiritstudios.spectre.mixin.effects.client;


import dev.spiritstudios.spectre.impl.effects.client.SpectreAccessibilityOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(Options.class)
public abstract class OptionsMixin {
	@Inject(method = "processOptions", at = @At("TAIL"))
	private void addScreenshakeIntensity(Options.FieldAccess accessor, CallbackInfo ci) {
		for (Map.Entry<String, OptionInstance<?>> entry : SpectreAccessibilityOptions.OPTIONS.entrySet()) {
			accessor.process("spectre_" + entry.getKey(), entry.getValue());
		}
	}
}
