package dev.spiritstudios.spectre.api.world.entity.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.spiritstudios.mojank.meow.Variables;
import dev.spiritstudios.mojank.meow.link.Alias;
import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.impl.serialization.CompilingCodec;

@FunctionalInterface
public interface BooleanExpression {
	Codec<BooleanExpression> CODEC = Codec.withAlternative(
		new CompilingCodec<>(BooleanExpression.class),
		Codec.BOOL.flatComapMap(
			v -> (query, variables) -> v,
			function -> DataResult.error(() -> "Cannot encode molang expression.")
		)
	);

	boolean evaluate(
		@Alias({"query", "q"}) Query query,
		Variables variables
	);
}
