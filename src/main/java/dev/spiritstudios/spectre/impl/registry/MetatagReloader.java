package dev.spiritstudios.spectre.impl.registry;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.spectre.api.core.registry.metatag.MetatagFile;
import dev.spiritstudios.spectre.api.core.registry.metatag.MetatagKey;
import dev.spiritstudios.spectre.api.core.registry.SpectreRegistries;
import dev.spiritstudios.spectre.impl.Spectre;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleResourceReloader;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StrictJsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class MetatagReloader extends SimpleResourceReloader<List<MetatagContents<?, ?>>> {
	public static final Identifier ID = Spectre.id("metatags");

	private MetatagSyncS2CPayload syncPayload;

	public static void register() {
		ResourceLoader.get(PackType.SERVER_DATA).registerReloader(
			ID,
			new MetatagReloader()
		);

		// Metatags need to come after dynamic registries
		ResourceLoader.get(PackType.SERVER_DATA).addReloaderOrdering(
			ResourceReloaderKeys.AFTER_VANILLA,
			ID
		);
	}

	private MetatagReloader() {
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
			if (player.level().getServer().isSingleplayerOwner(player.nameAndId())) return;

			ServerPlayNetworking.send(player, syncPayload);
		});
	}

	private <K, V> MetatagContents<K, V> decodeMetatag(
		RegistryOps<JsonElement> ops,
		MetatagKey<K, V> metatag,
		Identifier metatagId,
		List<Resource> resources
	) {
		var codec = MetatagFile.resourceCodecOf(metatag);

		var results = new Object2ObjectOpenHashMap<Holder<K>, V>();

		// Entries are sorted from least to most significant in the data pack list
		for (Resource resource : resources) {
			try (var reader = resource.openAsReader()) {
				codec.parse(ops, StrictJsonParser.parse(reader))
					.ifSuccess(result -> {
						if (result.replace()) {
							results.clear();
						}

						result.entries().forEach((k, v) -> {
							var existing = results.remove(k);
							// The first argument has higher priority in the merge function
							if (existing != null) v = metatag.merge(v, existing);

							results.put(k, v);
						});
					})
					.ifError(error -> Spectre.LOGGER.error(
						"Couldn't parse metatag file '{}': {}",
						metatagId, error
					));
			} catch (IOException error) {
				Spectre.LOGGER.error(
					"Couldn't parse metatag file '{}': {}",
					metatagId, error
				);
			}
		}

		return new MetatagContents<>(
			metatag,
			results
		);
	}

	@Override
	protected List<MetatagContents<?, ?>> prepare(SharedState store) {
		HolderLookup.Provider registries = store.get(ResourceLoader.RELOADER_REGISTRY_LOOKUP_KEY);
		var resourceManager = store.resourceManager();

		var ops = registries.createSerializationContext(JsonOps.INSTANCE);

		var result = new ArrayList<MetatagContents<?, ?>>();

		SpectreRegistries.METATAG.listElements().forEach(entry -> {
			var metatag = entry.value();
			var metatagId = entry.key().identifier();

			Identifier registryId = metatag.registry().identifier();

			// eg. specter:metatags/minecraft/block/strippable.json
			var resources = resourceManager.getResourceStack(
				Identifier.fromNamespaceAndPath(
					metatagId.getNamespace(),
					"metatags/" + registryId.getNamespace() + "/" + registryId.getPath() + "/" + metatagId.getPath() + ".json"
				)
			);

			result.add(decodeMetatag(ops, metatag, metatagId, resources));
		});

		return result;
	}

	@Override
	protected void apply(List<MetatagContents<?, ?>> prepared, SharedState store) {
		HolderLookup.Provider registries = store.get(ResourceLoader.RELOADER_REGISTRY_LOOKUP_KEY);

		MetatagContents.apply(registries, prepared);

		syncPayload = new MetatagSyncS2CPayload(prepared.stream()
			.filter(contents -> contents.metatag().packetCodec() != null)
			.toList());
	}
}
