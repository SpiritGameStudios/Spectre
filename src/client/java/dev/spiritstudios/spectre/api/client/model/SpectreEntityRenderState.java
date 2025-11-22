package dev.spiritstudios.spectre.api.client.model;

import dev.spiritstudios.spectre.api.client.model.animation.AnimationControllerRenderState;
import dev.spiritstudios.spectre.api.core.math.Query;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface SpectreEntityRenderState {
	Query query();
	List<AnimationControllerRenderState> controllers();
}
