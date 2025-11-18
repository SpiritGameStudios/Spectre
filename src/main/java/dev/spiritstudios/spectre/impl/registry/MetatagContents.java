package dev.spiritstudios.spectre.impl.registry;

import dev.spiritstudios.spectre.api.core.exception.ImpossibleException;
import dev.spiritstudios.spectre.api.core.registry.metatag.MetatagKey;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;

public record MetatagContents<K, V>(MetatagKey<K, V> metatag, Map<Holder<K>, V> entries) {
	public static void apply(HolderLookup.Provider registries, List<MetatagContents<?, ?>> contents) {
		registries.listRegistries().flatMap(HolderLookup::listElements).forEach(entry -> {
			MutableMetatagHolder.cast(entry).spectre$clearMetatags();
		});

		contents.forEach(MetatagContents::apply);
	}

	private void apply() {
		entries.forEach((entry, value) -> {
			if (!(entry instanceof Holder.Reference<K> reference)) {
				// I'm fairly sure this is impossible, but in case it isn't:
				// You could probably fix this by looking up the direct entry's key
				// in the global registries
				throw new ImpossibleException();
			}

			var holder = MutableMetatagHolder.cast(reference);
			holder.spectre$putMetatag(metatag, value);
		});
	}
}
