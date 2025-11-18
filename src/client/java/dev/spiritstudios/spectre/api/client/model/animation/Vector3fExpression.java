package dev.spiritstudios.spectre.api.client.model.animation;

import com.mojang.serialization.Codec;
import dev.spiritstudios.mojank.meow.Variables;
import dev.spiritstudios.spectre.api.core.math.MolangExpression;
import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.api.data.serialization.SpectreCodecs;
import org.joml.Vector3f;

import java.util.List;

@FunctionalInterface
public interface Vector3fExpression {
	Vector3f evaluate(Query query, Variables variables);

	Codec<Vector3fExpression> CODEC = Codec.withAlternative(
		SpectreCodecs.MOLANG.listOf(3, 3).xmap(
			Vector3fExpression::of,
			exp -> {
				throw new UnsupportedOperationException("Cannot encode Vector3fExpression");
			}
		),
		SpectreCodecs.MOLANG.xmap(
			Vector3fExpression::of,
			exp -> {
				throw new UnsupportedOperationException("Cannot encode Vector3fExpression");
			}
		)
	);

	Codec<Vector3fExpression> CODEC_SIXTEENTH = Codec.withAlternative(
		SpectreCodecs.MOLANG.listOf(3, 3).xmap(
			Vector3fExpression::ofSixteenth,
			exp -> {
				throw new UnsupportedOperationException("Cannot encode Vector3fExpression");
			}
		),
		SpectreCodecs.MOLANG.xmap(
			Vector3fExpression::ofSixteenth,
			exp -> {
				throw new UnsupportedOperationException("Cannot encode Vector3fExpression");
			}
		)
	);

	static Vector3fExpression of(MolangExpression expression) {
		return (query, variables) -> new Vector3f(expression.evaluate(query, variables));
	}

	static Vector3fExpression ofSixteenth(MolangExpression expression) {
		return (query, variables) -> new Vector3f(expression.evaluate(query, variables) / 16F);
	}

	static Vector3fExpression of(List<MolangExpression> list) {
		var x = list.get(0);
		var y = list.get(1);
		var z = list.get(2);

		return (query, variables) -> new Vector3f(
			x.evaluate(query, variables),
			y.evaluate(query, variables),
			z.evaluate(query, variables)
		);
	}

	static Vector3fExpression ofSixteenth(List<MolangExpression> list) {
		var x = list.get(0);
		var y = list.get(1);
		var z = list.get(2);

		return (query, variables) -> new Vector3f(
			x.evaluate(query, variables) / 16F,
			y.evaluate(query, variables) / 16F,
			z.evaluate(query, variables) / 16F
		);
	}
}
