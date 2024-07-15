package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.mortimer_kerman.clouserhollowknightphysicsmod.ClouserHollowKnightPhysicsModClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow protected abstract void moveBy(float f, float g, float h);

    @Shadow protected abstract void setPos(double x, double y, double z);

    @Shadow private Vec3d pos;
    @Unique
    private static final Random random = new Random();

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.BY, by = 1))
    private void applyShakeAndScale(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
    {
        if (ClouserHollowKnightPhysicsModClient.eyeHeight != 1)
        {
            float eyeHeight = focusedEntity.getEyeHeight(focusedEntity.getPose());
            setPos (pos.x, pos.y + eyeHeight * (ClouserHollowKnightPhysicsModClient.eyeHeight - 1), pos.z);
        }
        if (focusedEntity.getWorld().getTime() > ClouserHollowKnightPhysicsModClient.NextShakeEnd) return;
        float x = (random.nextFloat() - 0.5f) * ClouserHollowKnightPhysicsModClient.ShakeStenght;
        float y = (random.nextFloat() - 0.5f) * ClouserHollowKnightPhysicsModClient.ShakeStenght;
        moveBy(0, x, y);
    }

    @Inject(method = "clipToSpace", at = @At(value = "HEAD"), cancellable = true)
    private void changeClipping(float f, CallbackInfoReturnable<Float> cir)
    {
        if(!ClouserHollowKnightPhysicsModClient.cameraClip)
        {
            cir.setReturnValue(f);
            cir.cancel();
        }
    }
}
