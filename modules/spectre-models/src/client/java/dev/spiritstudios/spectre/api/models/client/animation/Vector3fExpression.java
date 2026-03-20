package dev.spiritstudios.spectre.api.models.client.animation;

import com.mojang.serialization.Codec;
import dev.spiritstudios.spectre.api.models.client.MolangExpression;
import dev.spiritstudios.spectre.api.models.client.Query;
import dev.spiritstudios.spectre.impl.models.client.serial.CompilingCodec;
import org.joml.Vector3f;

import java.util.List;

@FunctionalInterface
public interface Vector3fExpression {
	Vector3f evaluate(Query query);

	Codec<Vector3fExpression> CODEC = Codec.withAlternative(
		CompilingCodec.MOLANG.listOf(3, 3).xmap(
			Vector3fExpression::of,
			exp -> {
				throw new UnsupportedOperationException("Cannot encode Vector3fExpression");
			}
		),
		CompilingCodec.MOLANG.xmap(
			Vector3fExpression::of,
			exp -> {
				throw new UnsupportedOperationException("Cannot encode Vector3fExpression");
			}
		)
	);

	Codec<Vector3fExpression> CODEC_SIXTEENTH = Codec.withAlternative(
		CompilingCodec.MOLANG.listOf(3, 3).xmap(
			Vector3fExpression::ofSixteenth,
			exp -> {
				throw new UnsupportedOperationException("Cannot encode Vector3fExpression");
			}
		),
		CompilingCodec.MOLANG.xmap(
			Vector3fExpression::ofSixteenth,
			exp -> {
				throw new UnsupportedOperationException("Cannot encode Vector3fExpression");
			}
		)
	);

	static Vector3fExpression of(MolangExpression expression) {
		return (query) -> new Vector3f(expression.evaluate(query));
	}

	static Vector3fExpression ofSixteenth(MolangExpression expression) {
		return (query) -> new Vector3f(expression.evaluate(query) / 16F);
	}

	static Vector3fExpression of(List<MolangExpression> list) {
		var x = list.get(0);
		var y = list.get(1);
		var z = list.get(2);

		return (query) -> new Vector3f(
			x.evaluate(query),
			y.evaluate(query),
			z.evaluate(query)
		);
	}

	static Vector3fExpression ofSixteenth(List<MolangExpression> list) {
		var x = list.get(0);
		var y = list.get(1);
		var z = list.get(2);

		return (query) -> new Vector3f(
			x.evaluate(query) / 16F,
			y.evaluate(query) / 16F,
			z.evaluate(query) / 16F
		);
	}
}
