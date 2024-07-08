package net.mortimer_kerman.clouserhollowknightphysicsmod.argument;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;

public class OperationArgumentType extends EnumArgumentType<Operation>
{
    private OperationArgumentType() { super(Operation.CODEC, Operation::values); }

    public static EnumArgumentType<Operation> operation() { return new OperationArgumentType(); }

    public static Operation getOperation(CommandContext<ServerCommandSource> context, String id) { return context.getArgument(id, Operation.class); }
}

