package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin
{
    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);
    @Shadow @Nullable public abstract StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);
    @Shadow public abstract boolean isFallFlying();

    @Shadow public abstract float getYaw(float tickDelta);

    @ModifyVariable(method = "takeKnockback(DDD)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    protected double knockbackStrength(double strength) { return strength; }

    @Inject(at = @At("RETURN"), method = "getStepHeight", cancellable = true)
    protected void stepHeight(CallbackInfoReturnable<Float> cir) { }
}
