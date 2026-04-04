package dev.spiritstudios.spectre.mixin.models.client;

import dev.spiritstudios.spectre.api.models.client.ext.SpectrePartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(PartDefinition.class)
public class PartDefinitionMixin implements SpectrePartDefinition {
	@Shadow
	@Final
	private Map<String, PartDefinition> children;

	@Shadow
	@Final
	private PartPose partPose;

	@Invoker("<init>")
	private static PartDefinition invokeInit(final List<CubeDefinition> cubes, final PartPose partPose) {
		throw new IllegalStateException("Implemented via mixin.");
	}

	@Override
	public PartDefinition copyCubeless(PartDefinition newPart) {
		for (Map.Entry<String, PartDefinition> entry : this.children.entrySet()) {
			var existing = newPart.getChild(entry.getKey());

			if (existing == null) newPart.addOrReplaceChild(entry.getKey(), entry.getValue().copyCubeless());
			else entry.getValue().copyCubeless(existing);
		}

		return newPart;
	}

	@Override
	public PartDefinition copyCubeless() {
		var newPart = invokeInit(List.of(), this.partPose);

		return copyCubeless(newPart);
	}
}
