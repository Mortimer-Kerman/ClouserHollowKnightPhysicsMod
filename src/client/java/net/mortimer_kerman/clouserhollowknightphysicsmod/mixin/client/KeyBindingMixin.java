package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import net.minecraft.network.PacketByteBuf;
import net.mortimer_kerman.clouserhollowknightphysicsmod.ClouserHollowKnightPhysicsMod;
import net.mortimer_kerman.clouserhollowknightphysicsmod.ClouserHollowKnightPhysicsModClient;

import net.mortimer_kerman.clouserhollowknightphysicsmod.Payloads;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin
{
    @Shadow @Final private String translationKey;

    @Shadow private boolean pressed;

    @Inject(at = @At("RETURN"), method = "isPressed", cancellable = true)
    public void isPressed(CallbackInfoReturnable<Boolean> cir)
    {
        boolean movementKey = translationKey.equals("key.forward")
                            ||translationKey.equals("key.back")
                            ||translationKey.equals("key.left")
                            ||translationKey.equals("key.right")
                            ||translationKey.equals("key.jump");

        boolean lockedKey = ClouserHollowKnightPhysicsModClient.zKeysLookOn && (translationKey.equals("key.forward") || translationKey.equals("key.back"));

        cir.setReturnValue(cir.getReturnValue() && (ClouserHollowKnightPhysicsModClient.movementOn || !movementKey) && !lockedKey);
    }

    @Inject(at = @At("HEAD"), method = "setPressed")
    public void setPressed(boolean pressed, CallbackInfo ci)
    {
        if (!ClouserHollowKnightPhysicsModClient.zKeysLookOn) return;
        if (this.pressed == pressed) return;
        if (!translationKey.equals("key.forward") && !translationKey.equals("key.back")) return;

        int code = 5;

        if (pressed)
        {
            if (translationKey.equals("key.forward")) code = 0;
            if (translationKey.equals("key.back")) code = 1;
        }
        else
        {
            if (translationKey.equals("key.forward")) code = 2;
            if (translationKey.equals("key.back")) code = 3;
        }

        int finalCode = code;
        MinecraftClient.getInstance().execute(() -> ClientPlayNetworking.send(new Payloads.IntPayload(ClouserHollowKnightPhysicsMod.ZKEY_PRESS, finalCode)));
    }
}
