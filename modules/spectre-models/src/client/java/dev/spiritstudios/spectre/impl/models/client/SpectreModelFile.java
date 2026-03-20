package dev.spiritstudios.spectre.impl.models.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.spectre.api.models.client.layer.data.LayerData;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.spiritstudios.spectre.impl.models.client.Todo.todo;

public record SpectreModelFile(
	List<SpectreLayerDefinition<?, ?>> layers
) {
	// TODO: Encoding support
	public static final Codec<SpectreModelFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.unboundedMap(Codec.STRING, LayerData.CODEC).fieldOf("layers").forGetter(file -> todo()),
		Bone.CODEC.listOf().optionalFieldOf("bones", List.of()).forGetter(file -> todo())
	).apply(instance, (layerData, bones) -> {
		List<SpectreLayerDefinition<?, ?>> layers = new ArrayList<>();

		for (Map.Entry<String, LayerData<?>> entry : layerData.entrySet()) {
			layers.add(SpectreModelFile.createLayerDef(entry.getKey(), entry.getValue(), bones));
		}

		return new SpectreModelFile(layers);
	}));

	private static <Data extends LayerData<RenderState>, RenderState extends EntityRenderState> SpectreLayerDefinition<Data, RenderState> createLayerDef(
		String layerName,
		Data data,
		List<Bone> bones
	) {
		MeshDefinition mesh = new MeshDefinition();

		for (Bone bone : bones) {
			bone.bake(mesh.getRoot(), null, layerName);
		}

		return new SpectreLayerDefinition<>(layerName, mesh, data);
	}
}
