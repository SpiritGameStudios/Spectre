package dev.spiritstudios.spectre.api.core.registry.metatag;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import java.util.function.BinaryOperator;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public interface MetatagKey<K, V> {
	ResourceKey<? extends Registry<K>> registry();

	Codec<V> codec();

	@Nullable StreamCodec<RegistryFriendlyByteBuf, V> packetCodec();

	V merge(V a, V b);

	final class Builder<K, V> {
		private final ResourceKey<? extends Registry<K>> registry;
		private final Codec<V> codec;

		private @Nullable StreamCodec<RegistryFriendlyByteBuf, V> packetCodec = null;
		private BinaryOperator<V> merger = (a, b) -> a;

		public Builder(ResourceKey<? extends Registry<K>> registry, Codec<V> codec) {
			this.registry = registry;
			this.codec = codec;
		}

		public Builder(Registry<K> registry, Codec<V> codec) {
			this(registry.key(), codec);
		}

		public Builder<K, V> sync(StreamCodec<RegistryFriendlyByteBuf, V> codec) {
			this.packetCodec = codec;
			return this;
		}

		public Builder<K, V> merger(BinaryOperator<V> merger) {
			this.merger = merger;
			return this;
		}

		public MetatagKey<K, V> build() {
			return new SimpleMetatag<>(
				registry,
				codec,
				packetCodec,
				merger
			);
		}

		private record SimpleMetatag<K, V>(
			ResourceKey<? extends Registry<K>> registry,
			Codec<V> codec,
			@Nullable StreamCodec<RegistryFriendlyByteBuf, V> packetCodec,
			BinaryOperator<V> merger
		) implements MetatagKey<K, V> {
			// Record would usually override these to make it act like a value
			@Override
			public boolean equals(Object obj) {
				return obj == this;
			}

			@Override
			public int hashCode() {
				return System.identityHashCode(this);
			}

			@Override
			public V merge(V a, V b) {
				return merger.apply(a, b);
			}
		}
	}
}
