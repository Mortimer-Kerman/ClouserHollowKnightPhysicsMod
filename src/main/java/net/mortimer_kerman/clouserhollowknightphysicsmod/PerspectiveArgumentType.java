package net.mortimer_kerman.clouserhollowknightphysicsmod;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public class PerspectiveArgumentType extends EnumArgumentType<PlayerPerspective>
{
    private PerspectiveArgumentType() {
        super(PlayerPerspective.CODEC, PlayerPerspective::values);
    }

    public static EnumArgumentType<PlayerPerspective> playerPerspective() {
        return new PerspectiveArgumentType();
    }

    public static PlayerPerspective getPlayerPerspective(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, PlayerPerspective.class);
    }
}

