package dev.spiritstudios.spectre.test.models;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class SpectreModelsTestmodInitializer implements ModInitializer {
	public static final EntityDataSerializer<Identifier> IDENTIFIER = EntityDataSerializer.forValueType(Identifier.STREAM_CODEC);

	public static final ResourceKey<EntityType<?>> ENTITY_DISPLAY_KEY = ResourceKey.create(
		Registries.ENTITY_TYPE,
		Identifier.fromNamespaceAndPath("spectre-models-testmod", "entity_display")
	);

	public static EntityType<EntityDisplay> ENTITY_DISPLAY;


	public static final ResourceKey<EntityType<?>> PUMPKIN_KEY = ResourceKey.create(
		Registries.ENTITY_TYPE,
		Identifier.fromNamespaceAndPath("spectre-models-testmod", "pumpkin")
	);

	public static EntityType<PumpkinEntity> PUMPKIN;


	@Override
	public void onInitialize() {
		FabricEntityDataRegistry.register(Identifier.fromNamespaceAndPath("spectre-models-testmod", "identifier"), IDENTIFIER);

		ENTITY_DISPLAY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			ENTITY_DISPLAY_KEY,
			EntityType.Builder.of(
				EntityDisplay::new,
				MobCategory.MISC
			).build(ENTITY_DISPLAY_KEY)
		);

		PUMPKIN = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			PUMPKIN_KEY,
			EntityType.Builder.of(
				PumpkinEntity::new,
				MobCategory.MISC
			).build(PUMPKIN_KEY)
		);

		FabricDefaultAttributeRegistry.register(PUMPKIN, PumpkinEntity.createAttributes());
	}
}
