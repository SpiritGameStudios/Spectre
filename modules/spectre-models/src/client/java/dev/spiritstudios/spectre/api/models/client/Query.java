package dev.spiritstudios.spectre.api.models.client;

import net.minecraft.SharedConstants;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentUser;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

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
	public boolean is_moving;
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
	public double modified_move_speed;

	public int moon_phase; // TODO: Enum support in mojank
	public float moon_brightness;

	public void set(Entity entity, float partialTick) {
		Level level = entity.level();

		day = level.getGameTime() / SharedConstants.TICKS_PER_GAME_DAY;

		moon_phase = level.environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, entity.position()).index();
		moon_brightness = DimensionType.MOON_BRIGHTNESS_PER_PHASE[moon_phase];

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
		is_moving = entity.getDeltaMovement().lengthSqr() != 0F;
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
		modified_move_speed = entity instanceof LivingEntity living ? living.getSpeed() : 0;
	}
}
