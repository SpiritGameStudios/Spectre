package dev.spiritstudios.spectre.api.effects.client;

import org.jetbrains.annotations.Range;

public final class SpectreScreenshake {
	/**
	 * The amount of trauma suffered by the camera. Controls the level of screenshake and it's length.
	 *
	 * @apiNote While this is intended to be in the range of zero to one, going above will still function.
	 */
	@Range(from = 0, to = 1)
	public static float trauma = 0F;

	/**
	 * Adds trauma to the camera, and clamps the resulting trauma between zero and one.
	 */
	public static void addTrauma(float amount) {
		trauma = Math.clamp(trauma + amount, 0F, 1F);
	}
}
