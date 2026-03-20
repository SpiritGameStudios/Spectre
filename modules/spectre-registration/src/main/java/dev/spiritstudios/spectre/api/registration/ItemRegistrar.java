package dev.spiritstudios.spectre.api.registration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ItemRegistrar extends Registrar<Item> {
	public ItemRegistrar(String namespace) {
		super(BuiltInRegistries.ITEM, namespace);
	}

	public <T extends Item> T registerItem(String path, Function<Item.Properties, T> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(
			registry.key(),
			Identifier.fromNamespaceAndPath(namespace, path)
		);

		properties.setId(key);

		T item = factory.apply(properties);

		Registry.register(
			registry,
			key,
			item
		);

		return item;
	}

	public Item registerItem(String path, Item.Properties properties) {
		return registerItem(path, Item::new, properties);
	}

	public Item registerItem(String path) {
		return registerItem(path, Item::new, new Item.Properties());
	}

	public SpawnEggItem registerSpawnEgg(EntityType<?> type) {
		return registerItem(
			EntityType.getKey(type).getPath() + "_spawn_egg",
			SpawnEggItem::new,
			new Item.Properties().spawnEgg(type)
		);
	}

	public BlockItem registerBlock(Block block) {
		return registerBlock(block, BlockItem::new);
	}

	public BlockItem registerBlock(Block block, Item.Properties properties) {
		return registerBlock(block, BlockItem::new, properties);
	}

	public <T extends Item> T registerBlock(Block block, BiFunction<Block, Item.Properties, T> factory) {
		return registerBlock(block, factory, new Item.Properties());
	}

	@SuppressWarnings("deprecation")
	public <T extends Item> T registerBlock(
		Block block,
		BiFunction<Block, Item.Properties, T> factory,
		Item.Properties properties
	) {
		return registerItem(
			block.builtInRegistryHolder().key().identifier().getPath(),
			p -> factory.apply(block, p),
			properties.useBlockDescriptionPrefix()
		);
	}
}
