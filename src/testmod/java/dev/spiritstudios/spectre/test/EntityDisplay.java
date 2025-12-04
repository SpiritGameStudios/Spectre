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
	public static final String TAG_BLOCK_STATE = "block_state";
	private static final EntityDataAccessor<Identifier> STATE_ID = SynchedEntityData.defineId(
		EntityDisplay.class, SpectreTestmod.ENTITY_MODEL
	);

	private @Nullable RenderState state;

	public EntityDisplay(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(STATE_ID, Identifier.withDefaultNamespace("zombie/main"));
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
		super.onSyncedDataUpdated(dataAccessor);
		if (dataAccessor.equals(STATE_ID)) {
			this.updateRenderState = true;
		}
	}

	public final Identifier getModel() {
		return this.entityData.get(STATE_ID);
	}

	public final void setModel(Identifier model) {
		this.entityData.set(STATE_ID, model);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		this.setModel(input.read("model", Identifier.CODEC).orElse(Identifier.withDefaultNamespace("zombie/main")));
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.store("model", Identifier.CODEC, getModel());
	}

	@Nullable
	public RenderState state() {
		return this.state;
	}

	@Override
	protected void updateRenderSubState(boolean interpolate, float partialTick) {
		this.state = new RenderState(this.getModel());
	}

	public record RenderState(Identifier model) {
	}
}
