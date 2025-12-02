package dev.spiritstudios.spectre.mixin.world.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.spiritstudios.spectre.api.world.entity.EntityPart;
import dev.spiritstudios.spectre.api.world.entity.PartHolder;
import dev.spiritstudios.spectre.impl.world.entity.EntityPartLevel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(Level.class)
public abstract class LevelMixin implements EntityPartLevel {
	@Unique
	protected final Int2ObjectMap<EntityPart<?>> specter$parts = new Int2ObjectOpenHashMap<>();

	@Inject(method = "method_47576", at = @At("TAIL"), cancellable = true)
	private static <T extends Entity> void collectEntitiesByTypeLambda(
			Predicate<? super T> predicate,
			List<? super T> result,
			int limit,
			EntityTypeTest<Entity, T> filter,
			Entity entity,
			CallbackInfoReturnable<AbortableIterationConsumer.Continuation> cir
	) {
		if (entity instanceof PartHolder<?> partHolder) {
			for (EntityPart<?> part : partHolder.getSubEntities()) {
				T partCasted = filter.tryCast(part);

				if (partCasted != null && predicate.test(partCasted)) {
					result.add(partCasted);
					if (result.size() >= limit) {
						cir.setReturnValue(AbortableIterationConsumer.Continuation.ABORT);
					}
				}
			}
		}
	}

	@WrapMethod(method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;")
	private List<Entity> getOtherEntities(Entity except, AABB box, Predicate<? super Entity> predicate, Operation<List<Entity>> original) {
		List<Entity> list = original.call(except, box, predicate);

		for (EntityPart<?> part : this.specter$parts.values()) {
			if (part != except && part.getOwner() != except && predicate.test(part) && box.intersects(part.getBoundingBox())) {
				list.add(part);
			}
		}

		return list;
	}

	@Override
	public Int2ObjectMap<EntityPart<?>> specter$getParts() {
		return specter$parts;
	}
}
