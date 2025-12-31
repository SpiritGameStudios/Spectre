package dev.spiritstudios.spectre.impl.client.render.model.animation;

import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.mojank.meow.Variables;
import dev.spiritstudios.mojank.meow.analysis.AnalysisResult;
import dev.spiritstudios.mojank.meow.compile.CompilerFactory;
import dev.spiritstudios.mojank.meow.link.Linker;
import dev.spiritstudios.spectre.api.client.model.animation.AnimationLocation;
import dev.spiritstudios.spectre.api.client.model.animation.EntityAnimationSet;
import dev.spiritstudios.spectre.api.client.model.animation.SpectreAnimationDefinition;
import dev.spiritstudios.spectre.api.core.MolangMath;
import dev.spiritstudios.spectre.api.core.math.MolangExpression;
import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.impl.Spectre;
import dev.spiritstudios.spectre.impl.client.serial.AnimationJson;
import dev.spiritstudios.spectre.impl.serialization.CompilerOps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class AnimationManager implements PreparableReloadListener {
	public static final Identifier ID = Spectre.id("animations");

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

	public static final FileToIdConverter LISTER = new FileToIdConverter("spectre/animations/entity", ".animation.json");

	public static Map<AnimationLocation, SpectreAnimationDefinition> load(ResourceManager manager) {
		var resources = LISTER.listMatchingResourceStacks(manager);

		var compiler = FACTORY.build(new AnalysisResult(
			null,
			null
		));

		var ops = new CompilerOps<>(JsonOps.INSTANCE, compiler, MolangExpression.class);

		Map<AnimationLocation, SpectreAnimationDefinition> results = new HashMap<>();

		for (Map.Entry<Identifier, List<Resource>> entry : resources.entrySet()) {
			var id = LISTER.fileToId(entry.getKey());

			for (Resource resource : entry.getValue()) {
				try (Reader reader = resource.openAsReader()) {
					AnimationJson.CODEC.parse(ops, StrictJsonParser.parse(reader))
						.ifSuccess(animations -> {
							animations.animations().forEach((name, animation) -> {
								results.put(
									new AnimationLocation(
										id,
										name.replaceFirst("animation." + id.getPath() + ".", "")
									),
									animation
								);
							});
						})
						.ifError(error -> LOGGER.error("Couldn't parse animations file '{}': {}", id, error));
				} catch (IllegalArgumentException | IOException | JsonParseException error) {
					LOGGER.error("Couldn't parse animation file '{}': {}", id, error);
				}
			}
		}

		return results;
	}

	private EntityAnimationSet entityAnimationSet = EntityAnimationSet.EMPTY;

	@Override
	public CompletableFuture<Void> reload(SharedState state, Executor prepareExecutor, PreparationBarrier barrier, Executor applyExecutor) {
		CompletableFuture<EntityAnimationSet> animationSetFuture = CompletableFuture.supplyAsync(
			() -> EntityAnimationSet.load(state.resourceManager()),
			prepareExecutor
		);

		return animationSetFuture
			.thenApplyAsync(
				ReloadState::new,
				prepareExecutor
			)
			.thenCompose(barrier::wait)
			.thenAcceptAsync(
				this::apply,
				applyExecutor
			);
	}

	private void apply(ReloadState state) {
		this.entityAnimationSet = state.entityAnimationSet;
	}

	public Supplier<EntityAnimationSet> entityAnimations() {
		return () -> this.entityAnimationSet;
	}

	@Environment(EnvType.CLIENT)
	record ReloadState(
		EntityAnimationSet entityAnimationSet
	) {
	}
}
