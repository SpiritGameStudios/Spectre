package dev.spiritstudios.spectre.api.core.math;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.SharedConstants;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentUser;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public class Query {
	public float anim_time;

	public float body_x_rotation;
	public float body_y_rotation;

	public long day;

	public boolean has_collision;
	public boolean has_gravity;
	public boolean has_owner;
	public boolean has_player_rider;
	public boolean has_rider;

	public boolean is_alive;
	public boolean is_angry;
	public boolean is_breathing;
	public boolean is_fire_immune;
	public boolean is_invisible;
	public boolean is_in_lava;
	public boolean is_in_water;
	public boolean is_in_water_or_rain;
	public boolean is_leashed;
	public boolean is_saddled;
	public boolean is_silent;
	public boolean is_sneaking;
	public boolean is_sprinting;
	public boolean is_swimming;

	public float scale;

	public double vertical_speed;
	public double ground_speed;

	public void set(Entity entity, float partialTick) {
		day = entity.level().getGameTime() / SharedConstants.TICKS_PER_GAME_DAY;

		body_x_rotation = entity.getXRot(partialTick);
		body_y_rotation = entity.getYRot(partialTick);

		has_collision = !entity.noPhysics;
		has_gravity = !entity.isNoGravity();
		has_owner = entity instanceof OwnableEntity ownable && ownable.getOwner() != null;
		has_player_rider = entity.hasPassenger(passenger -> passenger instanceof Player);
		has_rider = entity.hasPassenger(e -> true);

		is_alive = entity.isAlive();
		is_angry = entity instanceof NeutralMob neutral && neutral.isAngry();
		is_breathing = entity.getAirSupply() >= entity.getMaxAirSupply();
		is_fire_immune = entity.fireImmune();
		is_invisible = entity.isInvisible();
		is_in_water = entity.isInWater();
		is_in_water_or_rain = entity.isInWaterOrRain();
		is_in_lava = entity.isInLava();
		is_leashed = entity instanceof Leashable leashable && leashable.isLeashed();
		is_saddled = entity instanceof EquipmentUser equipment && !equipment.getItemBySlot(EquipmentSlot.SADDLE).isEmpty();
		is_silent = entity.isSilent();
		is_sneaking = entity.isCrouching();
		is_sprinting = entity.isSprinting();
		is_swimming = entity.isSwimming();

		scale = entity instanceof LivingEntity living ? living.getScale() : 1F;

		vertical_speed = entity.getDeltaMovement().y;
		ground_speed = entity.getDeltaMovement().horizontalDistance();
	}
}
