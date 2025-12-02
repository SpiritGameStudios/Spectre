package dev.spiritstudios.spectre.impl.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.spiritstudios.spectre.api.network.ScreenshakeS2CPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static net.minecraft.commands.Commands.argument;

public final class ScreenshakeCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("screenshake")
			.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(
				argument("viewers", EntityArgument.players()).then(
					argument("trauma", FloatArgumentType.floatArg(0F, 1F))
						.executes(context ->
							run(context,
								FloatArgumentType.getFloat(context, "trauma"),
								EntityArgument.getPlayers(context, "viewers")))
				)
			));
	}

	private static int run(CommandContext<CommandSourceStack> context, float trauma, Collection<ServerPlayer> viewers) {
		ScreenshakeS2CPayload payload = new ScreenshakeS2CPayload(trauma);
		viewers.forEach(player -> ServerPlayNetworking.send(player, payload));
		return Command.SINGLE_SUCCESS;
	}
}
