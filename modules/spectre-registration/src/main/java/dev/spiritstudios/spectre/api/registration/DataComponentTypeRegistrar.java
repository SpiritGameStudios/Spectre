package dev.spiritstudios.spectre.api.registration;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.function.UnaryOperator;

public class DataComponentTypeRegistrar extends Registrar<DataComponentType<?>> {
	public DataComponentTypeRegistrar(String namespace) {
		super(BuiltInRegistries.DATA_COMPONENT_TYPE, namespace);
	}

	public <T> DataComponentType<T> register(
		String path,
		UnaryOperator<DataComponentType.Builder<T>> builder
	) {
		return Registry.register(
			registry,
			Identifier.fromNamespaceAndPath(namespace, path),
			builder.apply(DataComponentType.builder()).build()
		);
	}
}
