package net.mortimer_kerman.clouserhollowknightphysicsmod;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public class Payloads
{
    public record BooleanPayload(String strId, boolean value) implements CustomPayload
    {
        public static final CustomPayload.Id<BooleanPayload> ID = new CustomPayload.Id<>(PAYLOAD_BOOL);
        public static final PacketCodec<RegistryByteBuf, BooleanPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, BooleanPayload::strId, PacketCodecs.BOOL, BooleanPayload::value, BooleanPayload::new);
        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    public record IntPayload(String strId, int value) implements CustomPayload
    {
        public static final CustomPayload.Id<IntPayload> ID = new CustomPayload.Id<>(PAYLOAD_INT);
        public static final PacketCodec<RegistryByteBuf, IntPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, IntPayload::strId, PacketCodecs.INTEGER, IntPayload::value, IntPayload::new);
        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    public record FloatPayload(String strId, float value) implements CustomPayload
    {
        public static final CustomPayload.Id<FloatPayload> ID = new CustomPayload.Id<>(PAYLOAD_FLOAT);
        public static final PacketCodec<RegistryByteBuf, FloatPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, FloatPayload::strId, PacketCodecs.FLOAT, FloatPayload::value, FloatPayload::new);
        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    public record Vec3dCouplePayload(String strId, Vec3d value1, Vec3d value2) implements CustomPayload
    {
        public static final CustomPayload.Id<Vec3dCouplePayload> ID = new CustomPayload.Id<>(PAYLOAD_VEC3D_COUPLE);
        public static final PacketCodec<RegistryByteBuf, Vec3dCouplePayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, Vec3dCouplePayload::strId, CODEC_VEC3D, Vec3dCouplePayload::value1, CODEC_VEC3D, Vec3dCouplePayload::value2, Vec3dCouplePayload::new);
        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    public record EmptyPayload(String strId) implements CustomPayload
    {
        public static final CustomPayload.Id<EmptyPayload> ID = new CustomPayload.Id<>(PAYLOAD_EMPTY);
        public static final PacketCodec<RegistryByteBuf, EmptyPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, EmptyPayload::strId, EmptyPayload::new);
        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    public static Identifier PAYLOAD_BOOL = Identifier.of(ClouserHollowKnightPhysicsMod.MOD_ID, "bool_payload");
    public static Identifier PAYLOAD_INT = Identifier.of(ClouserHollowKnightPhysicsMod.MOD_ID, "int_payload");
    public static Identifier PAYLOAD_FLOAT = Identifier.of(ClouserHollowKnightPhysicsMod.MOD_ID, "float_payload");
    public static Identifier PAYLOAD_VEC3D_COUPLE = Identifier.of(ClouserHollowKnightPhysicsMod.MOD_ID, "vec3d_couple_payload");
    public static Identifier PAYLOAD_EMPTY = Identifier.of(ClouserHollowKnightPhysicsMod.MOD_ID, "empty_payload");

    public static void RegisterPayloads() {
        PayloadTypeRegistry.playC2S().register(BooleanPayload.ID, BooleanPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BooleanPayload.ID, BooleanPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(IntPayload.ID, IntPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(IntPayload.ID, IntPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(FloatPayload.ID, FloatPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(FloatPayload.ID, FloatPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(Vec3dCouplePayload.ID, Vec3dCouplePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(Vec3dCouplePayload.ID, Vec3dCouplePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(EmptyPayload.ID, EmptyPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EmptyPayload.ID, EmptyPayload.CODEC);
    }

    public static PacketCodec<ByteBuf, Vec3d> CODEC_VEC3D = new PacketCodec<>() {
        public Vec3d decode(ByteBuf byteBuf) {
            return new Vec3d(byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble());
        }

        public void encode(ByteBuf byteBuf, Vec3d vec3d) {
            byteBuf.writeDouble(vec3d.getX());
            byteBuf.writeDouble(vec3d.getY());
            byteBuf.writeDouble(vec3d.getZ());
        }
    };
}
