package dev.spiritstudios.spectre.api.registration;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class Registrar<T> {
	protected final Registry<T> registry;
	protected final String namespace;

	public Registrar(Registry<T> registry, String namespace) {
		this.registry = registry;
		this.namespace = namespace;
	}

	public <E extends T> E register(String path, E value) {
		return Registry.register(
			registry,
			Identifier.fromNamespaceAndPath(namespace, path),
			value
		);
	}

	public <E extends T> Holder<E> registerForHolder(String path, E value) {
		return Registry.registerForHolder(
			registry,
			Identifier.fromNamespaceAndPath(namespace, path),
			value
		);
	}
}
