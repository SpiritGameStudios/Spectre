package dev.spiritstudios.spectre.mixin.registry.metatag;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.spiritstudios.spectre.api.core.registry.metatag.SpectreMetatags;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VillagerType.class)
public abstract class VillagerTypeMixin {
	@WrapMethod(method = "byBiome")
	private static ResourceKey<VillagerType> addMetatag(Holder<Biome> biomeEntry, Operation<ResourceKey<VillagerType>> original) {
		return biomeEntry.getData(SpectreMetatags.VILLAGER_TYPE)
			.flatMap(Holder::unwrapKey)
			.orElseGet(() -> original.call(biomeEntry));
	}
}
