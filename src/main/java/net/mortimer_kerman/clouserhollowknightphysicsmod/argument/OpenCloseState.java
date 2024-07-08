package net.mortimer_kerman.clouserhollowknightphysicsmod.argument;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum OpenCloseState implements StringIdentifiable
{
    OPEN("open", 0),
    CLOSE("close", 1);

    public static final Codec<OpenCloseState> CODEC;
    public final String id;
    public final int tag;

    OpenCloseState(String id, int tag) { this.id = id; this.tag = tag; }

    @Override
    public String asString() {
        return this.id;
    }

    static {
        CODEC = StringIdentifiable.createCodec(OpenCloseState::values);
    }
}
