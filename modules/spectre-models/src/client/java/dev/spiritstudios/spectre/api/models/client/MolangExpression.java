package dev.spiritstudios.spectre.api.models.client;


import dev.spiritstudios.mojank.compile.link.Alias;

@FunctionalInterface
public interface MolangExpression {
	float evaluate(@Alias("query") Query query);
}
