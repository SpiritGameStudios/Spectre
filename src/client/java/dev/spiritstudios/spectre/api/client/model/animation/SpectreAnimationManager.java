package dev.spiritstudios.spectre.api.client.model.animation;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.mojank.meow.Variables;
import dev.spiritstudios.mojank.meow.analysis.AnalysisResult;
import dev.spiritstudios.mojank.meow.compile.CompilerFactory;
import dev.spiritstudios.mojank.meow.compile.Linker;
import dev.spiritstudios.spectre.api.client.model.serial.ActorAnimation;
import dev.spiritstudios.spectre.api.core.math.MolangExpression;
import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.impl.serialization.CompilerOps;
import dev.spiritstudios.spectre.impl.client.animation.MolangMath;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleResourceReloader;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpectreAnimationManager extends SimpleResourceReloader<Map<ResourceLocation, JsonElement>> {
	private static final Logger LOGGER = LogUtils.getLogger();

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	private static final CompilerFactory<MolangExpression> FACTORY = new CompilerFactory<>(
		LOOKUP,
		MolangExpression.class,
		Linker.UNTRUSTED.toBuilder()
			.addAllowedClasses(
				Variables.class,
				Vec3.class,
				Vector4fc.class, Vector4f.class,
				Vector3fc.class, Vector3f.class,
				Vector2fc.class, Vector2f.class,
				MolangExpression.class, Query.class
			)
			.aliasClass(MolangMath.class, "math")
			.build()
	);

	public static final FileToIdConverter FINDER = new FileToIdConverter("spectre/animations", ".animation.json");

	public final Map<String, ActorAnimation> animations = new Object2ObjectOpenHashMap<>();

	@Override
	protected Map<ResourceLocation, JsonElement> prepare(SharedState store) {
		var resources = FINDER.listMatchingResources(store.resourceManager());

		Map<ResourceLocation, JsonElement> results = new HashMap<>();

		for (Map.Entry<ResourceLocation, Resource> resource : resources.entrySet()) {
			try (Reader reader = resource.getValue().openAsReader()) {
				var element = StrictJsonParser.parse(reader);

				results.put(resource.getKey(), element);

			} catch (IllegalArgumentException | IOException | JsonParseException error) {
				LOGGER.error("Couldn't parse data file '{}': {}", resource.getKey(), error);
			}
		}

		return results;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> prepared, SharedState store) {
		var compiler = FACTORY.build(new AnalysisResult(
			null,
			null
		));

//		var registries = store.get(ResourceLoader.RELOADER_REGISTRY_LOOKUP_KEY);
		var ops = new CompilerOps<>(JsonOps.INSTANCE, compiler);

		Map<String, ActorAnimation> results = new HashMap<>();

		prepared.forEach((id, json) -> {
			var map = ops.getMap(json).getOrThrow();
			var animations = ops.getMap(map.get("animations")).getOrThrow();
			animations.entries()
				.forEach(entry -> {
					var name = ops.getStringValue(entry.getFirst()).getOrThrow();
					ActorAnimation.CODEC.parse(
							ops,
							entry.getSecond()
						)
						.ifSuccess(anim -> results.put(name, anim))
						.ifError(error -> LOGGER.error("Couldn't parse data file '{}': {}", id, error));
				});
		});

		animations.clear();
		animations.putAll(results);
	}
}
