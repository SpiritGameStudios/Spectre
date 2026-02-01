package dev.spiritstudios.spectre.api.client.model.animation;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum LoopType implements StringRepresentable {
	TRUE("true"),
	FALSE("false"),
	HOLD_ON_LAST_FRAME("hold_on_last_frame");

	private final String name;

	LoopType(String name) {
		this.name = name;
	}

	@Override
	public @NotNull String getSerializedName() {
		return name;
	}

	public static final Codec<LoopType> CODEC = Codec.either(
		Codec.BOOL.xmap(
			bool -> bool
				? LoopType.TRUE
				: LoopType.FALSE,
			LoopType.TRUE::equals
		),
		StringRepresentable.fromEnum(LoopType::values)
	).xmap(
		Either::unwrap,
		type -> switch (type) {
			case TRUE, FALSE -> Either.left(type);
			case HOLD_ON_LAST_FRAME -> Either.right(type);
		}
	);
}
