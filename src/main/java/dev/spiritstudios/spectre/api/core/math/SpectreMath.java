package dev.spiritstudios.spectre.api.core.math;

import java.util.Arrays;
import java.util.List;
import net.minecraft.core.Direction;

public final class SpectreMath {
	public static final List<Direction> HORIZONTAL_DIRECTIONS = Arrays.stream(Direction.values()).filter(direction ->
		direction.getAxis().isHorizontal()).toList();

	private SpectreMath() {
	}

	public static double wrap(double value, double min, double max) {
		double range = max - min;
		return value - range * Math.floor((value - min) / range);
	}
}
