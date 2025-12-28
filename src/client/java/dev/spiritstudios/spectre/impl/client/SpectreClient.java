package dev.spiritstudios.spectre.impl.client;

import dev.spiritstudios.spectre.api.client.SpectreScreenshake;
import dev.spiritstudios.spectre.api.network.ScreenshakeS2CPayload;
import dev.spiritstudios.spectre.impl.client.world.entity.render.animation.AnimationManager;
import dev.spiritstudios.spectre.impl.client.world.level.render.CustomChunkSectionLayer;
import dev.spiritstudios.spectre.impl.registry.MetatagContents;
import dev.spiritstudios.spectre.impl.registry.MetatagSyncS2CPayload;
import dev.spiritstudios.spectre.impl.world.item.CreativeModeTabReloader;
import dev.spiritstudios.spectre.impl.world.item.CreativeModeTabsS2CPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class SpectreClient implements ClientModInitializer {
	public static final AnimationManager ANIMATION_MANAGER = new AnimationManager();

	public static final List<CustomChunkSectionLayer> CUSTOM_LAYERS = new ArrayList<>();

    @Override
    public void onInitializeClient() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(AnimationManager.ID, ANIMATION_MANAGER);
		ResourceLoader.get(PackType.CLIENT_RESOURCES).addReloaderOrdering(
			AnimationManager.ID,
			ResourceReloaderKeys.Client.MODELS
		);

		ClientPlayNetworking.registerGlobalReceiver(
			ScreenshakeS2CPayload.TYPE,
			(payload, context) -> {
				SpectreScreenshake.addTrauma(payload.trauma());
			}
		);

		ClientPlayNetworking.registerGlobalReceiver(
			MetatagSyncS2CPayload.TYPE,
			(payload, context) -> {
				MetatagContents.apply(context.player().registryAccess(), payload.contents());
			}
		);

		ClientPlayNetworking.registerGlobalReceiver(
			CreativeModeTabsS2CPayload.TYPE,
			(payload, context) -> {
				CreativeModeTabReloader.apply(payload.tabs());
			}
		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			SpectreScreenshake.addTrauma(-(1F / SharedConstants.TICKS_PER_SECOND) / 2F);
		});
    }
}
