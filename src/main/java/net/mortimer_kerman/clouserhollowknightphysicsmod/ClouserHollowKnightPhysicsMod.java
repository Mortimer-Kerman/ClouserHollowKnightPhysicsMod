package net.mortimer_kerman.clouserhollowknightphysicsmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.command.argument.*;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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

	public static final Identifier KNOCKBACK_COOLDOWN = new Identifier(MOD_ID, "knockback_timer");

	public static final GameRules.Key<GameRules.BooleanRule> PHYSICS_GAMERULE = GameRuleRegistry.register("hollowKnightPhysicsOn", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<GameRules.BooleanRule> MOVEMENT_GAMERULE = GameRuleRegistry.register("canPlayerMove", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> KNOCKBACK_GAMERULE = GameRuleRegistry.register("isKnockbackOn", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> AXIS_X_GAMERULE = GameRuleRegistry.register("axisXenabled", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> AXIS_Y_GAMERULE = GameRuleRegistry.register("axisYenabled", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> DOUBLEJUMP_GAMERULE = GameRuleRegistry.register("canDoubleJump", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));

	@Override
	public void onInitialize()
	{
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> VelocityCommand(dispatcher));
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

			server.execute(() -> {
				ServerPlayNetworking.send(player, PHYSICS_ON, physics);
				ServerPlayNetworking.send(player, MOVEMENT_ON, movement);
				ServerPlayNetworking.send(player, KNOCKBACK_ON, knockback);
				ServerPlayNetworking.send(player, AXIS_X_ON, axisX);
				ServerPlayNetworking.send(player, AXIS_Y_ON, axisY);
				ServerPlayNetworking.send(player, DOUBLEJUMP_ON, doubleJump);
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
					CommandManager.argument("pausePhysics", BoolArgumentType.bool())
						.executes(ctx -> setVelocity(ctx.getSource(), Collections.singleton((ctx.getSource()).getEntityOrThrow()), Vec3ArgumentType.getPosArgument(ctx, "velocity"), BoolArgumentType.getBool(ctx, "pausePhysics")))
				)
			)
			.then(
				CommandManager.argument("targets", EntityArgumentType.entities())
				.then(
					CommandManager.argument("velocity", Vec3ArgumentType.vec3(false))
						.executes(ctx -> setVelocity(ctx.getSource(), EntityArgumentType.getEntities(ctx, "targets"), Vec3ArgumentType.getPosArgument(ctx, "velocity")))
					.then(
						CommandManager.argument("pausePhysics", BoolArgumentType.bool())
							.executes(ctx -> setVelocity(ctx.getSource(), EntityArgumentType.getEntities(ctx, "targets"), Vec3ArgumentType.getPosArgument(ctx, "velocity"), BoolArgumentType.getBool(ctx, "pausePhysics")))
					)
				)
			)
		);
	}

	private static int setVelocity(ServerCommandSource src, Collection<? extends Entity> targets, PosArgument velocity) { return setVelocity(src, targets, velocity, false); }

	private static int setVelocity(ServerCommandSource src, Collection<? extends Entity> targets, PosArgument velocity, boolean pausePhysics)
	{
		Entity srcEntity = src.getEntity();
		Vec3d srcVelocity = (srcEntity == null) ? Vec3d.ZERO : srcEntity.getVelocity();
		if (!velocity.isXRelative()) srcVelocity = srcVelocity.withAxis(Direction.Axis.X, 0);
		if (!velocity.isYRelative()) srcVelocity = srcVelocity.withAxis(Direction.Axis.Y, 0);
		if (!velocity.isZRelative()) srcVelocity = srcVelocity.withAxis(Direction.Axis.Z, 0);

		Vec3d targetVelocity = srcVelocity.add(velocity.toAbsolutePos(src.withPosition(Vec3d.ZERO)));

		for (Entity entity : targets)
		{
			entity.setVelocity(targetVelocity);
			entity.velocityModified = true;

			if (pausePhysics && entity instanceof ServerPlayerEntity player)
			{
				PacketByteBuf data = PacketByteBufs.create();
				data.writeInt(20);

				player.getServer().execute(() -> ServerPlayNetworking.send(player, ClouserHollowKnightPhysicsMod.KNOCKBACK_COOLDOWN, data));
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
}
