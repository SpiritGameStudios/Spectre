package dev.spiritstudios.spectre.mixin.effects.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.spiritstudios.spectre.impl.effects.client.SpectreAccessibilityOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AccessibilityOptionsScreen.class)
public abstract class AccessibilityOptionsScreenMixin {
	@ModifyReturnValue(method = "options", at = @At("RETURN"))
	private static OptionInstance<?>[] addOptions(OptionInstance<?>[] original) {
		return ArrayUtils.addAll(original, SpectreAccessibilityOptions.OPTIONS.values().toArray(OptionInstance[]::new));
	}
}
