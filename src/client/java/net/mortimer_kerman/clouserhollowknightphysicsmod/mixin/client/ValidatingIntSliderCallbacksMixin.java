package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.minecraft.client.option.SimpleOption;
import net.mortimer_kerman.clouserhollowknightphysicsmod.ClouserHollowKnightPhysicsModClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(SimpleOption.ValidatingIntSliderCallbacks.class)
public abstract class ValidatingIntSliderCallbacksMixin
{
    @Inject(method = "validate(Ljava/lang/Integer;)Ljava/util/Optional;", at=@At("RETURN"), cancellable = true)
    public void validate(Integer integer, CallbackInfoReturnable<Optional<Integer>> cir)
    {
        if (ClouserHollowKnightPhysicsModClient.LockFOVAutoChange) {
            if(integer.compareTo(5) < 0) cir.setReturnValue(Optional.of(5));
            else if (integer.compareTo(180) > 0) cir.setReturnValue(Optional.of(180));
            else cir.setReturnValue(Optional.of(integer));
        }
    }
}
