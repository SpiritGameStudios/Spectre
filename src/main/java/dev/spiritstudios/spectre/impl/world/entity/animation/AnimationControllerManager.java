package dev.spiritstudios.spectre.impl.world.entity.animation;

import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.mojank.meow.Variables;
import dev.spiritstudios.mojank.meow.analysis.AnalysisResult;
import dev.spiritstudios.mojank.meow.compile.CompilerFactory;
import dev.spiritstudios.mojank.meow.link.Linker;
import dev.spiritstudios.spectre.api.core.MolangMath;
import dev.spiritstudios.spectre.api.core.math.MolangExpression;
import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.api.world.entity.animation.AnimationController;
import dev.spiritstudios.spectre.api.world.entity.animation.BooleanExpression;
import dev.spiritstudios.spectre.impl.serialization.CompilerOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class AnimationControllerManager extends SimpleResourceReloader<Map<ResourceLocation, Map<String, AnimationControllerDesc>>> {
	public static final Set<AnimationController> CONTROLLERS = Collections.newSetFromMap(new WeakHashMap<>());

	public static final AnimationControllerManager INSTANCE = new AnimationControllerManager();

	private static final Logger LOGGER = LogUtils.getLogger();

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	private static final CompilerFactory<BooleanExpression> FACTORY = new CompilerFactory<>(
		LOOKUP,
		BooleanExpression.class,
		Linker.UNTRUSTED.toBuilder()
			.addAllowedClasses(
				Variables.class,
				Vec3.class,
				Vector4fc.class, Vector4f.class,
				Vector3fc.class, Vector3f.class,
				Vector2fc.class, Vector2f.class,
				MolangExpression.class, BooleanExpression.class, Query.class
			)
			.aliasClass(MolangMath.class, "math")
			.build()
	);

	public static final FileToIdConverter LISTER = new FileToIdConverter("spectre/animation_controllers", ".animation_controllers.json");

	public final Map<ResourceLocation, Map<String, AnimationControllerDesc>> controllers = new Object2ObjectOpenHashMap<>();

	@Override
	protected Map<ResourceLocation, Map<String, AnimationControllerDesc>> prepare(SharedState store) {
		var resources = LISTER.listMatchingResourceStacks(store.resourceManager());

		var compiler = FACTORY.build(new AnalysisResult(
			null,
			null
		));

		var ops = new CompilerOps<>(JsonOps.INSTANCE, compiler, BooleanExpression.class);

		Map<ResourceLocation, Map<String, AnimationControllerDesc>> results = new HashMap<>();

		for (Map.Entry<ResourceLocation, List<Resource>> entry : resources.entrySet()) {
			var id = LISTER.fileToId(entry.getKey());
			var value = results.computeIfAbsent(id, k -> new HashMap<>());

			for (Resource resource : entry.getValue()) {
				try (Reader reader = resource.openAsReader()) {
					AnimationControllersJson.CODEC.parse(ops, StrictJsonParser.parse(reader))
						.ifSuccess(controllers -> value.putAll(controllers.controllers()))
						.ifError(error -> LOGGER.error("Couldn't parse animation controllers file '{}': {}", id, error));
				} catch (IllegalArgumentException | IOException | JsonParseException error) {
					LOGGER.error("Couldn't parse animation controller file '{}': {}", id, error);
				}
			}
		}

		return results;
	}

	@Override
	protected void apply(Map<ResourceLocation, Map<String, AnimationControllerDesc>> prepared, SharedState store) {
		controllers.clear();
		controllers.putAll(prepared);

		for (AnimationController controller : CONTROLLERS) {
			controller.transition(prepared.get(controller.location).get(controller.name).bake());
		}
	}
}
