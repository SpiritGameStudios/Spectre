package dev.spiritstudios.spectre.impl.serialization;

import com.mojang.serialization.DynamicOps;
import dev.spiritstudios.mojank.meow.compile.Compiler;
import dev.spiritstudios.spectre.api.core.math.MolangExpression;
import net.minecraft.resources.DelegatingOps;
import org.jetbrains.annotations.Nullable;

public class CompilerOps<T> extends DelegatingOps<T> {
	public final Compiler<MolangExpression> compiler;

	public CompilerOps(DynamicOps<T> delegate, Compiler<MolangExpression> compiler) {
		super(delegate);
		this.compiler = compiler;
	}
}
