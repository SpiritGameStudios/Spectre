package dev.spiritstudios.spectre.mixin.registry.metatag.builtin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.spiritstudios.spectre.api.core.registry.metatag.SpectreMetatags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin {
	@WrapOperation(method = "evaluateNewBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/AxeItem;getStripped(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"))
	private Optional<BlockState> getFromMetatag(
		AxeItem instance,
		BlockState state,
		Operation<Optional<BlockState>> original
	) {
		return state.getBlock().getData(SpectreMetatags.STRIPPABLE)
			.map(data -> data.getStrippedBlockState(state))
			.or(() -> original.call(instance, state));
	}
}
