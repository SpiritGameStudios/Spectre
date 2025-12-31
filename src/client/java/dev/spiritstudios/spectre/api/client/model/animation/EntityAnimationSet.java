package dev.spiritstudios.spectre.api.client.model.animation;

import dev.spiritstudios.spectre.impl.client.render.model.animation.AnimationManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Map;

public class EntityAnimationSet {
	public static final EntityAnimationSet EMPTY = new EntityAnimationSet(Map.of());
	private final Map<AnimationLocation, SpectreAnimationDefinition> definitions;

	public SpectreKeyframeAnimation bake(AnimationLocation location, ModelPart part) {
		SpectreAnimationDefinition definition = this.definitions.get(location);
		if (definition == null) {
			throw new IllegalArgumentException("No animation with location " + location);
		} else {
			return definition.bake(part);
		}
	}

	public EntityAnimationSet(Map<AnimationLocation, SpectreAnimationDefinition> definitions) {
		this.definitions = definitions;
	}

	public static EntityAnimationSet load(ResourceManager manager) {
		return new EntityAnimationSet(AnimationManager.load(manager));
	}
}
