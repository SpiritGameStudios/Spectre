package dev.spiritstudios.spectre.test.models.client;

import dev.spiritstudios.spectre.api.models.client.Query;
import dev.spiritstudios.spectre.test.models.EntityDisplay;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;

public class EntityDisplayRenderState extends DisplayEntityRenderState {
	public final Query query = new Query();
	public EntityDisplay.RenderState state;

	@Override
	public boolean hasSubState() {
		return this.state != null;
	}
}
