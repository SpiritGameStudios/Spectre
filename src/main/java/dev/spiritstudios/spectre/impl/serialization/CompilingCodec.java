package dev.spiritstudios.spectre.impl.serialization;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import dev.spiritstudios.mojank.meow.Parser;
import dev.spiritstudios.spectre.impl.Spectre;

public class CompilingCodec<T> implements PrimitiveCodec<T> {
	private final Class<T> type;

	public CompilingCodec(Class<T> type) {
		this.type = type;
	}

	@Override
	public <T1> DataResult<T> read(DynamicOps<T1> ops, T1 input) {
		if (!(ops instanceof CompilerOps<T1, ?> compilerOps)) {
			return DataResult.error(() -> "Cannot decode CompilingCodec without a compiler!");
		}

		if (compilerOps.type != type) {
			return DataResult.error(() -> "Cannot decode CompilingCodec without a compiler of the correct type!");
		}

		return ops.getStringValue(input)
			.flatMap(string -> {
				var expression = Parser.MOLANG.parse(string);
				try {
					// Safe, we already checked the type above
					//noinspection unchecked
					return DataResult.success((T) compilerOps.compiler.compileAndInitialize(expression, string));
				} catch (Throwable e) {
					return DataResult.error(() -> "Compilation of molang expression '" + string + "' failed. " + e);
				}
			});
	}

	@Override
	public <T1> T1 write(DynamicOps<T1> ops, T value) {
		// Compiled expressions store their source code in getString
		return ops.createString(value.toString());
	}
}
