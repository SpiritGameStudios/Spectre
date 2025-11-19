package dev.spiritstudios.spectre.api.core;

import dev.spiritstudios.mojank.meow.binding.Pure;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.NotImplementedException;

public final class MolangMath {
	public static final float tau = Mth.TWO_PI;
	public static final float pi = Mth.PI;

	@Pure
	public static float abs(float value) {
		return Math.abs(value);
	}

	@Pure
	public static float acos(float value) {
		return (float) Math.acos(value);
	}

	@Pure
	public static float asin(float value) {
		return (float) Math.asin(value);
	}

	@Pure
	public static float atan(float value) {
		return (float) Math.atan(value);
	}

	@Pure
	public static float atan2(float y, float x) {
		return (float) Mth.atan2(y, x);
	}

	@Pure
	public static float ceil(float value) {
		return Mth.ceil(value);
	}

	@Pure
	public static float clamp(float value, float min, float max) {
		return Mth.clamp(value, min, max);
	}

	@Pure
	public static float copy_sign(float a, float b) {
		return Math.copySign(a, b);
	}

	@Pure
	public static float cos(float value) {
		return Mth.cos(value * Mth.DEG_TO_RAD);
	}

	public static float die_roll(float num, float low, float high) {
		// TODO
		throw new NotImplementedException();
	}

	public static int die_roll_integer(float num, float low, float high) {
		// TODO
		throw new NotImplementedException();
	}

	public static float random(float low, float high) {
		// TODO
		float min = Math.min(low, high);
		float max = Math.max(low, high);

		return min + (float) Math.random() * (max - min);
	}

	// What the fuck
	public static float ease_out_bounce(float delta) {
		if (delta < (1F / 2.75F)) {
			return 7.5625F * delta * delta;
		} else if (delta < (2F / 2.75F)) {
			float d2 = delta - (1.5F / 2.75F);
			return (7.5625F * d2 * d2) + 0.75F;
		} else if (delta < (2.5F / 2.75F)) {
			float d2 = delta - (2.25F / 2.75F);
			return (7.5625F * d2 * d2) + 0.9375F;
		} else {
			float d2 = delta - (2.625F / 2.75F);
			return (7.5625F * d2 * d2) + 0.984375F;
		}
	}


	public static float ease_in_back(float start, float end, float delta) {
		return Mth.lerp(
			delta * delta * ((1.70158F + 1) * delta - 1.70158F),
			start, end
		);
	}

	public static float ease_in_bounce(float start, float end, float delta) {
		return Mth.lerp(
			1F - ease_out_bounce(1F - delta),
			start, end
		);
	}

	public static float ease_in_circ(float start, float end, float delta) {
		return Mth.lerp(
			-1F * (Mth.sqrt(1F - delta * delta) - 1F),
			start, end
		);
	}

	public static float ease_in_cubic(float start, float end, float delta) {
		return Mth.lerp(
			delta * delta * delta,
			start, end
		);
	}

	public static float ease_in_elastic(float start, float end, float delta) {
		final float p = 1 - 0.7F;
		final float s = p / Mth.TWO_PI * Mth.HALF_PI; // orig source had half pi as asin(1)

		return Mth.lerp(
			(delta == 0F || delta == 1F) ? delta : -(
				(float) Math.pow(2F, 10F * (delta - 1F)) *
					Mth.sin(((delta - 1F) - s) * Mth.TWO_PI / p)
			),
			start, end
		);
	}

	public static float ease_in_expo(float start, float end, float delta) {
		return Mth.lerp(
			delta == 0F ? 0F : (float) Math.pow(2F, 10F * (delta - 1F)),
			start, end
		);
	}

	public static float ease_in_out_back(float start, float end, float delta) {
		float d2 = delta * 2F;
		float d22 = d2 - 2;
		final float s = 1.70158F * 1.525F;

		return Mth.lerp(
			d2 < 1F ?
				0.5F * d2 * d2 * (
					((s + 1) * d2) - s
				) :
				0.5F * (
					d22 * d22 * ((s + 1) * d22 + s) + 2
				),
			start, end
		);
	}

	public static float ease_in_out_bounce(float start, float end, float delta) {
		return Mth.lerp(
			delta < 0.5F ? (1F - ease_out_bounce(1F - (delta * 2F))) * 0.5F :
				(ease_out_bounce((delta * 2F) - 1F) * 0.5F) + 0.5F,
			start, end
		);
	}

	public static float ease_in_out_circ(float start, float end, float delta) {
		float d2 = delta * 2F;
		float d22 = d2 - 2F;

		return Mth.lerp(
			d2 < 1F ?
				-0.5F * (Mth.sqrt(1F - d2 * d2) - 1F) :
				0.5F * (Mth.sqrt(1F - d22 * d22) + 1F),
			start, end
		);
	}

	public static float ease_out_back(float start, float end, float delta) {
		delta = delta - 1;

		return Mth.lerp(
			delta * delta * ((1.70158F + 1) * delta + 1.70158F) + 1F,
			start, end
		);
	}

	// todo: finish easings

	@Pure
	public static float exp(float value) {
		return (float) Math.exp(value);
	}

	@Pure
	public static float floor(float value) {
		return Mth.floor(value);
	}

	@Pure
	public static float hermite_blend(float value) {
		return (3F * value * value) - (2F * value * value * value);
	}

	@Pure
	public static float inverse_lerp(float start, float end, float value) {
		return (value - start) / (end - start);
	}

	@Pure
	public static float lerp(float start, float end, float delta) {
		return Mth.lerp(delta, start, end);
	}

	@Pure
	public static float lerp_rotate(float start, float end, float delta) {
		float a = (((start + 180) % 360) + 180) % 360;
		float b = (((end + 180) % 360) + 180) % 360;

		if (a > b) {
			var oA = a;
			a = b;
			b = oA;
		}

		float difference = b - a;
		return difference > 180F ?
			((((b + delta * (360F - difference)) + 180) % 360) + 180) % 360 :
			a + delta * difference;
	}

	@Pure
	public static float ln(float value) {
		return (float) Math.log(value);
	}

	@Pure
	public static float max(float a, float b) {
		return Math.max(a, b);
	}

	@Pure
	public static float min(float a, float b) {
		return Math.min(a, b);
	}

	@Pure
	public static float min_angle(float value) {
		float floor = Math.max(4F, 4F + Math.round(value / -360F)) * 2F + 1F;
		return ((value + 180F * floor) % 360F) - 180F;
	}

	@Pure
	public static float mod(float a, float b) {
		return a % b;
	}

	@Pure
	public static float pow(float base, float exponent) {
		return (float) Math.pow(base, exponent);
	}

	@Pure
	public static float round(float value) {
		return Math.round(value);
	}

	@Pure
	public static float sign(float value) {
		return Mth.sign(value);
	}

	@Pure
	public static float sin(float x) {
		return Mth.sin(x * Mth.DEG_TO_RAD);
	}

	@Pure
	public static float sqrt(float value) {
		return Mth.sqrt(value);
	}

	@Pure
	public static float trunc(float value) {
		return (int) value;
	}
}
