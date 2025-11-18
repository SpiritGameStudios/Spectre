package dev.spiritstudios.spectre.impl.serialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import dev.spiritstudios.mojank.meow.Parser;
import dev.spiritstudios.spectre.api.core.math.MolangExpression;
import dev.spiritstudios.spectre.impl.Spectre;

import java.util.Objects;

public class MolangCodec implements PrimitiveCodec<MolangExpression> {
	public static final Codec<MolangExpression> INSTANCE = new MolangCodec();

	@Override
	public <T> DataResult<MolangExpression> read(DynamicOps<T> ops, T input) {
		if (!(ops instanceof CompilerOps<T> compilerOps)) return DataResult.error(() -> "Cannot decode MolangExpression without a compiler!");

		return ops.getStringValue(input)
			.map(string -> {
				var expression = Parser.MOLANG.parse(string);
				try {
					return compilerOps.compiler.compileAndInitialize(expression, string);
				} catch (Throwable e) {
					Spectre.LOGGER.info(string);
					Spectre.LOGGER.info(expression.toString());

					throw e;
				}
			});
	}

	@Override
	public <T> T write(DynamicOps<T> ops, MolangExpression value) {
		// Compiled expressions store their source code in getString
		return ops.createString(value.toString());
	}
}
