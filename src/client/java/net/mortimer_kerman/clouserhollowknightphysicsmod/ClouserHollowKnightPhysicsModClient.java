package net.mortimer_kerman.clouserhollowknightphysicsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;

public class ClouserHollowKnightPhysicsModClient implements ClientModInitializer
{
	public static boolean physicsOn = false;
	public static boolean movementOn = true;
	public static boolean knockbackOn = true;
	public static boolean axisXOn = true;
	public static boolean axisYOn = true;
	public static boolean doubleJumpOn = false;
	public static boolean canJump = true;
	public static boolean hollowKnightJump = false;
	public static boolean zKeysLookOn = false;
	public static float stepHeight = 0.6f;

	public static int knockbackCooldown = 0;

	public static boolean perspectiveLocked = false;

	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(Payloads.BooleanPayload.ID, (payload, context) -> {
			switch (payload.strId()) {
				case ClouserHollowKnightPhysicsMod.PHYSICS_ON -> physicsOn = payload.value();
				case ClouserHollowKnightPhysicsMod.MOVEMENT_ON -> movementOn = payload.value();
				case ClouserHollowKnightPhysicsMod.KNOCKBACK_ON -> knockbackOn = payload.value();
				case ClouserHollowKnightPhysicsMod.AXIS_X_ON -> axisXOn = payload.value();
				case ClouserHollowKnightPhysicsMod.AXIS_Y_ON -> axisYOn = payload.value();
				case ClouserHollowKnightPhysicsMod.DOUBLEJUMP_ON -> doubleJumpOn = payload.value();
				case ClouserHollowKnightPhysicsMod.CANJUMP_ON -> canJump = payload.value();
				case ClouserHollowKnightPhysicsMod.PERSPECTIVE_LOCKED_ON -> perspectiveLocked = payload.value();
				case ClouserHollowKnightPhysicsMod.HOLLOWKNIGHT_JUMP_ON -> hollowKnightJump = payload.value();
				case ClouserHollowKnightPhysicsMod.ZKEYS_LOOK_ON -> zKeysLookOn = payload.value();
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(Payloads.FloatPayload.ID, (payload, context) -> { if (payload.strId().equals(ClouserHollowKnightPhysicsMod.PLAYER_STEP_HEIGHT)) stepHeight = payload.value(); });

		ClientPlayNetworking.registerGlobalReceiver(Payloads.IntPayload.ID, (payload, context) -> {
			switch (payload.strId()) {
				case ClouserHollowKnightPhysicsMod.KNOCKBACK_COOLDOWN:
					knockbackCooldown = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.PERSPECTIVE_DATA:
					switch (payload.value()) {
						case 0 -> context.client().options.setPerspective(Perspective.FIRST_PERSON);
						case 1 -> context.client().options.setPerspective(Perspective.THIRD_PERSON_BACK);
						case 2 -> context.client().options.setPerspective(Perspective.THIRD_PERSON_FRONT);
					}
					break;
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(Payloads.EmptyPayload.ID, (payload, context) -> {
			if(payload.strId().equals(ClouserHollowKnightPhysicsMod.CLEARCHAT) && context.client().inGameHud != null) context.client().inGameHud.getChatHud().clear(false);
		});

		ClientPlayNetworking.registerGlobalReceiver(Payloads.Vec3dCouplePayload.ID, (payload, context) -> {
			if(payload.strId().equals(ClouserHollowKnightPhysicsMod.VELOCITY_CHANGE)) {
				if (context.client().player == null) return;
				context.client().player.setVelocity(context.client().player.getVelocity().multiply(payload.value1()).add(payload.value2()));
			}
		});
	}
}
