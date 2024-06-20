package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.minecraft.entity.Entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Shadow public abstract World getWorld();
    @Shadow public abstract boolean isInFluid();
    @Shadow public abstract boolean isSprinting();
    @Shadow public abstract Vec3d getRotationVecClient();
    @Shadow public abstract boolean isOnGround();
    @Shadow public abstract BlockPos getBlockPos();
    @Shadow public abstract Vec3d getVelocity();
    @Shadow @Nullable public abstract MinecraftServer getServer();
    @Shadow public abstract void setVelocity(double x, double y, double z);
    @Shadow public abstract void setPitch(float pitch);
    @Shadow public abstract float getPitch();
    @Shadow public abstract void setYaw(float yaw);
    @Shadow public abstract float getYaw();
    @Shadow public float prevPitch;
    @Shadow public float prevYaw;
    @Shadow public @Nullable abstract Entity getVehicle();
    @Shadow public @Nullable abstract LivingEntity getControllingPassenger();

    @Shadow public float fallDistance;

    @Inject(at = @At("HEAD"), method = "changeLookDirection(DD)V", cancellable = true)
    protected void onLookDirectionChange(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) { }
}
