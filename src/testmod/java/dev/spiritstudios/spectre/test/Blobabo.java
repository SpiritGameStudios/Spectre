package dev.spiritstudios.spectre.test;

import dev.spiritstudios.spectre.api.core.math.Query;
import dev.spiritstudios.spectre.api.world.entity.EntityPart;
import dev.spiritstudios.spectre.api.world.entity.PartHolder;
import dev.spiritstudios.spectre.api.world.entity.animation.AnimationController;
import dev.spiritstudios.spectre.impl.Spectre;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class Blobabo extends PathfinderMob implements PartHolder<Blobabo> {
	private final List<BlobaboPart> parts = List.of(new BlobaboPart(
		this,
		EntityDimensions.scalable(2f, 1f),
		new Vec3(0, 1, 0)
	));

	private boolean swimming;

	public final AnimationController movement;
	public final AnimationController antenna;

	private final Query query = new Query();

	public Blobabo(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);

		movement = AnimationController.create(
			Spectre.id("bloomray"),
			"controller.animation.bloomray.movement",
			this
		);

		antenna = AnimationController.create(
			Spectre.id("bloomray"),
			"controller.animation.bloomray.antenna",
			this
		);
	}

	@Override
	public List<? extends EntityPart<Blobabo>> getParts() {
		return parts;
	}

	@Override
	public void aiStep() {
		super.aiStep();
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

		query.set(this, 0F);

		movement.tick(query);
		antenna.tick(query);
	}
}
