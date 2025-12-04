package dev.spiritstudios.spectre.test;

import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;

public class EntityDisplayRenderState extends DisplayEntityRenderState {
	public EntityDisplay.RenderState state;

	@Override
	public boolean hasSubState() {
		return this.state != null;
	}
}
