package dev.spiritstudios.spectre.impl.models.client.animation;

import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import dev.spiritstudios.spectre.api.models.client.animation.AnimationLocation;
import dev.spiritstudios.spectre.api.models.client.animation.EntityAnimationSet;
import dev.spiritstudios.spectre.api.models.client.animation.SpectreAnimationDefinition;
import dev.spiritstudios.spectre.impl.models.client.serial.AnimationJson;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class AnimationLoader implements PreparableReloadListener {
	public static final Identifier ID = Identifier.fromNamespaceAndPath("spectre-models", "animations");

	private static final Logger LOGGER = LogUtils.getLogger();

	public static final FileToIdConverter LISTER = new FileToIdConverter("spectre/animations/entity", ".animation.json");

	public AnimationLoader() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(AnimationLoader.ID, this);
		ResourceLoader.get(PackType.CLIENT_RESOURCES).addListenerOrdering(
			AnimationLoader.ID,
			ResourceReloaderKeys.Client.MODELS
		);
	}

	public static Map<AnimationLocation, SpectreAnimationDefinition> load(ResourceManager manager) {
		var resources = LISTER.listMatchingResourceStacks(manager);

		Map<AnimationLocation, SpectreAnimationDefinition> results = new HashMap<>();

		for (Map.Entry<Identifier, List<Resource>> entry : resources.entrySet()) {
			var id = LISTER.fileToId(entry.getKey());

			for (Resource resource : entry.getValue()) {
				try (Reader reader = resource.openAsReader()) {
					AnimationJson.CODEC.parse(JsonOps.INSTANCE, StrictJsonParser.parse(reader))
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
