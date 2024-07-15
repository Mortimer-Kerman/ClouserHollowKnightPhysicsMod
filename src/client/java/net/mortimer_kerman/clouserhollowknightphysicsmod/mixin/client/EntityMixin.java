package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.minecraft.entity.Entity;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.mortimer_kerman.clouserhollowknightphysicsmod.ClouserHollowKnightPhysicsModClient;
import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Shadow public abstract World getWorld();
    @Shadow public abstract boolean isInFluid();
    @Shadow public abstract boolean isSprinting();
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

    @Shadow public abstract float getEyeHeight(EntityPose pose);

    @Shadow public abstract EntityPose getPose();

    @Inject(at = @At("HEAD"), method = "changeLookDirection(DD)V", cancellable = true)
    protected void onLookDirectionChange(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) { }

    @Inject(method = "getCameraPosVec", at = @At(value = "RETURN"), cancellable = true)
    private void cameraPosVec(float tickDelta, CallbackInfoReturnable<Vec3d> cir)
    {
        if (ClouserHollowKnightPhysicsModClient.eyeHeight == 1) return;
        float eyeHeight = getEyeHeight(getPose());
        cir.setReturnValue(cir.getReturnValue().add(0,eyeHeight * (ClouserHollowKnightPhysicsModClient.eyeHeight - 1), 0));
    }
}
