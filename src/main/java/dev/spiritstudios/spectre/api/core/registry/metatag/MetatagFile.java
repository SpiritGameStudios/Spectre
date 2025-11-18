package dev.spiritstudios.spectre.api.core.registry.metatag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;

import java.util.Map;

// TODO: Allow using regular tags as keys
public record MetatagFile<K, V>(Map<Holder<K>, V> entries, boolean replace) {
	public static <K, V> Codec<MetatagFile<K, V>> resourceCodecOf(MetatagKey<K, V> metatag) {
		return RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(
				RegistryFixedCodec.create(metatag.registry()),
				metatag.codec()
			).fieldOf("values").forGetter(MetatagFile::entries),
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(MetatagFile::replace)
		).apply(instance, MetatagFile::new));
	}
}
