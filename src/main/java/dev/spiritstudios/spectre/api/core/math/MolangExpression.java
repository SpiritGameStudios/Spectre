package dev.spiritstudios.spectre.api.core.math;

import dev.spiritstudios.mojank.meow.Variables;
import dev.spiritstudios.mojank.meow.link.Alias;

@FunctionalInterface
public interface MolangExpression {
	float evaluate(
		@Alias("query") Query query,
		Variables variables
	);
}
