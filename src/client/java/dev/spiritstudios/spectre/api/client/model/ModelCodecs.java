package dev.spiritstudios.spectre.api.client.model;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class ModelCodecs {
	public static final Codec<Vector3fc> ROTATION_VECTOR = ExtraCodecs.VECTOR3F
		.xmap(
			vec -> vec.mul(-Mth.DEG_TO_RAD, -Mth.DEG_TO_RAD, Mth.DEG_TO_RAD, new Vector3f()),
			vec -> vec.mul(-Mth.RAD_TO_DEG, -Mth.RAD_TO_DEG, Mth.RAD_TO_DEG, new Vector3f())
		);
}
