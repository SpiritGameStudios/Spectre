package dev.spiritstudios.spectre.api.core.registry.metatag;

import dev.spiritstudios.spectre.api.core.registry.SpectreRegistries;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class MetatagsProvider<K> implements DataProvider {
	private final CompletableFuture<HolderLookup.Provider> lookupProvider;

	private final Map<MetatagKey<K, ?>, MetatagBuilder<K, ?>> builders = new Object2ObjectOpenHashMap<>(); // ? always == ?
	private final @Nullable MetatagBuilder.ReverseLookup<K> reverseLookup;
	private final ResourceKey<? extends Registry<K>> registry;

	protected final PackOutput.PathProvider pathProvider;

	protected MetatagsProvider(
		PackOutput output,
		CompletableFuture<HolderLookup.Provider> lookupProvider,
		ResourceKey<? extends Registry<K>> registry,
		@Nullable MetatagBuilder.ReverseLookup<K> reverseLookup
	) {
		this.pathProvider = output.createPathProvider(
			PackOutput.Target.DATA_PACK,
			"metatags/" + registry.location().getNamespace() + "/" + registry.location().getPath()
		);
		this.registry = registry;
		this.lookupProvider = lookupProvider;
		this.reverseLookup = reverseLookup;
	}

	protected MetatagsProvider(
		PackOutput output,
		CompletableFuture<HolderLookup.Provider> lookupProvider,
		ResourceKey<? extends Registry<K>> registry
	) {
		this(output, lookupProvider, registry, null);
	}

	@Override
	public @NotNull String getName() {
		return "Metatags for " + this.registry.location();
	}

	@Override
	public @NotNull CompletableFuture<?> run(CachedOutput output) {
		return this.createContentsProvider()
			.thenCompose(lookup -> CompletableFuture.allOf(
				this.builders
					.entrySet()
					.stream()
					.map(entry -> save(entry, output, lookup))
					.toArray(CompletableFuture[]::new)
			));
	}

	@SuppressWarnings("unchecked")
	private <V> CompletableFuture<?> save(Map.Entry<MetatagKey<K, ?>, MetatagBuilder<K, ?>> entry, CachedOutput output, HolderLookup.Provider lookup) {
		MetatagKey<K, V> key = (MetatagKey<K, V>) entry.getKey();
		MetatagBuilder<K, V> builder = (MetatagBuilder<K, V>) entry.getValue();

		ResourceLocation metatagLocation = SpectreRegistries.METATAG.getKey(key);

		Path path = this.pathProvider.json(metatagLocation);

		return DataProvider.saveStable(
			output,
			lookup,
			MetatagFile.resourceCodecOf(key),
			builder.build(),
			path
		);
	}

	@SuppressWarnings("unchecked")
	protected <V> MetatagBuilder<K, V> builder(MetatagKey<K, V> key) {
		return (MetatagBuilder<K, V>) builders.computeIfAbsent(
			key,
			(k) -> new MetatagBuilder<>(
				lookupProvider.join().lookupOrThrow(registry),
				reverseLookup
			)
		);
	}

	protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
		return this.lookupProvider.thenApply(provider -> {
			this.builders.clear();
			this.addMetatags(provider);
			return provider;
		});
	}

	protected abstract void addMetatags(HolderLookup.Provider wrapperLookup);

	public static abstract class ItemMetatagProvider extends MetatagsProvider<Item> {
		@SuppressWarnings("deprecation")
		protected ItemMetatagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider, Registries.ITEM, item -> Optional.of(item.builtInRegistryHolder()));
		}
	}

	public static abstract class BlockMetatagProvider extends MetatagsProvider<Block> {
		@SuppressWarnings("deprecation")
		protected BlockMetatagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider, Registries.BLOCK, block -> Optional.of(block.builtInRegistryHolder()));
		}
	}

	public static abstract class EntityTypeMetatagProvider extends MetatagsProvider<EntityType<?>> {
		@SuppressWarnings("deprecation")
		protected EntityTypeMetatagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider, Registries.ENTITY_TYPE, type -> Optional.of(type.builtInRegistryHolder()));
		}
	}
}
