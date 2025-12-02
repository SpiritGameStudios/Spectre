package dev.spiritstudios.spectre.api.world.entity;

import net.minecraft.world.entity.Entity;

import java.util.List;

public interface PartHolder<T extends Entity> {
	List<? extends EntityPart<T>> getSubEntities();
}
