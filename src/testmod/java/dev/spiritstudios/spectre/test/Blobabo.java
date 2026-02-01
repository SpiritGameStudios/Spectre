package dev.spiritstudios.spectre.test;

import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.api.world.entity.EntityPart;
import dev.spiritstudios.spectre.api.world.entity.PartHolder;
import dev.spiritstudios.spectre.api.world.entity.animation.AnimationController;
import net.minecraft.SharedConstants;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Blobabo extends PathfinderMob implements PartHolder<Blobabo> {
	private final List<BlobaboPart> parts = List.of(new BlobaboPart(
		this,
		EntityDimensions.scalable(2f, 1f),
		new Vec3(0, 1, 0)
	));

	private float wiggleCooldown = 20 * SharedConstants.TICKS_PER_SECOND;

	private boolean swimming;

	public final AnimationController movement;
	public final AnimationController antenna;

	public Blobabo(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);

		var builder = new AnimationController.Builder();

		var idle = builder
			.createState("idle")
			.animation("idle");

		var swim = builder
			.createState("swim")
			.animation("swim");

		idle.transition(() -> this.getDeltaMovement().lengthSqr() > (0.25 * 0.25) ?
			new AnimationController.Transition(swim.state) :
			null
		);

		swim.transition(() -> this.getDeltaMovement().lengthSqr() < (0.25 * 0.25) ?
			new AnimationController.Transition(idle.state) :
			null
		);

		movement = builder.build(idle);

		builder = new AnimationController.Builder();

		var sway = builder
			.createState("sway")
			.animation("sway");

		var wiggle = builder
			.createState("wiggle")
			.animation("wiggle");

		sway.transition(() -> this.getDeltaMovement().lengthSqr() > (0.25 * 0.25) ?
			new AnimationController.Transition(swim.state) :
			null
		);

		antenna = builder.build(idle);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();

		this.goalSelector.addGoal(0, new WaterAvoidingRandomStrollGoal(this, 1.0));
	}

	@Override
	public List<? extends EntityPart<Blobabo>> getSubEntities() {
		return parts;
	}

	@Override
	public void aiStep() {
		super.aiStep();

		parts.getFirst().setOldPosAndRot();
		parts.getFirst().setPos(parts.getFirst().relativePos.add(this.position()));
	}

	@Override
	public boolean hurtClient(DamageSource damageSource) {
		swimming = !swimming;
		return super.hurtClient(damageSource);
	}

	@Override
	public void tick() {
		super.tick();

		movement.tick(tickCount);
		antenna.tick(tickCount);
	}
}
