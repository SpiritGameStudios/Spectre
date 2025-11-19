package dev.spiritstudios.spectre.test;

import dev.spiritstudios.spectre.api.client.model.animation.AnimationControllerRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class BlobaboEntityRenderState extends LivingEntityRenderState {
	public final AnimationControllerRenderState movement = new AnimationControllerRenderState();
	public final AnimationControllerRenderState antenna = new AnimationControllerRenderState();
}
