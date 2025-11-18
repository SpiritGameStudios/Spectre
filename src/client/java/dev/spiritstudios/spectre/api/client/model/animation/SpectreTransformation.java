package dev.spiritstudios.spectre.api.client.model.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.spiritstudios.spectre.api.client.model.serial.LerpMode;
import dev.spiritstudios.spectre.api.core.exception.ImpossibleException;
import dev.spiritstudios.spectre.api.core.math.MolangContext;
import dev.spiritstudios.spectre.api.core.math.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.util.Mth;

public record SpectreTransformation(SpectreKeyframe... keyframes) {
	public static final SpectreTransformation EMPTY = new SpectreTransformation();

	public static final Codec<SpectreTransformation> CODEC = codec(false);
	public static final Codec<SpectreTransformation> CODEC_SIXTEENTH = codec(true);

	private static Codec<SpectreTransformation> codec(boolean rotation) {
		return Codec.withAlternative(
			Codec.dispatchedMap(
				// Json stores all string keys as strings so we need to do this
				Codec.STRING.comapFlatMap(
					str -> {
						try {
							return DataResult.success(Float.parseFloat(str));
						} catch (NumberFormatException e) {
							return DataResult.error(() -> "Not a valid number: " + str);
						}
					},
					Object::toString
				),
				f -> SpectreKeyframe.codec(f, rotation)
			).xmap(
				map -> new SpectreTransformation(
					map.values()
						.stream()
						.sorted(Comparator.comparing(SpectreKeyframe::timestamp))
						.toArray(SpectreKeyframe[]::new)
				),
				trans -> Arrays.stream(trans.keyframes)
					.collect(Collectors.toMap(
						SpectreKeyframe::timestamp,
						Function.identity()
					))
			),
			(rotation ? Vector3fExpression.CODEC_SIXTEENTH : Vector3fExpression.CODEC).xmap(
				exp -> new SpectreTransformation(
					new SpectreKeyframe(
						0F,
						exp, exp,
						LerpMode.LINEAR
					)
				),
				trans -> {
					throw new ImpossibleException();
				}
			)
		);
	}

	private static final Logger LOGGER = LogManager.getLogger(SpectreTransformation.class);

	public void apply(
		Query query,
		LoopType loop,
		float runningSeconds,
		float scale,
		Vector3f destination
	) {
		if (keyframes.length == 0) return;

		var search = Mth.binarySearch(0, this.keyframes.length, index -> runningSeconds <= this.keyframes[index].timestamp());

		int start;
		int end;

		SpectreKeyframe startFrame;
		SpectreKeyframe endFrame;

		if (loop == LoopType.TRUE) {
			start = search - 1 < 0 ? keyframes.length - 1 : search - 1;
			end = start + 1 == keyframes.length ? 0 : start + 1;

			startFrame = this.keyframes[start];
			endFrame = this.keyframes[end];

			if (startFrame.timestamp() < endFrame.timestamp()) {
				var temp = startFrame;
				startFrame = endFrame;
				endFrame = temp;

				var temp2 = start;
				start = end;
				end = temp2;
			}
		} else {
			start = Math.max(0, search - 1);
			end = Math.min(this.keyframes.length - 1, start + 1);

			startFrame = this.keyframes[start];
			endFrame = this.keyframes[end];
		}

		float delta = end != start ?
			(runningSeconds - startFrame.timestamp()) / (endFrame.timestamp() - startFrame.timestamp()) :
			0.0F;

		query.anim_time = runningSeconds;

		endFrame.lerpMode().apply(query, destination, delta, this.keyframes, start, end, scale);
	}
}
