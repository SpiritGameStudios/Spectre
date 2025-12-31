package dev.spiritstudios.spectre.impl.client.pond;

import dev.spiritstudios.spectre.impl.client.serial.Face;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public interface SpectreCubeDef {
	ThreadLocal<@Nullable Map<Direction, Face>> FACE_UV = new ThreadLocal<>();

	default void spectre$setFaceUv(Map<Direction, Face> faceUV) {
		throw new IllegalStateException("Implemented via mixin");
	}
}
