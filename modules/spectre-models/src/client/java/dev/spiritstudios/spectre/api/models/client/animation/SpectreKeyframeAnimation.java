package dev.spiritstudios.spectre.api.models.client.animation;

import com.mojang.logging.LogUtils;
import dev.spiritstudios.spectre.api.models.client.Query;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SpectreKeyframeAnimation {
	private static final Logger LOGGER = LogUtils.getLogger();

	private final SpectreAnimationDefinition definition;
	private final List<Entry> entries;

	public static SpectreKeyframeAnimation bake(ModelPart root, SpectreAnimationDefinition definition) {
		List<Entry> entries = new ArrayList<>();
		Function<String, ModelPart> function = root.createPartLookup();

		for (var entry : definition.boneAnimations().entrySet()) {
			String partName = entry.getKey();
			List<SpectreAnimationChannel> channels = entry.getValue();

			ModelPart part = function.apply(partName);
			if (part == null) {
				LOGGER.warn("Cannot animate {}, which does not exist in model", partName);
				continue;
			}

			for (SpectreAnimationChannel channel : channels) {
				entries.add(new Entry(part, channel.target(), channel.keyframes()));
			}
		}

		return new SpectreKeyframeAnimation(definition, List.copyOf(entries));
	}

	private SpectreKeyframeAnimation(SpectreAnimationDefinition definition, List<Entry> entries) {
		this.definition = definition;
		this.entries = entries;
	}

	public void applyWalk(Query query, float walkAnimationPos, float walkAnimationSpeed, float timeMultiplier, float speedMultiplier) {
		this.apply(query, walkAnimationPos * timeMultiplier, Math.min(walkAnimationSpeed * speedMultiplier, 1.0F));
	}

	public void apply(Query query, float timeInTicks, float scale) {
		float elapsedSeconds = this.getElapsedSeconds(timeInTicks);
		query.anim_time = elapsedSeconds;

		Vector3f scratchVector = new Vector3f();

		for (Entry entry : this.entries) {
			entry.apply(query, elapsedSeconds, scale, definition, scratchVector);
		}
	}

	private float getElapsedSeconds(float timeInTicks) {
		float seconds = timeInTicks / 20F;

		return this.definition.loop() == LoopType.TRUE ? seconds % this.definition.lengthInSeconds() : seconds;
	}

	record Entry(ModelPart part, AnimationChannel.Target target, SpectreKeyframe[] keyframes) {
		public void apply(Query query, float elapsedSeconds, float scale, SpectreAnimationDefinition definition, Vector3f scratchVector) {
			if (keyframes.length == 0) return;

			var search = Mth.binarySearch(0, this.keyframes.length, index -> elapsedSeconds <= this.keyframes[index].timestamp());

			int start;
			int end;

			SpectreKeyframe startFrame;
			SpectreKeyframe endFrame;

			if (definition.loop() == LoopType.TRUE) {
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
				(elapsedSeconds - startFrame.timestamp()) / (endFrame.timestamp() - startFrame.timestamp()) :
				0.0F;

			endFrame.interpolation().apply(query, delta, this.keyframes, start, end, scale, scratchVector);
			target.apply(part, scratchVector);
		}
	}
}
