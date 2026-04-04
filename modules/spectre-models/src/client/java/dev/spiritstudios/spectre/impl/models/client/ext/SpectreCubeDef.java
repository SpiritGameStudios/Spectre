package dev.spiritstudios.spectre.impl.models.client.ext;

import dev.spiritstudios.spectre.impl.models.client.serial.Face;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public interface SpectreCubeDef {
	ScopedValue<@Nullable Map<Direction, Face>> FACE_UV = ScopedValue.newInstance();

	default void spectre$setFaceUv(Map<Direction, Face> faceUV) {
		throw new IllegalStateException("Implemented via mixin");
	}
}
