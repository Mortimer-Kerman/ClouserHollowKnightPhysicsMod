package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import net.mortimer_kerman.clouserhollowknightphysicsmod.ClouserHollowKnightPhysicsMod;

import net.mortimer_kerman.clouserhollowknightphysicsmod.Payloads;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRuleCommand.class)
public class GameruleMixin
{
    @Inject(at = @At("HEAD"), method = "executeSet")
    private static <T extends GameRules.Rule<T>> void SetGravity(CommandContext<ServerCommandSource> context, GameRules.Key<T> key, CallbackInfoReturnable<Integer> cir)
    {
        String channel;
        MinecraftServer server = context.getSource().getServer();
        PacketByteBuf data = PacketByteBufs.create();

        if (key.getName().equals(ClouserHollowKnightPhysicsMod.PHYSICS_GAMERULE.getName())) channel = ClouserHollowKnightPhysicsMod.PHYSICS_ON;
        else if (key.getName().equals(ClouserHollowKnightPhysicsMod.MOVEMENT_GAMERULE.getName())) channel = ClouserHollowKnightPhysicsMod.MOVEMENT_ON;
        else if (key.getName().equals(ClouserHollowKnightPhysicsMod.KNOCKBACK_GAMERULE.getName())) channel = ClouserHollowKnightPhysicsMod.KNOCKBACK_ON;
        else if (key.getName().equals(ClouserHollowKnightPhysicsMod.AXIS_X_GAMERULE.getName())) channel = ClouserHollowKnightPhysicsMod.AXIS_X_ON;
        else if (key.getName().equals(ClouserHollowKnightPhysicsMod.AXIS_Y_GAMERULE.getName())) channel = ClouserHollowKnightPhysicsMod.AXIS_Y_ON;
        else if (key.getName().equals(ClouserHollowKnightPhysicsMod.DOUBLEJUMP_GAMERULE.getName())) channel = ClouserHollowKnightPhysicsMod.DOUBLEJUMP_ON;
        else if (key.getName().equals(ClouserHollowKnightPhysicsMod.CANJUMP_GAMERULE.getName())) channel = ClouserHollowKnightPhysicsMod.CANJUMP_ON;
        else if (key.getName().equals(ClouserHollowKnightPhysicsMod.PERSPECTIVE_LOCKED_GAMERULE.getName())) channel = ClouserHollowKnightPhysicsMod.PERSPECTIVE_LOCKED_ON;
        else if (key.getName().equals(ClouserHollowKnightPhysicsMod.HOLLOWKNIGHT_JUMP_GAMERULE.getName())) channel = ClouserHollowKnightPhysicsMod.HOLLOWKNIGHT_JUMP_ON;
        else if (key.getName().equals(ClouserHollowKnightPhysicsMod.ZKEYS_LOOK_GAMERULE.getName())) channel = ClouserHollowKnightPhysicsMod.ZKEYS_LOOK_ON;
        else if (key.getName().equals(ClouserHollowKnightPhysicsMod.PLAYER_STEP_GAMERULE.getName()))
        {
            float value = (float)DoubleArgumentType.getDouble(context, "value");

            for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                server.execute(() -> ServerPlayNetworking.send(player, new Payloads.FloatPayload(ClouserHollowKnightPhysicsMod.PLAYER_STEP_HEIGHT, value)));
            }
            return;
        }
        else return;

        boolean value = BoolArgumentType.getBool(context, "value");

        for(ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            server.execute(() -> ServerPlayNetworking.send(player, new Payloads.BooleanPayload(channel, value)));
        }
    }
}
