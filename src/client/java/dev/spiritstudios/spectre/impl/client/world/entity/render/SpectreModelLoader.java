package dev.spiritstudios.spectre.impl.client.world.entity.render;

import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.spectre.api.client.model.Bone;
import dev.spiritstudios.spectre.impl.client.serial.GeoJson;
import dev.spiritstudios.spectre.impl.client.serial.MinecraftGeometry;
import dev.spiritstudios.spectre.impl.client.serial.SerialBone;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectreModelLoader {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static Map<ModelLayerLocation, LayerDefinition> load(ResourceManager manager, String prefix) {
		var lister = new FileToIdConverter(prefix, ".geo.json");

		var resources = lister.listMatchingResourceStacks(manager);
		Map<ModelLayerLocation, LayerDefinition> results = new HashMap<>();

		for (Map.Entry<Identifier, List<Resource>> entry : resources.entrySet()) {
			var id = lister.fileToId(entry.getKey());

			for (Resource resource : entry.getValue()) {
				try (Reader reader = resource.openAsReader()) {
					GeoJson.CODEC.parse(JsonOps.INSTANCE, StrictJsonParser.parse(reader))
						.ifSuccess(geo -> {
							for (MinecraftGeometry geometry : geo.geometry()) {

								Map<String, Bone> bones = new Object2ObjectOpenHashMap<>();
								List<Bone> rootBones = new ArrayList<>();
								for (SerialBone bone : geometry.bones()) {
									bones.put(bone.name(), new Bone(
										bone.name(),
										bone.cubes(),
										bone.pivot(),
										bone.rotation()
									));
								}

								for (SerialBone bone : geometry.bones()) {
									bone.parent()
										.ifPresentOrElse(
											parentName -> {
												var parent = bones.get(parentName);
												var child = bones.get(bone.name());

												parent.children.add(child);
											},
											() -> rootBones.add(bones.get(bone.name()))
										);
								}

								var mesh = new MeshDefinition();
								var rootPart = mesh.getRoot();

								for (Bone bone : rootBones) {
									bone.bake(rootPart, null);
								}

								results.put(
									new ModelLayerLocation(
										lister.fileToId(entry.getKey()),
										geometry.description().id().replaceFirst("geometry.", "")
									),
									LayerDefinition.create(
										mesh,
										geometry.description().textureWidth(),
										geometry.description().textureHeight()
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
