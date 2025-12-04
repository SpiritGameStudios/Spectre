package dev.spiritstudios.spectre.test;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class EntityDisplay extends Display {
	public static final String TAG_MODEL = "model";
	public static final String TAG_TEXTURE = "texture";

	private static final EntityDataAccessor<Identifier> MODEL = SynchedEntityData.defineId(EntityDisplay.class, SpectreTestmod.IDENTIFIER);
	private static final EntityDataAccessor<Identifier> TEXTURE = SynchedEntityData.defineId(EntityDisplay.class, SpectreTestmod.IDENTIFIER);


	private @Nullable RenderState state;

	public EntityDisplay(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(MODEL, Identifier.withDefaultNamespace("zombie/main"));
		builder.define(TEXTURE, Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png"));
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
		super.onSyncedDataUpdated(dataAccessor);
		if (dataAccessor.equals(MODEL) || dataAccessor.equals(TEXTURE)) this.updateRenderState = true;
	}

	public final Identifier getModel() {
		return this.entityData.get(MODEL);
	}

	public final void setModel(Identifier model) {
		this.entityData.set(MODEL, model);
	}

	public final Identifier getTexture() {
		return this.entityData.get(TEXTURE);
	}

	public final void setTexture(Identifier texture) {
		this.entityData.set(TEXTURE, texture);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		this.setModel(input.read("model", Identifier.CODEC).orElse(Identifier.withDefaultNamespace("zombie/main")));
		this.setTexture(input.read("texture", Identifier.CODEC).orElse(Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png")));
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.store("model", Identifier.CODEC, getModel());
		output.store("texture", Identifier.CODEC, getTexture());
	}

	@Nullable
	public RenderState state() {
		return this.state;
	}

	@Override
	protected void updateRenderSubState(boolean interpolate, float partialTick) {
		this.state = new RenderState(this.getModel(), this.getTexture());
	}

	public record RenderState(Identifier model, Identifier texture) {
	}
}
