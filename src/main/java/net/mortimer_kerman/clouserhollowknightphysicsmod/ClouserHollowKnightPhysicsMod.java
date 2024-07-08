package net.mortimer_kerman.clouserhollowknightphysicsmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.Blocks;
import net.minecraft.command.argument.*;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.mortimer_kerman.clouserhollowknightphysicsmod.argument.*;
import net.mortimer_kerman.clouserhollowknightphysicsmod.argument.OperationArgumentType;
import net.mortimer_kerman.clouserhollowknightphysicsmod.data.DataManager;
import net.mortimer_kerman.clouserhollowknightphysicsmod.data.PlayerData;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class ClouserHollowKnightPhysicsMod implements ModInitializer
{
	public static final String MOD_ID = "clouser-hollowknight-physics-mod";

	public static final String PHYSICS_ON = 			"physics_on";
	public static final String MOVEMENT_ON = 			"movement_on";
	public static final String KNOCKBACK_ON = 			"knockback_on";
	public static final String AXIS_X_ON = 				"axis_x_on";
	public static final String AXIS_Y_ON = 				"axis_y_on";
	public static final String DOUBLEJUMP_ON = 			"doublejump_on";
	public static final String CANJUMP_ON = 			"canjump_on";
	public static final String PERSPECTIVE_LOCKED_ON = 	"perspectivelocked_on";
	public static final String HOLLOWKNIGHT_JUMP_ON = 	"hollowknight_jump_on";
	public static final String ZKEYS_LOOK_ON = 			"zkeys_look_on";
	public static final String PLAYER_STEP_HEIGHT = 	"player_step_height";
	public static final String PLAYER_FALL =	 		"can_player_fall";

	public static final String KNOCKBACK_COOLDOWN = "knockback_timer";
	public static final String CLEARCHAT = 			"clearchat";
	public static final String PERSPECTIVE_DATA = 	"perspective_data";
	public static final String JUMP_RECORD = 		"jump_record";
	public static final String VELOCITY_CHANGE = 	"velocity_change";
	public static final String ZKEY_PRESS = 		"zkey_press";
	public static final String INVENTORY =	 		"inventory";
	public static final String RESET_DOUBLEJUMP = 	"reset_doublejump";
	public static final String SHAKE_CAMERA = 		"shake_camera";

	public static final String FOV_MODIFIER = 		"fov_modifier";
	public static final String FAST_FALL = 			"fast_fall";

	public static final GameRules.Key<GameRules.BooleanRule> PHYSICS_GAMERULE = 			GameRuleRegistry.register("hollowKnightPhysicsOn", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<GameRules.BooleanRule> MOVEMENT_GAMERULE = 			GameRuleRegistry.register("canPlayerMove", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> KNOCKBACK_GAMERULE = 			GameRuleRegistry.register("isKnockbackOn", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> AXIS_X_GAMERULE = 				GameRuleRegistry.register("axisXenabled", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> AXIS_Y_GAMERULE = 				GameRuleRegistry.register("axisYenabled", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> DOUBLEJUMP_GAMERULE = 			GameRuleRegistry.register("canDoubleJump", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<GameRules.BooleanRule> CANJUMP_GAMERULE = 			GameRuleRegistry.register("canPlayerJump", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> PERSPECTIVE_LOCKED_GAMERULE = 	GameRuleRegistry.register("perspectiveLocked", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<GameRules.BooleanRule> HOLLOWKNIGHT_JUMP_GAMERULE = 	GameRuleRegistry.register("hollowKnightJump", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<GameRules.BooleanRule> ZKEYS_LOOK_GAMERULE = 			GameRuleRegistry.register("zKeysLookOn", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<DoubleRule> 			 PLAYER_STEP_GAMERULE = 		GameRuleRegistry.register("playerStepHeight", GameRules.Category.MISC, GameRuleFactory.createDoubleRule(0.6D, 0));
	public static final GameRules.Key<GameRules.BooleanRule> PLAYER_FALL_GAMERULE = 		GameRuleRegistry.register("canPlayerFall", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

	@Override
	public void onInitialize()
	{
		Payloads.RegisterPayloads();

		ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "template_perspective"), PerspectiveArgumentType.class, ConstantArgumentSerializer.of(PerspectiveArgumentType::playerPerspective));
		ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "template_openclose"), OpenCloseArgumentType.class, ConstantArgumentSerializer.of(OpenCloseArgumentType::openCloseState));
		ArgumentTypeRegistry.registerArgumentType(Identifier.of(MOD_ID, "template_operation"), OperationArgumentType.class, ConstantArgumentSerializer.of(OperationArgumentType::operation));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> VelocityCommand(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ClearChatCommand(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> PerspectiveCommand(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> InventoryCommand(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ResetDoubleJumpCommand(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ShakeCameraCommand(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> FovCommand(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> FastFallCommand(dispatcher));

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {

			ServerPlayerEntity player = handler.getPlayer();

			PlayerData playerState = DataManager.getPlayerState(player);

			boolean physics = server.getGameRules().getBoolean(PHYSICS_GAMERULE);
			boolean movement = server.getGameRules().getBoolean(MOVEMENT_GAMERULE);
			boolean knockback = server.getGameRules().getBoolean(KNOCKBACK_GAMERULE);
			boolean axisX = server.getGameRules().getBoolean(AXIS_X_GAMERULE);
			boolean axisY = server.getGameRules().getBoolean(AXIS_Y_GAMERULE);
			boolean doubleJump = server.getGameRules().getBoolean(DOUBLEJUMP_GAMERULE);
			boolean canJump = server.getGameRules().getBoolean(CANJUMP_GAMERULE);
			boolean perspectiveLocked = server.getGameRules().getBoolean(PERSPECTIVE_LOCKED_GAMERULE);
			boolean hollowKnightJump = server.getGameRules().getBoolean(HOLLOWKNIGHT_JUMP_GAMERULE);
			boolean zKeysLookOn = server.getGameRules().getBoolean(ZKEYS_LOOK_GAMERULE);
			float playerStepHeight = (float)server.getGameRules().get(PLAYER_STEP_GAMERULE).get();
			boolean canPlayerFall = server.getGameRules().getBoolean(PLAYER_FALL_GAMERULE);

			server.execute(() -> {
				ServerPlayNetworking.send(player, new Payloads.BooleanPayload(PHYSICS_ON, physics));
				ServerPlayNetworking.send(player, new Payloads.BooleanPayload(MOVEMENT_ON, movement));
				ServerPlayNetworking.send(player, new Payloads.BooleanPayload(KNOCKBACK_ON, knockback));
				ServerPlayNetworking.send(player, new Payloads.BooleanPayload(AXIS_X_ON, axisX));
				ServerPlayNetworking.send(player, new Payloads.BooleanPayload(AXIS_Y_ON, axisY));
				ServerPlayNetworking.send(player, new Payloads.BooleanPayload(DOUBLEJUMP_ON, doubleJump));
				ServerPlayNetworking.send(player, new Payloads.BooleanPayload(CANJUMP_ON, canJump));
				ServerPlayNetworking.send(player, new Payloads.BooleanPayload(PERSPECTIVE_LOCKED_ON, perspectiveLocked));
				ServerPlayNetworking.send(player, new Payloads.BooleanPayload(HOLLOWKNIGHT_JUMP_ON, hollowKnightJump));
				ServerPlayNetworking.send(player, new Payloads.BooleanPayload(ZKEYS_LOOK_ON, zKeysLookOn));
				ServerPlayNetworking.send(player, new Payloads.FloatPayload(PLAYER_STEP_HEIGHT, playerStepHeight));
				ServerPlayNetworking.send(player, new Payloads.BooleanPayload(PLAYER_FALL, canPlayerFall));

				ServerPlayNetworking.send(player, new Payloads.BooleanPayload(FAST_FALL, playerState.fastFall));
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(Payloads.EmptyPayload.ID, (payload, context) -> { if (payload.strId().equals(JUMP_RECORD)) context.player().incrementStat(Stats.JUMP); });

		ServerPlayNetworking.registerGlobalReceiver(Payloads.IntPayload.ID, (payload, context) -> {
			if (payload.strId().equals(ZKEY_PRESS)) context.server().execute(() -> {
				int code = payload.value();
				if (code == 0) context.player().getWorld().setBlockState(new BlockPos(13, -40, 13), Blocks.REDSTONE_BLOCK.getDefaultState(), 3);
				if (code == 1) context.player().getWorld().setBlockState(new BlockPos(13, -40, 17), Blocks.REDSTONE_BLOCK.getDefaultState(), 3);
				if (code == 2) context.player().getWorld().setBlockState(new BlockPos(13, -40, 15), Blocks.REDSTONE_BLOCK.getDefaultState(), 3);
				if (code == 3) context.player().getWorld().setBlockState(new BlockPos(13, -40, 15), Blocks.REDSTONE_BLOCK.getDefaultState(), 3);
			});
		});
	}

	private static void VelocityCommand(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("velocity")
			.requires(s -> s.hasPermissionLevel(2))
			.then(
				CommandManager.argument("velocity", Vec3ArgumentType.vec3(false))
					.executes(ctx -> setVelocity(ctx.getSource(), Collections.singleton((ctx.getSource()).getEntityOrThrow()), Vec3ArgumentType.getPosArgument(ctx, "velocity")))
				.then(
						CommandManager.argument("pausePhysicsTicks", IntegerArgumentType.integer(0,255))
						.executes(ctx -> setVelocity(ctx.getSource(), Collections.singleton((ctx.getSource()).getEntityOrThrow()), Vec3ArgumentType.getPosArgument(ctx, "velocity"), IntegerArgumentType.getInteger(ctx, "pausePhysicsTicks")))
				)
			)
			.then(
				CommandManager.argument("targets", EntityArgumentType.entities())
				.then(
					CommandManager.argument("velocity", Vec3ArgumentType.vec3(false))
						.executes(ctx -> setVelocity(ctx.getSource(), EntityArgumentType.getEntities(ctx, "targets"), Vec3ArgumentType.getPosArgument(ctx, "velocity")))
					.then(
						CommandManager.argument("pausePhysicsTicks", IntegerArgumentType.integer(0,255))
							.executes(ctx -> setVelocity(ctx.getSource(), EntityArgumentType.getEntities(ctx, "targets"), Vec3ArgumentType.getPosArgument(ctx, "velocity"), IntegerArgumentType.getInteger(ctx, "pausePhysicsTicks")))
					)
				)
			)
		);
	}

	private static int setVelocity(ServerCommandSource src, Collection<? extends Entity> targets, PosArgument velocity) { return setVelocity(src, targets, velocity, 0); }

	private static int setVelocity(ServerCommandSource src, Collection<? extends Entity> targets, PosArgument velocity, int pausePhysicsTicks)
	{
		Vec3d velocityArg = velocity.toAbsolutePos(src.withPosition(Vec3d.ZERO));

		Entity srcEntity = src.getEntity();
		Vec3d srcVelocity = (srcEntity == null) ? Vec3d.ZERO : srcEntity.getVelocity();
		if (!velocity.isXRelative()) srcVelocity = srcVelocity.withAxis(Direction.Axis.X, 0);
		if (!velocity.isYRelative()) srcVelocity = srcVelocity.withAxis(Direction.Axis.Y, 0);
		if (!velocity.isZRelative()) srcVelocity = srcVelocity.withAxis(Direction.Axis.Z, 0);

		Vec3d targetVelocity = srcVelocity.add(velocityArg);

		for (Entity entity : targets)
		{
			if(entity instanceof ServerPlayerEntity player)
			{
				if (pausePhysicsTicks != 0)
				{
					player.getServer().execute(() -> ServerPlayNetworking.send(player, new Payloads.IntPayload(KNOCKBACK_COOLDOWN, pausePhysicsTicks)));
				}

				Vec3d velocityRelative = new Vec3d(velocity.isXRelative() ? 1 : 0, velocity.isYRelative() ? 1 : 0, velocity.isZRelative() ? 1 : 0);

				player.getServer().execute(() -> ServerPlayNetworking.send(player, new Payloads.Vec3dCouplePayload(VELOCITY_CHANGE, velocityRelative, velocityArg)));
			}
			else
			{
				entity.setVelocity(targetVelocity);
				entity.velocityModified = true;
			}
		}

		if (targets.size() == 1) {
			src.sendFeedback(() -> Text.translatable("commands.velocity.success.single",
					targets.iterator().next().getDisplayName(),
					String.format(Locale.ROOT, "%f", targetVelocity.x),
					String.format(Locale.ROOT, "%f", targetVelocity.y),
					String.format(Locale.ROOT, "%f", targetVelocity.z)
			), true);
		} else {
			src.sendFeedback(() -> Text.translatable("commands.velocity.success.multiple",
					targets.size(),
					String.format(Locale.ROOT, "%f", targetVelocity.x),
					String.format(Locale.ROOT, "%f", targetVelocity.y),
					String.format(Locale.ROOT, "%f", targetVelocity.z)
			), true);
		}

		return targets.size();
	}

	private static void ClearChatCommand(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("clearchat")
			.requires(s -> s.hasPermissionLevel(2))
				.executes(ctx -> clearChat(ctx.getSource(), Collections.singleton((ctx.getSource()).getPlayerOrThrow())))
			.then(
				CommandManager.argument("targets", EntityArgumentType.players())
					.executes(ctx -> clearChat(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "targets")))
			)
		);
	}

	private static int clearChat(ServerCommandSource src, Collection<? extends ServerPlayerEntity> targets)
	{
		for (ServerPlayerEntity player : targets)
		{
			player.getServer().execute(() -> ServerPlayNetworking.send(player, new Payloads.EmptyPayload(CLEARCHAT)));
		}

		src.sendFeedback(() -> Text.translatable("commands.chatclear.success"), true);

		return targets.size();
	}

	private static void PerspectiveCommand(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("perspective")
				.requires(s -> s.hasPermissionLevel(2))
				.then(
					CommandManager.argument("perspective", PerspectiveArgumentType.playerPerspective())
						.executes(ctx -> setPerspective(ctx.getSource(), Collections.singleton((ctx.getSource()).getPlayerOrThrow()), PerspectiveArgumentType.getPlayerPerspective(ctx, "perspective")))
				)
				.then(
						CommandManager.argument("targets", EntityArgumentType.players())
						.then(
							CommandManager.argument("perspective", PerspectiveArgumentType.playerPerspective())
								.executes(ctx -> setPerspective(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "targets"), PerspectiveArgumentType.getPlayerPerspective(ctx, "perspective")))
						)
				)
		);
	}

	private static int setPerspective(ServerCommandSource src, Collection<? extends ServerPlayerEntity> targets, PlayerPerspective perspective)
	{
		for (ServerPlayerEntity player : targets)
		{
			player.getServer().execute(() -> ServerPlayNetworking.send(player, new Payloads.IntPayload(PERSPECTIVE_DATA, perspective.tag)));
		}

		src.sendFeedback(() -> Text.translatable("commands.perspective.success"), true);

		return targets.size();
	}

	private static void InventoryCommand(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("inventory")
				.requires(s -> s.hasPermissionLevel(2))
				.then(
						CommandManager.argument("switch", OpenCloseArgumentType.openCloseState())
								.executes(ctx -> inventory(ctx.getSource(), Collections.singleton((ctx.getSource()).getPlayerOrThrow()), OpenCloseArgumentType.getOpenCloseState(ctx, "switch")))
				)
				.then(
						CommandManager.argument("targets", EntityArgumentType.players())
								.then(
										CommandManager.argument("switch", OpenCloseArgumentType.openCloseState())
												.executes(ctx -> inventory(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "targets"), OpenCloseArgumentType.getOpenCloseState(ctx, "switch")))
								)
				)
		);
	}

	private static int inventory(ServerCommandSource src, Collection<? extends ServerPlayerEntity> targets, OpenCloseState openClose)
	{
		for (ServerPlayerEntity player : targets)
		{
			player.getServer().execute(() -> ServerPlayNetworking.send(player, new Payloads.BooleanPayload(INVENTORY, openClose.equals(OpenCloseState.OPEN))));
		}

		if (openClose.equals(OpenCloseState.OPEN)) src.sendFeedback(() -> Text.translatable("commands.inventory.open"), true);
		else src.sendFeedback(() -> Text.translatable("commands.inventory.close"), true);

		return targets.size();
	}

	private static void ResetDoubleJumpCommand(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("resetDoubleJump")
				.requires(s -> s.hasPermissionLevel(2))
				.executes(ctx -> resetDoubleJump(ctx.getSource(), Collections.singleton((ctx.getSource()).getPlayerOrThrow())))
				.then(
						CommandManager.argument("targets", EntityArgumentType.players())
								.executes(ctx -> resetDoubleJump(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "targets")))
				)
		);
	}

	private static int resetDoubleJump(ServerCommandSource src, Collection<? extends ServerPlayerEntity> targets)
	{
		for (ServerPlayerEntity player : targets)
		{
			player.getServer().execute(() -> ServerPlayNetworking.send(player, new Payloads.EmptyPayload(RESET_DOUBLEJUMP)));
		}

		src.sendFeedback(() -> Text.translatable("commands.resetDoubleJump.success"), true);

		return targets.size();
	}

	private static void ShakeCameraCommand(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("shakeCamera")
				.requires(s -> s.hasPermissionLevel(2))
				.executes(ctx -> shakeCamera(ctx.getSource(), Collections.singleton((ctx.getSource()).getPlayerOrThrow()), 1, 20))
				.then(
						CommandManager.literal("clear")
								.executes(ctx -> shakeCamera(ctx.getSource(), Collections.singleton((ctx.getSource()).getPlayerOrThrow()), 0, 0))
				)
				.then(
						CommandManager.argument("strength", FloatArgumentType.floatArg(0, 10))
								.executes(ctx -> shakeCamera(ctx.getSource(), Collections.singleton((ctx.getSource()).getPlayerOrThrow()), FloatArgumentType.getFloat(ctx, "strength"), 20))
								.then(
										CommandManager.argument("duration", IntegerArgumentType.integer(0))
												.executes(ctx -> shakeCamera(ctx.getSource(), Collections.singleton((ctx.getSource()).getPlayerOrThrow()), FloatArgumentType.getFloat(ctx, "strength"), IntegerArgumentType.getInteger(ctx, "duration")))
								)
				)
				.then(
						CommandManager.argument("targets", EntityArgumentType.players())
								.executes(ctx -> shakeCamera(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "targets"), 1, 20))
								.then(
										CommandManager.literal("clear")
												.executes(ctx -> shakeCamera(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "targets"), 0, 0))
								)
								.then(
										CommandManager.argument("strength", FloatArgumentType.floatArg(0, 10))
												.executes(ctx -> shakeCamera(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "targets"), FloatArgumentType.getFloat(ctx, "strength"), 20))
												.then(
														CommandManager.argument("duration", IntegerArgumentType.integer(0))
																.executes(ctx -> shakeCamera(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "targets"), FloatArgumentType.getFloat(ctx, "strength"), IntegerArgumentType.getInteger(ctx, "duration")))
												)
								)
				)
		);
	}

	private static int shakeCamera(ServerCommandSource src, Collection<? extends ServerPlayerEntity> targets, float strength, int duration)
	{
		for (ServerPlayerEntity player : targets)
		{
			player.getServer().execute(() -> ServerPlayNetworking.send(player, new Payloads.FloatIntPayload(SHAKE_CAMERA, strength, duration)));
		}

		src.sendFeedback(() -> Text.translatable("commands.shakeCamera.success"), true);

		return targets.size();
	}

	private static void FovCommand(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("fov")
				.requires(s -> s.hasPermissionLevel(2))
				.then(
						CommandManager.argument("operation", OperationArgumentType.operation())
								.then(
										CommandManager.argument("value", FloatArgumentType.floatArg())
												.executes(ctx -> setFov(ctx.getSource(), Collections.singleton((ctx.getSource()).getPlayerOrThrow()), OperationArgumentType.getOperation(ctx, "operation"), FloatArgumentType.getFloat(ctx, "value")))
												.then(
														CommandManager.argument("targets", EntityArgumentType.players())
																.executes(ctx -> setFov(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "targets"), OperationArgumentType.getOperation(ctx, "operation"), FloatArgumentType.getFloat(ctx, "value")))
												)
								)

				)
		);
	}

	private static int setFov(ServerCommandSource src, Collection<? extends ServerPlayerEntity> targets, Operation operation, float fov)
	{
		if(operation == Operation.DIVIDE && fov == 0) {
			src.sendError(Text.translatable("commands.fov.divisionbyzero"));
			return 0;
		}

		for (ServerPlayerEntity player : targets)
		{
			player.getServer().execute(() -> ServerPlayNetworking.send(player, new Payloads.FloatIntPayload(FOV_MODIFIER, fov, operation.tag)));
		}

		src.sendFeedback(() -> Text.translatable("commands.fov.success"), true);

		return targets.size();
	}

	private static void FastFallCommand(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("fastFall")
				.requires(s -> s.hasPermissionLevel(2))
				.then(
						CommandManager.argument("enabled", BoolArgumentType.bool())
								.executes(ctx -> fastFall(ctx.getSource(), Collections.singleton((ctx.getSource()).getPlayerOrThrow()), BoolArgumentType.getBool(ctx, "enabled")))
				)
				.then(
						CommandManager.argument("targets", EntityArgumentType.players())
								.then(
										CommandManager.argument("enabled", BoolArgumentType.bool())
												.executes(ctx -> fastFall(ctx.getSource(), EntityArgumentType.getPlayers(ctx, "targets"), BoolArgumentType.getBool(ctx, "enabled")))
								)
				)
		);
	}

	private static int fastFall(ServerCommandSource src, Collection<? extends ServerPlayerEntity> targets, boolean value)
	{
		for (ServerPlayerEntity player : targets)
		{
			PlayerData playerState = DataManager.getPlayerState(player);
			playerState.fastFall = value;

			player.getServer().execute(() -> ServerPlayNetworking.send(player, new Payloads.BooleanPayload(FAST_FALL, value)));
		}

		src.sendFeedback(() -> Text.translatable("commands.fastFall.success", String.valueOf(value)), true);

		return targets.size();
	}
}
