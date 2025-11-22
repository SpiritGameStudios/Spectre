package dev.spiritstudios.spectre.api.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.function.Function;

public class EmptyEntityModel<T extends EntityRenderState> extends EntityModel<T> {
	public EmptyEntityModel() {
		super(new ModelPart(Collections.emptyList(), Collections.emptyMap()));
	}

	protected EmptyEntityModel(Function<ResourceLocation, RenderType> function) {
		super(new ModelPart(Collections.emptyList(), Collections.emptyMap()), function);
	}
}
