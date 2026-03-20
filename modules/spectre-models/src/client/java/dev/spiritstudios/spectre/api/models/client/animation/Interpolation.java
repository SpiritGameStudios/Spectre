package dev.spiritstudios.spectre.api.models.client.animation;

import com.mojang.serialization.Codec;
import dev.spiritstudios.spectre.api.models.client.Query;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

@FunctionalInterface
public interface Interpolation {
	ExtraCodecs.LateBoundIdMapper<Identifier, Interpolation> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();
	Codec<Interpolation> CODEC = ID_MAPPER.codec(Identifier.CODEC);

	void apply(Query query, float delta, SpectreKeyframe[] keyframes, int start, int end, float scale, Vector3f destination);
}
