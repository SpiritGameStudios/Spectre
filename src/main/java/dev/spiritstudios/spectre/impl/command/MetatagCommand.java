package dev.spiritstudios.spectre.impl.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.spiritstudios.spectre.api.core.registry.SpectreRegistryKeys;
import dev.spiritstudios.spectre.api.core.registry.metatag.MetatagFile;
import dev.spiritstudios.spectre.api.core.registry.metatag.MetatagKey;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class MetatagCommand {

	private static final ResourceKey<Registry<Registry<Object>>> ROOT = ResourceKey.createRegistryKey(Registries.ROOT_REGISTRY_NAME);

	private static final DynamicCommandExceptionType ERROR_INVALID_REGISTRY = new DynamicCommandExceptionType(
		object -> Component.translatableEscape("metatag.notFound", object)
	);

	private static CompletableFuture<Suggestions> suggestMetatagEntries(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		var metatag = ResourceArgument.getResource(
			context,
			"metatag",
			SpectreRegistryKeys.METATAG
		).value();

		return SharedSuggestionProvider.suggestResource(
			metatagEntries(metatag, context.getSource().registryAccess()),
			builder
		);
	}

	private static <K, V> Stream<Identifier> metatagEntries(MetatagKey<K, V> metatag, RegistryAccess registries) {
		return registries
			.lookupOrThrow(metatag.registry())
			.listElements()
			.filter(holder -> holder.hasData(metatag))
			.map(Holder.Reference::key)
			.map(ResourceKey::identifier);
	}


	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
		dispatcher.register(literal("metatag")
			.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(argument("metatag", ResourceArgument.resource(buildContext, SpectreRegistryKeys.METATAG))
				.executes(context -> dump(
					context,
					ResourceArgument.getResource(
						context,
						"metatag",
						SpectreRegistryKeys.METATAG
					).value()
				))
				.then(argument("holder", IdentifierArgument.id())
					.suggests(MetatagCommand::suggestMetatagEntries)
					.executes(context -> get(
						context,
						ResourceArgument.getResource(
							context,
							"metatag",
							SpectreRegistryKeys.METATAG
						).value(),
						IdentifierArgument.getId(context, "holder")
					)))));
	}

	private static <K, V> int get(CommandContext<CommandSourceStack> context, MetatagKey<K, V> metatag, Identifier holderLocation) {
		RegistryAccess registries = context.getSource().registryAccess();

		var holder = registries.getOrThrow(
			ResourceKey.create(
				metatag.registry(),
				holderLocation
			)
		);

		context.getSource().sendSuccess(() ->
			NbtUtils.toPrettyComponent(
				metatag.codec().encodeStart(
						registries.createSerializationContext(NbtOps.INSTANCE),
						holder.getDataOrThrow(metatag)
					)
					.getOrThrow()), true);
		return Command.SINGLE_SUCCESS;
	}

	private static <K, V> int dump(CommandContext<CommandSourceStack> context, MetatagKey<K, V> metatagKey) throws CommandSyntaxException {
		Codec<MetatagFile<K, V>> codec = MetatagFile.resourceCodecOf(metatagKey);

		RegistryAccess registryManager = context.getSource().registryAccess();
		Registry<K> registry = registryManager.lookupOrThrow(metatagKey.registry());

		MetatagFile<K, V> resource = new MetatagFile<>(
			registry.listElements()
				.flatMap(entry -> entry.getData(metatagKey)
					.map(value -> new Pair<>(entry, value))
					.stream()
				)
				.collect(Collectors.toMap(
					Pair::getFirst,
					Pair::getSecond
				)),
			false
		);

		context.getSource().sendSuccess(() ->
			NbtUtils.toPrettyComponent(codec.encodeStart(registryManager.createSerializationContext(NbtOps.INSTANCE), resource)
				.getOrThrow()), true);
		return Command.SINGLE_SUCCESS;
	}
}
