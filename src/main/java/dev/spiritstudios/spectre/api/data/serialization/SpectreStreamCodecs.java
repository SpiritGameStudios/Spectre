package dev.spiritstudios.spectre.api.data.serialization;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public final class SpectreStreamCodecs {
	private SpectreStreamCodecs() {
		throw new UnsupportedOperationException("Cannot instantiate utility class");
	}

	public static final StreamCodec<ByteBuf, Vec3> VEC3D = ByteBufCodecs.VECTOR3F.map(
		Vec3::new,
		Vec3::toVector3f
	);

	public static <T extends Enum<T>> StreamCodec<ByteBuf, T> enumCodec(Class<T> clazz) {
		T[] values = clazz.getEnumConstants();

		return ByteBufCodecs.VAR_INT.map(
			ordinal -> values[ordinal],
			Enum::ordinal
		);
	}
}
