package dev.spiritstudios.spectre.impl.serialization;

import com.mojang.serialization.DynamicOps;
import dev.spiritstudios.mojank.meow.compile.Compiler;
import dev.spiritstudios.spectre.api.core.math.MolangExpression;
import net.minecraft.resources.DelegatingOps;
import org.jetbrains.annotations.Nullable;

public class CompilerOps<T, C> extends DelegatingOps<T> {
	public Compiler<C> compiler;
	public final Class<C> type;

	public CompilerOps(DynamicOps<T> delegate, Compiler<C> compiler, Class<C> type) {
		super(delegate);
		this.compiler = compiler;
		this.type = type;
	}
}
