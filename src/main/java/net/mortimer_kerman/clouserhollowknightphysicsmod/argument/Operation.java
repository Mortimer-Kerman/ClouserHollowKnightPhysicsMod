package net.mortimer_kerman.clouserhollowknightphysicsmod.argument;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum Operation implements StringIdentifiable
{
    SET("set", 0),
    ADD("add", 1),
    REMOVE("remove", 2),
    MULTIPLY("multiply", 3),
    DIVIDE("divide", 4);

    public static final Codec<Operation> CODEC;
    public final String id;
    public final int tag;

    Operation(String id, int tag) { this.id = id; this.tag = tag; }

    @Override
    public String asString() {
        return this.id;
    }

    static {
        CODEC = StringIdentifiable.createCodec(Operation::values);
    }

    public static Operation fromInt(int value) {
        return switch (value) {
            case 1 -> ADD;
            case 2 -> REMOVE;
            case 3 -> MULTIPLY;
            case 4 -> DIVIDE;
            default -> SET;
        };
    }
}
