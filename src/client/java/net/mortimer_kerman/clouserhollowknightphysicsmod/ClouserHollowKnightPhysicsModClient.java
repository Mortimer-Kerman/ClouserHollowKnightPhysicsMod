package net.mortimer_kerman.clouserhollowknightphysicsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.Perspective;
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

	public static int knockbackCooldown = 0;

	public static boolean perspectiveLocked = false;

	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.PHYSICS_ON, (client, handler, buf, responseSender) -> physicsOn = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.MOVEMENT_ON, (client, handler, buf, responseSender) -> movementOn = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.KNOCKBACK_ON, (client, handler, buf, responseSender) -> knockbackOn = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.AXIS_X_ON, (client, handler, buf, responseSender) -> axisXOn = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.AXIS_Y_ON, (client, handler, buf, responseSender) -> axisYOn = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.DOUBLEJUMP_ON, (client, handler, buf, responseSender) -> doubleJumpOn = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.CANJUMP_ON, (client, handler, buf, responseSender) -> canJump = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.PERSPECTIVE_LOCKED_ON, (client, handler, buf, responseSender) -> perspectiveLocked = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.HOLLOWKNIGHT_JUMP_ON, (client, handler, buf, responseSender) -> hollowKnightJump = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.ZKEYS_LOOK_ON, (client, handler, buf, responseSender) -> zKeysLookOn = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.PLAYER_STEP_HEIGHT, (client, handler, buf, responseSender) -> {
			if (client.player == null) return;
			client.player.setStepHeight(buf.readFloat());
		});

		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.KNOCKBACK_COOLDOWN, (client, handler, buf, responseSender) -> knockbackCooldown = buf.readInt());

		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.CLEARCHAT, (client, handler, buf, responseSender) -> {
			if (client.inGameHud != null) client.inGameHud.getChatHud().clear(false);
		});

		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.PERSPECTIVE_DATA, (client, handler, buf, responseSender) -> {
			switch (buf.readInt()) {
				case 0 -> client.options.setPerspective(Perspective.FIRST_PERSON);
				case 1 -> client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
				case 2 -> client.options.setPerspective(Perspective.THIRD_PERSON_FRONT);
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.VELOCITY_CHANGE, (client, handler, buf, responseSender) -> {
			if (client.player == null) return;
			client.player.setVelocity(client.player.getVelocity().multiply(buf.readVec3d()).add(buf.readVec3d()));
		});
	}
}
