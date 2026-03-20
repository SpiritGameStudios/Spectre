package dev.spiritstudios.spectre.impl.models.client;

import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectreModelLoader {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static Map<ModelLayerLocation, LayerDefinition> load(ResourceManager manager, String prefix) {
		var lister = new FileToIdConverter(prefix, ".json");

		var resources = lister.listMatchingResourceStacks(manager);
		Map<ModelLayerLocation, LayerDefinition> results = new HashMap<>();

		for (Map.Entry<Identifier, List<Resource>> entry : resources.entrySet()) {
			var id = lister.fileToId(entry.getKey());

			for (Resource resource : entry.getValue()) {
				try (Reader reader = resource.openAsReader()) {
					SpectreModelFile.CODEC.parse(JsonOps.INSTANCE, StrictJsonParser.parse(reader))
						.ifSuccess(file -> {
							for (SpectreLayerDefinition<?, ?> layer : file.layers()) {
								results.put(
									new ModelLayerLocation(
										lister.fileToId(entry.getKey()),
										layer.name()
									),
									LayerDefinition.create(
										layer.mesh(),
										layer.data().textureSize().x(),
										layer.data().textureSize().y()
									)
								);
							}
						})
						.ifError(error -> LOGGER.error("Couldn't parse geometry file '{}': {}", id, error));
				} catch (IllegalArgumentException | IOException | JsonParseException error) {
					LOGGER.error("Couldn't parse geometry file '{}': {}", id, error);
				}
			}
		}

		return results;
	}
}
