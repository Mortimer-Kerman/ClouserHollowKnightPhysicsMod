package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import net.mortimer_kerman.clouserhollowknightphysicsmod.ClouserHollowKnightPhysicsModClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow protected abstract void moveBy(float f, float g, float h);

    @Unique
    private static final Random random = new Random();

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.BY, by = 1))
    private void applyShake(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (focusedEntity.getWorld().getTime() > ClouserHollowKnightPhysicsModClient.NextShakeEnd) return;
        float x = (random.nextFloat() - 0.5f) * ClouserHollowKnightPhysicsModClient.ShakeStenght;
        float y = (random.nextFloat() - 0.5f) * ClouserHollowKnightPhysicsModClient.ShakeStenght;
        moveBy(0, x, y);
    }
}
