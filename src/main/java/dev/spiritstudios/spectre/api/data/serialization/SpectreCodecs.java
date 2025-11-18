package dev.spiritstudios.spectre.api.data.serialization;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import dev.spiritstudios.spectre.impl.serialization.MolangCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import dev.spiritstudios.spectre.api.core.math.MolangExpression;
import dev.spiritstudios.spectre.impl.serialization.FuzzyCodec;
import dev.spiritstudios.spectre.impl.serialization.KeyDispatchingCodec;
import org.joml.Vector3f;
import org.joml.Vector4d;

public final class SpectreCodecs {
	private SpectreCodecs() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	private static final Map<String, Integer> COLOR_BY_NAME = Stream.of(ChatFormatting.values())
		.filter(Objects::nonNull)
		.filter(format -> format.getColor() != null)
		.collect(ImmutableMap.toImmutableMap(
			format -> Objects.requireNonNull(format).getName(),
			format -> Objects.requireNonNull(format).getColor()
		));

	public static final Codec<Integer> ARGB = Codec.withAlternative(
		Codec.STRING.comapFlatMap(
			string -> {
				if (string.startsWith("#")) {
					try {
						int color = Integer.parseUnsignedInt(string.substring(1), 16);
						return DataResult.success(color, Lifecycle.stable());
					} catch (NumberFormatException ignored) {
						return DataResult.error(() -> "Invalid color value: " + string);
					}
				} else {
					Integer color = COLOR_BY_NAME.get(string);
					return color == null ? DataResult.error(() -> "Invalid color name: " + string) : DataResult.success(color, Lifecycle.stable());
				}
			},
			color -> "#" + Integer.toHexString(color)
		),
		Codec.INT
	);

	public static final Codec<Vector4d> VECTOR4D = Codec.DOUBLE.listOf()
		.comapFlatMap(
			list -> Util.fixedSize(list, 4)
				.map(vecList -> new Vector4d(vecList.getFirst(), vecList.get(1), vecList.get(2), vecList.get(3))),
			vector -> List.of(vector.x(), vector.y(), vector.z(), vector.w())
		);

	public static final Codec<Vector3f> VECTOR_3F_SIXTEENTH = ExtraCodecs.VECTOR3F.xmap(
		v -> v.div(16F),
		v -> v.mul(16F)
	);

	public static final Codec<MolangExpression> MOLANG = Codec.withAlternative(
		MolangCodec.INSTANCE,
		Codec.FLOAT.flatComapMap(
			v -> (query, variables) -> v,
			function -> DataResult.error(() -> "Cannot encode molang expression.")
		)
	);

	public static <T extends Enum<T>> Codec<T> enumCodec(Class<T> clazz) {
		return Codec.STRING.comapFlatMap(
			string -> {
				try {
					return DataResult.success(Enum.valueOf(clazz, string.toUpperCase()));
				} catch (IllegalArgumentException | NullPointerException e) {
					return DataResult.error(() -> "Value \"%s\" invalid for enum \"%s\"".formatted(string, clazz.getSimpleName()));
				}
			},
			t -> t.name().toLowerCase()
		);
	}

	public static Codec<Integer> clampedRange(int min, int max) {
		return Codec.INT.xmap(
			value -> Math.clamp(value, min, max),
			value -> Math.clamp(value, min, max)
		);
	}

	public static Codec<Float> clampedRange(float min, float max) {
		return Codec.FLOAT.xmap(
			value -> Math.clamp(value, min, max),
			value -> Math.clamp(value, min, max)
		);
	}

	public static Codec<Double> clampedRange(double min, double max) {
		return Codec.DOUBLE.xmap(
			value -> Math.clamp(value, min, max),
			value -> Math.clamp(value, min, max)
		);
	}

	public static <T> MapCodec<T> fuzzy(List<MapCodec<? extends T>> codecs, Function<T, MapEncoder<? extends T>> codecGetter) {
		return new FuzzyCodec<>(codecs, codecGetter);
	}

	/**
	 * Creates a codec that changes depending on the existing keys in a decoded map
	 *
	 * @param defaultCodec   The codec to use for encoding
	 * @param possibleCodecs All codecs that can be returned by {@code dispatcher}
	 * @param dispatcher     A function that returns a codec depending on the inputted map. If this returns a value not contained within {@code possibleCodecs}, an error will be returned.
	 */
	public static <T> MapCodec<T> keyDispatching(MapCodec<T> defaultCodec, List<MapCodec<T>> possibleCodecs, Function<MapLike<?>, MapCodec<T>> dispatcher) {
		return new KeyDispatchingCodec<>(defaultCodec, possibleCodecs, dispatcher);
	}

	public static <A> MapDecoder<Optional<A>> optionalFieldDecoder(String name, Decoder<A> decoder, boolean lenient) {
		return new MapDecoder.Implementation<>() {
			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return Stream.of(ops.createString(name));
			}

			@Override
			public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
				final T value = input.get(name);
				if (value == null) {
					return DataResult.success(Optional.empty());
				}
				final DataResult<A> parsed = decoder.parse(ops, value);
				if (parsed.isError() && lenient) {
					return DataResult.success(Optional.empty());
				}
				return parsed.map(Optional::of).setPartial(parsed.resultOrPartial());
			}
		};
	}

	public static <A> MapDecoder<Optional<A>> optionalFieldDecoder(String name, Decoder<A> decoder) {
		return optionalFieldDecoder(name, decoder, false);
	}

	public static <A> MapDecoder<A> optionalFieldDecoder(String name, Decoder<A> decoder, A defaultValue) {
		return optionalFieldDecoder(name, decoder, false)
			.map(optional -> optional.orElse(defaultValue));
	}
}
