package dev.spiritstudios.spectre.impl.client;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.Map;

public final class SpectreAccessibilityOptions {
	public static final OptionInstance<Double> SCREENSHAKE_INTENSITY = new OptionInstance<>(
		"options.spectre.screenshake_intensity",
		OptionInstance.cachedConstantTooltip(Component.translatable("options.spectre.screenshake_intensity.description")),
		SpectreAccessibilityOptions::getPercentValueOrOffText,
		OptionInstance.UnitDouble.INSTANCE,
		1.0,
		value -> {
		}
	);

	private static Component getPercentValueText(Component prefix, double value) {
		return Component.translatable("options.percent_value", prefix, (int) (value * 100.0));
	}

	private static Component getPercentValueOrOffText(Component prefix, double value) {
		return value == 0.0 ? Options.genericValueLabel(prefix, CommonComponents.OPTION_OFF) : getPercentValueText(prefix, value);
	}

	public static final Map<String, OptionInstance<?>> OPTIONS = Map.of(
		"screenshake_intensity", SCREENSHAKE_INTENSITY
	);
}
