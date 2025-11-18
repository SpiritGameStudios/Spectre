package dev.spiritstudios.spectre.api.core.registry.metatag;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class MetatagBuilder<K, V> {
	private final Map<Holder<K>, V> values = new Object2ObjectOpenHashMap<>();
	private boolean replace = false;

	private final @Nullable HolderLookup<K> lookup;
	private final @Nullable ReverseLookup<K> reverseLookup;

	public MetatagBuilder(@Nullable HolderLookup<K> lookup, @Nullable ReverseLookup<K> reverseLookup) {
		this.lookup = lookup;
		this.reverseLookup = reverseLookup;
	}

	public MetatagBuilder() {
		this(null, null);
	}

	public MetatagBuilder<K, V> replace() {
		this.replace = true;
		return this;
	}

	public MetatagBuilder<K, V> put(Holder<K> holder, V value) {
		values.put(holder, value);

		return this;
	}

	public MetatagBuilder<K, V> put(ResourceKey<K> key, V value) {
		if (lookup == null) throw new UnsupportedOperationException();

		values.put(lookup.getOrThrow(key), value);

		return this;
	}


	public MetatagBuilder<K, V> put(K key, V value) {
		if (reverseLookup == null) throw new UnsupportedOperationException();

		var holder = reverseLookup.get(key).orElseThrow(() -> new IllegalArgumentException("Tried to add an unregistered entry to a metatag!"));

		put(holder, value);

		return this;
	}

	public MetatagFile<K, V> build() {
		return new MetatagFile<>(
			values,
			replace
		);
	}

	public interface ReverseLookup<T> {
		Optional<Holder<T>> get(T value);
	}
}
