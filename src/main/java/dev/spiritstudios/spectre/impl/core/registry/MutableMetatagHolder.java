package dev.spiritstudios.spectre.impl.core.registry;

import dev.spiritstudios.spectre.api.core.registry.metatag.MetatagKey;
import net.minecraft.core.Holder;

public interface MutableMetatagHolder<T> {
	@SuppressWarnings("unchecked")
	static <T> MutableMetatagHolder<T> cast(Holder.Reference<T> reference) {
		return (MutableMetatagHolder<T>) reference;
	}

	void spectre$clearMetatags();
	<V> void spectre$putMetatag(MetatagKey<T, V> metatag, V value);
}
