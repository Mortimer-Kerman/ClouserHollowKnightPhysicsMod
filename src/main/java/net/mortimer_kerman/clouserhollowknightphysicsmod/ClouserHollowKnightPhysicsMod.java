package net.mortimer_kerman.clouserhollowknightphysicsmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.Blocks;
import net.minecraft.command.argument.*;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class ClouserHollowKnightPhysicsMod implements ModInitializer
{
	public static final String MOD_ID = "clouser-hollowknight-physics-mod";

	public static final Identifier PHYSICS_ON = new Identifier(MOD_ID, "physics_on");
	public static final Identifier MOVEMENT_ON = new Identifier(MOD_ID, "movement_on");
	public static final Identifier KNOCKBACK_ON = new Identifier(MOD_ID, "knockback_on");
	public static final Identifier AXIS_X_ON = new Identifier(MOD_ID, "axis_x_on");
	public static final Identifier AXIS_Y_ON = new Identifier(MOD_ID, "axis_y_on");
	public static final Identifier DOUBLEJUMP_ON = new Identifier(MOD_ID, "doublejump_on");
	public static final Identifier CANJUMP_ON = new Identifier(MOD_ID, "canjump_on");
	public static final Identifier PERSPECTIVE_LOCKED_ON = new Identifier(MOD_ID, "perspectivelocked_on");
	public static final Identifier HOLLOWKNIGHT_JUMP_ON = new Identifier(MOD_ID, "hollowknight_jump_on");
	public static final Identifier ZKEYS_LOOK_ON = new Identifier(MOD_ID, "zkeys_look_on");
	public static final Identifier PLAYER_STEP_HEIGHT = new Identifier(MOD_ID, "player_step_height");

	public static final Identifier KNOCKBACK_COOLDOWN = new Identifier(MOD_ID, "knockback_timer");
	public static final Identifier CLEARCHAT = new Identifier(MOD_ID, "clearchat");
	public static final Identifier PERSPECTIVE_DATA = new Identifier(MOD_ID, "perspective_data");
	public static final Identifier JUMP_RECORD = new Identifier(MOD_ID, "jump_record");
	public static final Identifier VELOCITY_CHANGE = new Identifier(MOD_ID, "velocity_change");
	public static final Identifier ZKEY_PRESS = new Identifier(MOD_ID, "zkey_press");

	public static final GameRules.Key<GameRules.BooleanRule> PHYSICS_GAMERULE = GameRuleRegistry.register("hollowKnightPhysicsOn", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<GameRules.BooleanRule> MOVEMENT_GAMERULE = GameRuleRegistry.register("canPlayerMove", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> KNOCKBACK_GAMERULE = GameRuleRegistry.register("isKnockbackOn", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> AXIS_X_GAMERULE = GameRuleRegistry.register("axisXenabled", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> AXIS_Y_GAMERULE = GameRuleRegistry.register("axisYenabled", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> DOUBLEJUMP_GAMERULE = GameRuleRegistry.register("canDoubleJump", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<GameRules.BooleanRule> CANJUMP_GAMERULE = GameRuleRegistry.register("canPlayerJump", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> PERSPECTIVE_LOCKED_GAMERULE = GameRuleRegistry.register("perspectiveLocked", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<GameRules.BooleanRule> HOLLOWKNIGHT_JUMP_GAMERULE = GameRuleRegistry.register("hollowKnightJump", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<GameRules.BooleanRule> ZKEYS_LOOK_GAMERULE = GameRuleRegistry.register("zKeysLookOn", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<DoubleRule> PLAYER_STEP_GAMERULE = GameRuleRegistry.register("playerStepHeight", GameRules.Category.MISC, GameRuleFactory.createDoubleRule(0.6D, 0));

	@Override
	public void onInitialize()
	{
		ArgumentTypeRegistry.registerArgumentType(new Identifier(MOD_ID, "template_perspective"), PerspectiveArgumentType.class, ConstantArgumentSerializer.of(PerspectiveArgumentType::playerPerspective));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> VelocityCommand(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ClearChatCommand(dispatcher));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> PerspectiveCommand(dispatcher));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();

			PacketByteBuf physics = PacketByteBufs.create();
			physics.writeBoolean(server.getGameRules().getBoolean(PHYSICS_GAMERULE));

			PacketByteBuf movement = PacketByteBufs.create();
			movement.writeBoolean(server.getGameRules().getBoolean(MOVEMENT_GAMERULE));

			PacketByteBuf knockback = PacketByteBufs.create();
			knockback.writeBoolean(server.getGameRules().getBoolean(KNOCKBACK_GAMERULE));

			PacketByteBuf axisX = PacketByteBufs.create();
			axisX.writeBoolean(server.getGameRules().getBoolean(AXIS_X_GAMERULE));

			PacketByteBuf axisY = PacketByteBufs.create();
			axisY.writeBoolean(server.getGameRules().getBoolean(AXIS_Y_GAMERULE));

			PacketByteBuf doubleJump = PacketByteBufs.create();
			doubleJump.writeBoolean(server.getGameRules().getBoolean(DOUBLEJUMP_GAMERULE));

			PacketByteBuf canJump = PacketByteBufs.create();
			canJump.writeBoolean(server.getGameRules().getBoolean(CANJUMP_GAMERULE));

			PacketByteBuf perspectiveLocked = PacketByteBufs.create();
			perspectiveLocked.writeBoolean(server.getGameRules().getBoolean(PERSPECTIVE_LOCKED_GAMERULE));

			PacketByteBuf hollowKnightJump = PacketByteBufs.create();
			hollowKnightJump.writeBoolean(server.getGameRules().getBoolean(HOLLOWKNIGHT_JUMP_GAMERULE));

			PacketByteBuf zKeysLookOn = PacketByteBufs.create();
			zKeysLookOn.writeBoolean(server.getGameRules().getBoolean(ZKEYS_LOOK_GAMERULE));

			PacketByteBuf playerStepHeight = PacketByteBufs.create();
			playerStepHeight.writeFloat((float)server.getGameRules().get(PLAYER_STEP_GAMERULE).get());

			server.execute(() -> {
				ServerPlayNetworking.send(player, PHYSICS_ON, physics);
				ServerPlayNetworking.send(player, MOVEMENT_ON, movement);
				ServerPlayNetworking.send(player, KNOCKBACK_ON, knockback);
				ServerPlayNetworking.send(player, AXIS_X_ON, axisX);
				ServerPlayNetworking.send(player, AXIS_Y_ON, axisY);
				ServerPlayNetworking.send(player, DOUBLEJUMP_ON, doubleJump);
				ServerPlayNetworking.send(player, CANJUMP_ON, canJump);
				ServerPlayNetworking.send(player, PERSPECTIVE_LOCKED_ON, perspectiveLocked);
				ServerPlayNetworking.send(player, HOLLOWKNIGHT_JUMP_ON, hollowKnightJump);
				ServerPlayNetworking.send(player, ZKEYS_LOOK_ON, zKeysLookOn);
				ServerPlayNetworking.send(player, PLAYER_STEP_HEIGHT, playerStepHeight);
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.JUMP_RECORD, (server, player, handler, buf, sender) -> player.incrementStat(Stats.JUMP));

		ServerPlayNetworking.registerGlobalReceiver(ClouserHollowKnightPhysicsMod.ZKEY_PRESS, (server, player, handler, buf, sender) -> {
			server.execute(() -> {
				int code = buf.readInt();
				if (code == 0) player.getWorld().setBlockState(new BlockPos(13, -40, 13), Blocks.REDSTONE_BLOCK.getDefaultState(), 3);
				if (code == 1) player.getWorld().setBlockState(new BlockPos(13, -40, 17), Blocks.REDSTONE_BLOCK.getDefaultState(), 3);
				if (code == 2) player.getWorld().setBlockState(new BlockPos(13, -40, 15), Blocks.REDSTONE_BLOCK.getDefaultState(), 3);
				if (code == 3) player.getWorld().setBlockState(new BlockPos(13, -40, 15), Blocks.REDSTONE_BLOCK.getDefaultState(), 3);
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
					PacketByteBuf data = PacketByteBufs.create();
					data.writeInt(pausePhysicsTicks);

					player.getServer().execute(() -> ServerPlayNetworking.send(player, KNOCKBACK_COOLDOWN, data));
				}

				PacketByteBuf data = PacketByteBufs.create();
				data.writeVec3d(new Vec3d(velocity.isXRelative() ? 1 : 0, velocity.isYRelative() ? 1 : 0, velocity.isZRelative() ? 1 : 0));
				data.writeVec3d(velocityArg);

				player.getServer().execute(() -> ServerPlayNetworking.send(player, VELOCITY_CHANGE, data));
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
				.executes(ctx -> clearChat(ctx.getSource(), Collections.singleton((ctx.getSource()).getEntityOrThrow())))
			.then(
				CommandManager.argument("targets", EntityArgumentType.entities())
					.executes(ctx -> clearChat(ctx.getSource(), EntityArgumentType.getEntities(ctx, "targets")))
			)
		);
	}

	private static int clearChat(ServerCommandSource src, Collection<? extends Entity> targets)
	{
		for (Entity entity : targets)
		{
			if (entity instanceof ServerPlayerEntity player)
			{
				player.getServer().execute(() -> ServerPlayNetworking.send(player, CLEARCHAT, PacketByteBufs.create()));
			}
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
						.executes(ctx -> setPerspective(ctx.getSource(), Collections.singleton((ctx.getSource()).getEntityOrThrow()), PerspectiveArgumentType.getPlayerPerspective(ctx, "perspective")))
				)
				.then(
						CommandManager.argument("targets", EntityArgumentType.entities())
						.then(
							CommandManager.argument("perspective", PerspectiveArgumentType.playerPerspective())
								.executes(ctx -> setPerspective(ctx.getSource(), EntityArgumentType.getEntities(ctx, "targets"), PerspectiveArgumentType.getPlayerPerspective(ctx, "perspective")))
						)
				)
		);
	}

	private static int setPerspective(ServerCommandSource src, Collection<? extends Entity> targets, PlayerPerspective perspective)
	{
		for (Entity entity : targets)
		{
			if (entity instanceof ServerPlayerEntity player)
			{
				PacketByteBuf data = PacketByteBufs.create();
				data.writeInt(perspective.tag);

				player.getServer().execute(() -> ServerPlayNetworking.send(player, PERSPECTIVE_DATA, data));
			}
		}

		src.sendFeedback(() -> Text.translatable("commands.perspective.success"), true);

		return targets.size();
	}
}
