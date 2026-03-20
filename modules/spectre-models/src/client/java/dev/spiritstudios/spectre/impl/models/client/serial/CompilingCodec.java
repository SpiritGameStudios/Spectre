package dev.spiritstudios.spectre.impl.models.client.serial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import dev.spiritstudios.mojank.MolangLexer;
import dev.spiritstudios.mojank.MolangParser;
import dev.spiritstudios.mojank.compile.Compiler;
import dev.spiritstudios.mojank.compile.link.Linker;
import dev.spiritstudios.spectre.api.models.client.MolangExpression;
import dev.spiritstudios.spectre.api.models.client.Query;
import dev.spiritstudios.spectre.impl.models.client.MolangMath;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;

public class CompilingCodec<T> implements PrimitiveCodec<T> {
	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	public static final Codec<MolangExpression> MOLANG = Codec.withAlternative(
		new CompilingCodec<>(
			Linker.UNTRUSTED.toBuilder()
				.addAllowedClasses(
					Vec3.class,
					Vector4fc.class, Vector4f.class,
					Vector3fc.class, Vector3f.class,
					Vector2fc.class, Vector2f.class,
					MolangExpression.class, Query.class
				)
				.aliasClass(MolangMath.class, "math")
				.build(),
			LOOKUP,
			MolangExpression.class
		),
		Codec.FLOAT.xmap(
			v -> _ -> v,
			function -> function.evaluate(null)
		)
	);

	private final Linker linker;
	private final MethodHandles.Lookup lookup;
	private final Class<T> targetClass;

	public CompilingCodec(Linker linker, MethodHandles.Lookup lookup, Class<T> type) {
		this.linker = linker;
		this.lookup = lookup;
		this.targetClass = type;
	}

	@Override
	public <T1> DataResult<T> read(DynamicOps<T1> ops, T1 input) {
		return ops.getStringValue(input)
			.flatMap(string -> {
				try {
					var expression = new MolangParser(new MolangLexer(new StringReader(string)), linker).parseAll();
					var result = Compiler.compile(lookup, linker, targetClass, expression, string);

					return DataResult.success(result);
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
