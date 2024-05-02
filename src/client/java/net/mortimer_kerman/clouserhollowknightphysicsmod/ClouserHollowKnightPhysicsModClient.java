package net.mortimer_kerman.clouserhollowknightphysicsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClouserHollowKnightPhysicsModClient implements ClientModInitializer
{
	public static boolean physicsOn = false;
	public static boolean movementOn = false;
	public static boolean knockbackOn = false;
	public static int knockbackCooldown = 0;

	@Override
	public void onInitializeClient()
	{
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.PHYSICS_ON, (client, handler, buf, responseSender) -> physicsOn = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.MOVEMENT_ON, (client, handler, buf, responseSender) -> movementOn = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.KNOCKBACK_ON, (client, handler, buf, responseSender) -> knockbackOn = buf.readBoolean());
		ClientPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.KNOCKBACK_COOLDOWN, (client, handler, buf, responseSender) -> knockbackCooldown = buf.readInt());
	}
}
