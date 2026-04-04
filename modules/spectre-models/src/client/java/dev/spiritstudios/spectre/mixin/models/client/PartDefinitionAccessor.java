package dev.spiritstudios.spectre.mixin.models.client;

import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PartDefinition.class)
public interface PartDefinitionAccessor {
	@Accessor
	Map<String, PartDefinition> getChildren();
}
