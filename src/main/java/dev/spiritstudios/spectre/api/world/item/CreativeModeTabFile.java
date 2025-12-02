package dev.spiritstudios.spectre.api.world.item;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record CreativeModeTabFile(
	Optional<Component> title,
	Optional<ItemStack> icon,
	Optional<Identifier> backgroundTexture,
	boolean replace,
	List<Either<ItemStack, HolderSet<Item>>> items
) implements CreativeModeTab.DisplayItemsGenerator {
	public static final Codec<CreativeModeTabFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ComponentSerialization.CODEC.optionalFieldOf("title").forGetter(CreativeModeTabFile::title),
		ItemStack.SINGLE_ITEM_CODEC.optionalFieldOf("icon").forGetter(CreativeModeTabFile::icon),
		Identifier.CODEC.optionalFieldOf("background_texture").forGetter(CreativeModeTabFile::backgroundTexture),
		Codec.BOOL.optionalFieldOf("replace", false).forGetter(CreativeModeTabFile::replace),
		Codec.either(
			Codec.withAlternative(
				ItemStack.SINGLE_ITEM_CODEC,
				ItemStack.SIMPLE_ITEM_CODEC
			), // Item ID or item object
			HolderSetCodec.create(
				Registries.ITEM,
				BuiltInRegistries.ITEM.holderByNameCodec(),
				false
			)
		).listOf().fieldOf("items").forGetter(CreativeModeTabFile::items)
	).apply(instance, CreativeModeTabFile::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CreativeModeTabFile> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC), CreativeModeTabFile::title,
		ByteBufCodecs.optional(ItemStack.STREAM_CODEC), CreativeModeTabFile::icon,
		ByteBufCodecs.optional(Identifier.STREAM_CODEC), CreativeModeTabFile::backgroundTexture,
		ByteBufCodecs.either(
			ItemStack.STREAM_CODEC,
			ByteBufCodecs.holderSet(Registries.ITEM)
		).apply(ByteBufCodecs.list()), CreativeModeTabFile::items,
		(title, icon, bg, items) -> new CreativeModeTabFile(title, icon, bg, false, items)
	);

	public CreativeModeTabFile merge(CreativeModeTabFile other) {
		if (this.replace) return this;
		if (other.replace) return other;

		var items = new ArrayList<>(this.items);
		items.addAll(other.items);

		return new CreativeModeTabFile(
			this.title.or(other::title),
			this.icon.or(other::icon),
			this.backgroundTexture.or(other::backgroundTexture),
			false,
			items
		);
	}

	@Override
	public void accept(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		for (Either<ItemStack, HolderSet<Item>> either : items) {
			either
				.ifLeft(output::accept)
				.ifRight(set -> {
					for (Holder<Item> holder : set) {
						output.accept(holder.value());
					}
				});
		}
	}

	public static class Builder {
		private final @Nullable HolderLookup<Item> lookup;

		private @Nullable Component title = null;
		private @Nullable ItemStack icon = null;
		private @Nullable Identifier backgroundTexture = null;
		private boolean replace = false;
		private List<Either<ItemStack, Either<HolderSet<Item>, TagKey<Item>>>> items = new ArrayList<>();

		public Builder(@Nullable HolderLookup<Item> lookup) {
			this.lookup = lookup;
		}

		public Builder() {
			this(null);
		}

		public Builder title(Component title) {
			this.title = title;
			return this;
		}

		public Builder icon(ItemStack icon) {
			this.icon = icon;
			return this;
		}

		public Builder icon(ItemLike icon) {
			return icon(new ItemStack(icon));
		}

		public Builder backgroundTexture(Identifier backgroundTexture) {
			this.backgroundTexture = backgroundTexture;
			return this;
		}

		public Builder replace() {
			this.replace = true;
			return this;
		}

		public Builder add(ItemStack stack) {
			items.add(Either.left(stack));
			return this;
		}

		public Builder add(ItemLike item) {
			return add(new ItemStack(item));
		}

		public Builder add(ItemStack... stacks) {
			for (ItemStack stack : stacks) {
				items.add(Either.left(stack));
			}

			return this;
		}

		public Builder add(ItemLike... items) {
			for (ItemLike item : items) {
				this.items.add(Either.left(new ItemStack(item)));
			}

			return this;
		}

		public Builder add(HolderSet<Item> set) {
			items.add(Either.right(Either.left(set)));
			return this;
		}

		@SafeVarargs
		public final Builder add(HolderSet<Item>... sets) {
			for (HolderSet<Item> set : sets) {
				items.add(Either.right(Either.left(set)));
			}

			return this;
		}

		public Builder add(TagKey<Item> tag) {
			if (lookup == null) {
				throw new UnsupportedOperationException("This builder does not support adding tag keys, add a HolderSet instead.");
			}

			return add(lookup.getOrThrow(tag));
		}

		@SafeVarargs
		public final Builder add(TagKey<Item>... tags) {
			if (lookup == null) {
				throw new UnsupportedOperationException("This builder does not support adding tag keys, add a HolderSet instead.");
			}

			for (TagKey<Item> tag : tags) {
				items.add(Either.right(Either.left(lookup.getOrThrow(tag))));
			}

			return this;
		}

		public CreativeModeTabFile build() {
			return new CreativeModeTabFile(
				Optional.ofNullable(title),
				Optional.ofNullable(icon),
				Optional.ofNullable(backgroundTexture),
				replace,
				items.stream()
					.map(either -> either.mapRight(either2 -> either2.map(
						Function.identity(),
						ignored -> {
							throw new UnsupportedOperationException("Tried to use a TagKey when it was not supported.");
						}
					)))
					.toList()
			);
		}

		public CreativeModeTabFile build(HolderGetter<Item> getter) {
			return new CreativeModeTabFile(
				Optional.ofNullable(title),
				Optional.ofNullable(icon),
				Optional.ofNullable(backgroundTexture),
				replace,
				items.stream()
					.map(
						either -> either.mapRight(either2 -> either2.map(
							Function.identity(),
							getter::getOrThrow
						))
					)
					.toList()
			);
		}
	}
}
