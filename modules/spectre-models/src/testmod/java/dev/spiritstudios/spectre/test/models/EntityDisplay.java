package dev.spiritstudios.spectre.test.models;

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
	public static final String TAG_ANIM = "anim";

	private static final EntityDataAccessor<Identifier> MODEL = SynchedEntityData.defineId(EntityDisplay.class, SpectreModelsTestmodInitializer.IDENTIFIER);
	private static final EntityDataAccessor<Identifier> TEXTURE = SynchedEntityData.defineId(EntityDisplay.class, SpectreModelsTestmodInitializer.IDENTIFIER);
	private static final EntityDataAccessor<Identifier> ANIM = SynchedEntityData.defineId(EntityDisplay.class, SpectreModelsTestmodInitializer.IDENTIFIER);


	protected @Nullable RenderState state;

	public EntityDisplay(EntityType<?> entityType, Level level) {
		super(entityType, level);
		updateRenderState = true;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(MODEL, Identifier.withDefaultNamespace("zombie/main"));
		builder.define(TEXTURE, Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png"));
		builder.define(ANIM, Identifier.withDefaultNamespace("none"));

	}

	@Override
	public boolean affectedByCulling() {
		return false;
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
		super.onSyncedDataUpdated(dataAccessor);
		if (dataAccessor.equals(MODEL) || dataAccessor.equals(TEXTURE) || dataAccessor.equals(ANIM)) this.updateRenderState = true;
	}

	public final Identifier getModel() {
		return this.entityData.get(MODEL);
	}

	public final void setModel(Identifier model) {
		this.entityData.set(MODEL, model);
		this.updateRenderState = true;
	}

	public final Identifier getTexture() {
		return this.entityData.get(TEXTURE);
	}

	public final Identifier getAnim() {
		return this.entityData.get(ANIM);
	}

	public final void setTexture(Identifier texture) {
		this.entityData.set(TEXTURE, texture);
		this.updateRenderState = true;
	}
	public final void setAnim(Identifier anim) {
		this.entityData.set(ANIM, anim);
		this.updateRenderState = true;
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		this.setModel(input.read(TAG_MODEL, Identifier.CODEC).orElse(Identifier.withDefaultNamespace("zombie/main")));
		this.setTexture(input.read(TAG_TEXTURE, Identifier.CODEC).orElse(Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png")));
		this.setAnim(input.read(TAG_ANIM, Identifier.CODEC).orElse(Identifier.withDefaultNamespace("none")));
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.store(TAG_MODEL, Identifier.CODEC, getModel());
		output.store(TAG_TEXTURE, Identifier.CODEC, getTexture());
		output.store(TAG_ANIM, Identifier.CODEC, getAnim());
	}

	@Nullable
	public RenderState state() {
		return this.state;
	}

	@Override
	protected void updateRenderSubState(boolean interpolate, float partialTick) {
		this.state = new RenderState(this.getModel(), this.getTexture(), getAnim());
	}

	public record RenderState(Identifier model, Identifier texture, Identifier anim) {
	}
}
