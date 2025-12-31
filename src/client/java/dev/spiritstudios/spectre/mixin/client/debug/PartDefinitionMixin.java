package dev.spiritstudios.spectre.mixin.client.debug;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.spiritstudios.spectre.impl.client.pond.SpectreModelPart;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(PartDefinition.class)
public class PartDefinitionMixin {
	@ModifyReturnValue(method = "method_32114", at = @At("RETURN"))
	private static ModelPart addName(ModelPart original, @Local(argsOnly = true) Map.Entry<String, ModelPart> entry) {
		original.spectre$setName(entry.getKey());
		return original;
	}
}
