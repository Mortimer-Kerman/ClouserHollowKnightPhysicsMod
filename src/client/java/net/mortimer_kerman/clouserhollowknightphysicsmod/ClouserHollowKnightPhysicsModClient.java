package net.mortimer_kerman.clouserhollowknightphysicsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.mortimer_kerman.clouserhollowknightphysicsmod.argument.Operation;
import net.mortimer_kerman.clouserhollowknightphysicsmod.interfaces.PlayerMixinInterface;
import org.lwjgl.glfw.GLFW;

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
	public static boolean canPlayerFall = true;
	public static boolean cameraClip = true;
	public static float eyeHeight = 1;
	public static boolean silksongJump = false;

	public static int knockbackCooldown = 0;

	public static boolean perspectiveLocked = false;

	public static long NextShakeEnd = 0;
	public static float ShakeStenght = 0;

	public static boolean LockFOVAutoChange = false;

	public static boolean fastFall = false;

	private static KeyBinding dashKey;
	private static boolean dashPressed = false;

	@Override
	public void onInitializeClient()
	{
		dashKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key." + ClouserHollowKnightPhysicsMod.MOD_ID + ".dash",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_UNKNOWN,
				KeyBinding.MOVEMENT_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (dashKey.isPressed()) {
				if(!dashPressed)
				{
					MinecraftClient.getInstance().execute(() -> ClientPlayNetworking.send(new Payloads.EmptyPayload(ClouserHollowKnightPhysicsMod.RECORD_DASH)));
					dashPressed = true;
				}
			}
			else dashPressed = false;
		});

		ClientPlayNetworking.registerGlobalReceiver(Payloads.BooleanPayload.ID, (payload, context) -> {
			switch (payload.strId()) {
				case ClouserHollowKnightPhysicsMod.PHYSICS_ON:
					physicsOn = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.MOVEMENT_ON:
					movementOn = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.KNOCKBACK_ON:
					knockbackOn = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.AXIS_X_ON:
					axisXOn = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.AXIS_Y_ON:
					axisYOn = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.DOUBLEJUMP_ON:
					doubleJumpOn = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.CANJUMP_ON:
					canJump = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.PERSPECTIVE_LOCKED_ON:
					perspectiveLocked = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.HOLLOWKNIGHT_JUMP_ON:
					hollowKnightJump = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.ZKEYS_LOOK_ON:
					zKeysLookOn = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.PLAYER_FALL:
					canPlayerFall = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.INVENTORY:
					if(payload.value()) {
						if (context.client().interactionManager.hasRidingInventory()) {
							context.player().openRidingInventory();
						} else {
							context.client().getTutorialManager().onInventoryOpened();
							context.client().setScreen(new InventoryScreen(context.player()));
						}
					}
					else if (context.client().currentScreen instanceof HandledScreen screen) screen.close();
					break;
				case ClouserHollowKnightPhysicsMod.FAST_FALL:
					fastFall = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.CAMERA_CLIP:
					cameraClip = payload.value();
					break;
				case ClouserHollowKnightPhysicsMod.SILKSONG_JUMP:
					silksongJump = payload.value();
					break;
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(Payloads.FloatPayload.ID, (payload, context) -> {
			switch (payload.strId()) {
				case ClouserHollowKnightPhysicsMod.PLAYER_STEP_HEIGHT -> stepHeight = payload.value();
				case ClouserHollowKnightPhysicsMod.PLAYER_EYE_HEIGHT -> eyeHeight = payload.value();
			}
		});

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
			switch (payload.strId()) {
				case ClouserHollowKnightPhysicsMod.CLEARCHAT:
					if (context.client().inGameHud != null) context.client().inGameHud.getChatHud().clear(false);
					break;
				case ClouserHollowKnightPhysicsMod.RESET_DOUBLEJUMP:
					((PlayerMixinInterface)context.player()).clouserHollowKnightPhysicsMod$ResetDoubleJump();
					break;
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(Payloads.Vec3dCouplePayload.ID, (payload, context) -> {
			if(payload.strId().equals(ClouserHollowKnightPhysicsMod.VELOCITY_CHANGE)) {
				if (context.player() == null) return;
				context.player().setVelocity(context.player().getVelocity().multiply(payload.value1()).add(payload.value2()));
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(Payloads.FloatIntPayload.ID, (payload, context) -> {
			switch (payload.strId()) {
				case ClouserHollowKnightPhysicsMod.SHAKE_CAMERA:
					ClientWorld world = context.client().world;
					if (world == null) return;
					NextShakeEnd = world.getTime() + payload.valueI();
					ShakeStenght = payload.valueF();
					break;
				case ClouserHollowKnightPhysicsMod.FOV_MODIFIER:
					SimpleOption<Integer> fov = context.client().options.getFov();
					LockFOVAutoChange = true;
					switch (Operation.fromInt(payload.valueI())) {
						case Operation.SET -> fov.setValue((int)payload.valueF());
						case Operation.ADD -> fov.setValue((int)(fov.getValue() + payload.valueF()));
						case Operation.REMOVE -> fov.setValue((int)(fov.getValue() - payload.valueF()));
						case Operation.MULTIPLY -> fov.setValue((int)(fov.getValue() * payload.valueF()));
						case Operation.DIVIDE -> fov.setValue((int)(fov.getValue() / payload.valueF()));
					}
					LockFOVAutoChange = false;
					break;
			}
		});
	}
}
