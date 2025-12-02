package dev.spiritstudios.spectre.api.core.registry;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class AutomaticDynamicRegistryProvider<T> extends FabricDynamicRegistryProvider {
	private final ResourceKey<Registry<T>> registryKey;
	private final String namespace;

	public AutomaticDynamicRegistryProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, ResourceKey<Registry<T>> registryKey, String namespace) {
		super(output, registriesFuture);
		this.registryKey = registryKey;
		this.namespace = namespace;
	}

	@Override
	protected void configure(HolderLookup.Provider wrapperLookup, Entries entries) {
		HolderLookup<T> wrapper = wrapperLookup.lookupOrThrow(registryKey);

		wrapper.listElementIds()
				.filter(key -> key.identifier().getNamespace().equals(namespace))
				.forEach(key -> entries.add(key, wrapper.getOrThrow(key).value()));
	}

	public static <T> FabricDataGenerator.Pack.RegistryDependentFactory<AutomaticDynamicRegistryProvider<T>> factory(ResourceKey<Registry<T>> registryKey, String namespace) {
		return (output, registriesFuture) -> new AutomaticDynamicRegistryProvider<>(output, registriesFuture, registryKey, namespace) {
			@Override
			public @NotNull String getName() {
				return "Dynamic Registry Entries for " + registryKey.identifier();
			}
		};
	}
}
