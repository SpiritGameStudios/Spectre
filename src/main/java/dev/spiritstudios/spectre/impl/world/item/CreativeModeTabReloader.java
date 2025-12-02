package dev.spiritstudios.spectre.impl.world.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.spectre.api.world.item.CreativeModeTabFile;
import dev.spiritstudios.spectre.impl.Spectre;
import dev.spiritstudios.spectre.impl.registry.UnfrozenRegistry;
import dev.spiritstudios.spectre.mixin.world.item.CreativeModeTabAccessor;
import dev.spiritstudios.spectre.mixin.registry.unfreeze.MappedRegistryAccessor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleResourceReloader;
import net.fabricmc.fabric.impl.tag.TagAliasLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.item.CreativeModeTab;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class CreativeModeTabReloader extends SimpleResourceReloader<Map<Identifier, List<JsonElement>>> {
	public static final Identifier ID = Spectre.id("creative_mode_tabs");

	private static final Logger LOGGER = LogUtils.getLogger();
	private static final FileToIdConverter LISTER = FileToIdConverter.json("spectre/creative_mode_tabs");

	private CreativeModeTabsS2CPayload syncPayload;

	public static void register() {
		ResourceLoader.get(PackType.SERVER_DATA).registerReloader(
			ID,
			new CreativeModeTabReloader()
		);

		ResourceLoader.get(PackType.SERVER_DATA).addReloaderOrdering(
			Identifier.fromNamespaceAndPath("fabric-tag-api-v1", "tag_alias_groups"),
			ID
		);
	}

	private CreativeModeTabReloader() {
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
			if (player.level().getServer().isSingleplayerOwner(player.nameAndId())) return;

			ServerPlayNetworking.send(player, syncPayload);
		});
	}

	@Override
	protected Map<Identifier, List<JsonElement>> prepare(SharedState store) {
		Map<Identifier, List<JsonElement>> output = new Object2ObjectOpenHashMap<>();

		for (Map.Entry<Identifier, List<Resource>> entry : LISTER.listMatchingResourceStacks(store.resourceManager()).entrySet()) {
			Identifier fileId = entry.getKey();
			Identifier id = LISTER.fileToId(fileId);

			for (Resource resource : entry.getValue()) {
				try (Reader reader = resource.openAsReader()) {
					output.computeIfAbsent(id, k -> new ArrayList<>())
						.add(StrictJsonParser.parse(reader));
				} catch (IllegalArgumentException | IOException | JsonParseException e) {
					LOGGER.error("Couldn't parse data file '{}' from '{}'", id, fileId, e);
				}
			}
		}

		return output;
	}

	public static void apply(Map<Identifier, CreativeModeTabFile> tabs) {
		if (!(BuiltInRegistries.CREATIVE_MODE_TAB instanceof MappedRegistry<CreativeModeTab> registry)) {
			throw new IllegalStateException("Creative mode tab registry is not a MappedRegistry. This is likely caused by a mod incompatibility. Please report this to Spirit Studios.");
		}

		MappedRegistryAccessor accessor = (MappedRegistryAccessor) registry;
		accessor.setFrozen(false);

		@SuppressWarnings("unchecked") UnfrozenRegistry<CreativeModeTab> unfrozen = ((UnfrozenRegistry<CreativeModeTab>) registry);

		// Remove item groups that we made using creative tab resources, we are about to load them in again
		// We copy the set since otherwise we would be modifying it while iterating
		for (Map.Entry<ResourceKey<CreativeModeTab>, CreativeModeTab> mapEntry : Set.copyOf(registry.entrySet())) {
			if (((CreativeModeTabAccessor) mapEntry.getValue()).getDisplayItemsGenerator() instanceof CreativeModeTabFile) {
				unfrozen.specter$remove(mapEntry.getKey());
			}
		}

		tabs.forEach((id, value) -> {
			ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, id);
			var builder = CreativeModeTab.builder(
				null,
				-1
			);

			value.title().ifPresentOrElse(
				builder::title,
				builder::hideTitle
			);

			value.icon().ifPresent(icon -> builder.icon(() -> icon));

			value.backgroundTexture().ifPresent(builder::backgroundTexture);

			builder.displayItems(value);

			Registry.register(
				BuiltInRegistries.CREATIVE_MODE_TAB,
				key,
				builder.build()
			);
		});

		accessor.setFrozen(true);
	}

	@Override
	protected void apply(Map<Identifier, List<JsonElement>> prepared, SharedState store) {
		HolderLookup.Provider registries = store.get(ResourceLoader.RELOADER_REGISTRY_LOOKUP_KEY);
		var ops = registries.createSerializationContext(JsonOps.INSTANCE);

		Map<Identifier, CreativeModeTabFile> files = new Object2ObjectOpenHashMap<>(prepared.size());

		prepared.forEach((id, jsons) -> {
			for (JsonElement json : jsons) {
				CreativeModeTabFile.CODEC.parse(ops, json).ifSuccess(object -> {
					files.compute(
						id,
						(key, existing) -> existing != null ? existing.merge(object) : object
					);
				}).ifError(error -> LOGGER.error("Couldn't parse data file '{}': {}", id, error));
			}
		});

		for (Map.Entry<Identifier, CreativeModeTabFile> entry : files.entrySet()) {
			var tab = entry.getValue();
			var id = entry.getKey();

			Objects.requireNonNull(tab.title(), "No display name was specified for creative tab '" + id + "'");
			Objects.requireNonNull(tab.icon(), "No icon was specified for creative tab '" + id + "'");
		}

		// In vanilla, adding tabs on the server does nothing, this is just for mod compat.
		apply(files);

		syncPayload = new CreativeModeTabsS2CPayload(files);
	}
}
