package dev.spiritstudios.spectre.api.models.client.animation;

import net.minecraft.resources.Identifier;

public record AnimationLocation(Identifier set, String name) {
	public String toString() {
		return this.set + "#" + this.name;
	}
}
