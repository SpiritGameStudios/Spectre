package dev.spiritstudios.spectre.api.world.item;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class CreativeModeTabsProvider implements DataProvider {
	private final CompletableFuture<HolderLookup.Provider> lookupProvider;
	protected final PackOutput.PathProvider pathProvider;

	private final Map<Identifier, CreativeModeTabFile.Builder> builders = new Object2ObjectOpenHashMap<>();

	private HolderLookup.Provider lookup;

	protected CreativeModeTabsProvider(
		PackOutput output,
		CompletableFuture<HolderLookup.Provider> lookupProvider
	) {
		this.lookupProvider = lookupProvider;

		this.pathProvider = output.createPathProvider(
			PackOutput.Target.DATA_PACK,
			"spectre/creative_mode_tabs"
		);
	}

	@Override
	public @NotNull CompletableFuture<?> run(CachedOutput output) {
		return createContentsProvider()
			.thenCompose(lookup -> CompletableFuture.allOf(
				this.builders
					.entrySet()
					.stream()
					.map(entry -> DataProvider.saveStable(
						output,
						lookup,
						CreativeModeTabFile.CODEC,
						entry.getValue().build(lookup.lookupOrThrow(Registries.ITEM)),
						this.pathProvider.json(entry.getKey())
					))
					.toArray(CompletableFuture[]::new)
			));
	}

	protected abstract void addCreativeModeTabs(HolderLookup.Provider lookup);

	protected CreativeModeTabFile.Builder builder(Identifier id) {
		return builders.computeIfAbsent(
			id,
			(k) -> new CreativeModeTabFile.Builder()
		);
	}

	protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
		return this.lookupProvider.thenApply(provider -> {
			this.builders.clear();
			this.addCreativeModeTabs(provider);
			return provider;
		});
	}
}
