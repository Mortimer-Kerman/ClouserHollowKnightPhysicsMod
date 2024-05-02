package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable
{
    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow @Nullable public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    @Shadow public abstract boolean isFallFlying();

    protected LivingEntityMixin(EntityType<?> type, World world) { super(type, world); }

    @ModifyVariable(method = "takeKnockback(DDD)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    protected double knockbackStrength(double strength) { return strength; }
}
