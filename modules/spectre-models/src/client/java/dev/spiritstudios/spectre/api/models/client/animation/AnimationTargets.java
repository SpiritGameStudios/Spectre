package dev.spiritstudios.spectre.api.models.client.animation;

import com.mojang.serialization.Codec;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

public final class AnimationTargets {
	public static final ExtraCodecs.LateBoundIdMapper<Identifier, AnimationChannel.Target> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();
	public static final Codec<AnimationChannel.Target> CODEC = ID_MAPPER.codec(Identifier.CODEC);

	static {
		ID_MAPPER.put(Identifier.withDefaultNamespace("position"), AnimationChannel.Targets.POSITION);
		ID_MAPPER.put(Identifier.withDefaultNamespace("rotation"), (part, rot) -> {
			part.offsetRotation(rot.mul(Mth.DEG_TO_RAD));
		});
		ID_MAPPER.put(Identifier.withDefaultNamespace("scale"), AnimationChannel.Targets.SCALE);
	}
}
