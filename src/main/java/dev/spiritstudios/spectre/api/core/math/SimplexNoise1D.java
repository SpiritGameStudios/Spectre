package dev.spiritstudios.spectre.api.core.math;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SimplexNoise1D {
	private final byte[] permutation;

	public SimplexNoise1D(RandomSource random) {
		this.permutation = new byte[256];

		// Vanilla uses this for its perlin noise, some sort of shuffling algo
		for (int i = 0; i < 256; i++) {
			this.permutation[i] = (byte)i;
		}

		for (int i = 0; i < 256; i++) {
			int j = random.nextInt(256 - i);
			byte b = this.permutation[i];
			this.permutation[i] = this.permutation[i + j];
			this.permutation[i + j] = b;
		}
	}

	private static float grad(byte hash, float x) {
		int h = hash & 0x0F;
		float grad = 1F + (h & 7);
		if ((h & 8) != 0) grad = -grad;
		return (grad * x);
	}

	public float sample(float x) {
		int loI = Mth.floor(x);
		int hiI = loI + 1;

		float loDist = x - loI;
		float hiDist = loDist - 1F;

		float lo = 1F - loDist * loDist;
		lo *= lo;
		lo = lo * lo * grad(permutation[loI & 0xFF], loDist);

		float hi = 1F - hiDist * hiDist;
		hi *= hi;
		hi = hi * hi * grad(permutation[hiI & 0xFF], hiDist);

		return 0.395F * (lo + hi);
	}
}
