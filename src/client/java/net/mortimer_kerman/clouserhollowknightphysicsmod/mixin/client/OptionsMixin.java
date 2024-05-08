package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;

import net.mortimer_kerman.clouserhollowknightphysicsmod.ClouserHollowKnightPhysicsModClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class OptionsMixin
{
    @Inject(at = @At("HEAD"), method = "setPerspective", cancellable = true)
    private void onPerspectiveChange(Perspective perspective, CallbackInfo ci)
    {
        if (ClouserHollowKnightPhysicsModClient.perspectiveLocked) ci.cancel();
    }
}
