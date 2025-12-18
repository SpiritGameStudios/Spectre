package dev.spiritstudios.spectre.mixin.client.world.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.spiritstudios.spectre.impl.client.world.entity.render.SpectreModelLoader;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {
	@WrapOperation(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", ordinal = 0))
	private static CompletableFuture<EntityModelSet> provideModelSet(
		Supplier<EntityModelSet> vanilla,
		Executor executor,
		Operation<CompletableFuture<EntityModelSet>> original,
		@Local(argsOnly = true) PreparableReloadListener.SharedState state
	) {
		return original.call(vanilla, executor)
			.thenApply(previous -> {
				var data = SpectreModelLoader.load(
					state.resourceManager(),
					"spectre/models/entity"
				);

				Map<ModelLayerLocation, LayerDefinition> newRoots = new HashMap<>(((EntityModelSetAccessor) previous).getRoots());

				// data after so we can theoretically replace vanilla models
				newRoots.putAll(data);

				return new EntityModelSet(newRoots);
			});
	}
}
