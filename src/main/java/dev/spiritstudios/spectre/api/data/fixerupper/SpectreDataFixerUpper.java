package dev.spiritstudios.spectre.api.data.fixerupper;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import dev.spiritstudios.spectre.impl.dfu.SpectreDataFixerUpperImpl;

public final class SpectreDataFixerUpper {
	public static void register(
		String modId,
		int currentDataVersion,
		DataFixer dataFixer
	) {
		SpectreDataFixerUpperImpl.get().register(modId, currentDataVersion, dataFixer);
	}

	public static Schema createRootSchema() {
		return SpectreDataFixerUpperImpl.get().createRootSchema();
	}
}
