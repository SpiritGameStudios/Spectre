package dev.spiritstudios.spectre.api.registration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class BlockRegistrar extends Registrar<Block> {
	public BlockRegistrar(String namespace) {
		super(BuiltInRegistries.BLOCK, namespace);
	}

	public <T extends Block> T register(
		String path,
		Function<BlockBehaviour.Properties, T> factory,
		BlockBehaviour.Properties properties
	) {
		var key = ResourceKey.create(
			registry.key(),
			Identifier.fromNamespaceAndPath(namespace, path)
		);

		T block = factory.apply(properties.setId(key));

		return Registry.register(registry, key, block);
	}

	public Block register(
		String path,
		BlockBehaviour.Properties properties
	) {
		return register(path, Block::new, properties);
	}

	@SuppressWarnings("deprecation")
	public StairBlock registerStair(String id, Block base) {
		return register(
			id,
			properties -> new StairBlock(base.defaultBlockState(), properties),
			BlockBehaviour.Properties.ofLegacyCopy(base)
		);
	}

	public StairBlock registerFullStair(String id, Block base) {
		return register(
			id,
			properties -> new StairBlock(base.defaultBlockState(), properties),
			BlockBehaviour.Properties.ofFullCopy(base)
		);
	}

	@SuppressWarnings("deprecation")
	public SlabBlock registerSlab(String id, Block base) {
		return register(id, SlabBlock::new, BlockBehaviour.Properties.ofLegacyCopy(base));
	}

	public SlabBlock registerFullSlab(String id, Block base) {
		return register(id, SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(base));
	}
}
