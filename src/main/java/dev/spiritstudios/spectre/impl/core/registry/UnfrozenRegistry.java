package dev.spiritstudios.spectre.impl.core.registry;

import net.minecraft.resources.ResourceKey;


// Could potentially be worth making this a public api? This is extremely unsafe though and it really shouldn't be used
// outside a few rare circumstances
public interface UnfrozenRegistry<T> {
	void specter$remove(ResourceKey<T> key);
}
