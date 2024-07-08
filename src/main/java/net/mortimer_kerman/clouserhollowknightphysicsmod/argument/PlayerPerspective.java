package net.mortimer_kerman.clouserhollowknightphysicsmod.argument;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringIdentifiable;

public enum PlayerPerspective implements StringIdentifiable
{
    FIRST_PERSON("firstPerson", 0),
    THIRD_PERSON_BACK("thirdPersonBack", 1),
    THIRD_PERSON_FRONT("thirdPersonFront", 2);

    public static final Codec<PlayerPerspective> CODEC;
    public final String id;
    public final int tag;

    PlayerPerspective(String id, int tag) { this.id = id; this.tag = tag; }

    @Override
    public String asString() {
        return this.id;
    }

    static {
        CODEC = StringIdentifiable.createCodec(PlayerPerspective::values);
    }
}
