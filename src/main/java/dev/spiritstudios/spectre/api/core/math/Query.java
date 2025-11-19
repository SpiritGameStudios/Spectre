package dev.spiritstudios.spectre.api.core.math;

import net.minecraft.world.entity.Entity;

public class Query {
	public float anim_time;
	public float life_time;
	public float time_stamp;
	public float state_time;

	public float yaw_speed;
	public float ground_speed;
	public float vertical_speed;

	public boolean is_first_person;

	public boolean is_swimming;

	public boolean all_animations_finished;

	public static Query of(Entity entity) {
		Query q = new Query();

		q.ground_speed = (float) entity.getDeltaMovement().x;
		q.vertical_speed = (float) entity.getDeltaMovement().y;

		return q;
	}
}
