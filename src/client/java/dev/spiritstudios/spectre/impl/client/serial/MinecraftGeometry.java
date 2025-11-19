package dev.spiritstudios.spectre.impl.client.serial;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record MinecraftGeometry(
	GeometryDescription description,
	List<ModelBone> bones
) {
	public static final Codec<MinecraftGeometry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		GeometryDescription.CODEC.fieldOf("description").forGetter(MinecraftGeometry::description),
		ModelBone.CODEC.listOf().fieldOf("bones").forGetter(MinecraftGeometry::bones)
	).apply(instance, MinecraftGeometry::new));
}
