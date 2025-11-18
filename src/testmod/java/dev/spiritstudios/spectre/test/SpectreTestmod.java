package dev.spiritstudios.spectre.test;

import com.mojang.serialization.Codec;
import dev.spiritstudios.spectre.api.core.registry.metatag.MetatagKey;
import dev.spiritstudios.spectre.api.core.registry.SpectreRegistries;
import dev.spiritstudios.spectre.impl.Spectre;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class SpectreTestmod implements ModInitializer {
	public static final MetatagKey<Block, Integer> TEST_METATAG = new MetatagKey.Builder<>(Registries.BLOCK, Codec.INT)
		.sync(ByteBufCodecs.INT.cast())
		.merger(Integer::sum)
		.build();

	public static final ResourceKey<EntityType<?>> BLOBABO_KEY = ResourceKey.create(
		Registries.ENTITY_TYPE,
		Spectre.id("blobabo")
	);

	public static final EntityType<Blobabo> BLOBABO = EntityType.Builder.of(
		Blobabo::new,
		MobCategory.MISC
	).build(BLOBABO_KEY);

	@Override
	public void onInitialize() {
		Registry.register(
			SpectreRegistries.METATAG,
			Spectre.id("test_metatag"),
			TEST_METATAG
		);

		Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			BLOBABO_KEY,
			BLOBABO
		);

		FabricDefaultAttributeRegistry.register(
			BLOBABO,
			PathfinderMob.createMobAttributes()
		);
	}
}
