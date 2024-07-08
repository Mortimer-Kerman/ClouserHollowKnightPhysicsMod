package net.mortimer_kerman.clouserhollowknightphysicsmod.argument;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public class OpenCloseArgumentType extends EnumArgumentType<OpenCloseState>
{
    private OpenCloseArgumentType() {
        super(OpenCloseState.CODEC, OpenCloseState::values);
    }

    public static EnumArgumentType<OpenCloseState> openCloseState() {
        return new OpenCloseArgumentType();
    }

    public static OpenCloseState getOpenCloseState(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, OpenCloseState.class);
    }
}

