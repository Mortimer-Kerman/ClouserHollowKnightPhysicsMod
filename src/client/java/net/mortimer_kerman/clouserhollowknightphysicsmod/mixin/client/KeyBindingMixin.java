package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.minecraft.client.option.KeyBinding;

import net.mortimer_kerman.clouserhollowknightphysicsmod.ClouserHollowKnightPhysicsModClient;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class KeyBindingMixin
{
    @Shadow @Final private String translationKey;

    @Inject(at = @At("RETURN"), method = "isPressed", cancellable = true)
    public void isPressed(CallbackInfoReturnable<Boolean> cir)
    {
        boolean movementKey = translationKey.equals("key.forward")
                            ||translationKey.equals("key.left")
                            ||translationKey.equals("key.back")
                            ||translationKey.equals("key.right")
                            ||translationKey.equals("key.jump");
        cir.setReturnValue(cir.getReturnValue() && (ClouserHollowKnightPhysicsModClient.movementOn || !movementKey));
    }
}
