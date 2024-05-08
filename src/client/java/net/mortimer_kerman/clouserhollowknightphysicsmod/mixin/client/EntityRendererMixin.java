package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity>
{
    @Shadow protected abstract int getBlockLight(T entity, BlockPos pos);

    @Inject(at = @At("HEAD"), method = "getLight(Lnet/minecraft/entity/Entity;F)I", cancellable = true)
    private void getLight(T entity, float tickDelta, CallbackInfoReturnable<Integer> cir)
    {
        BlockPos blockPos = BlockPos.ofFloored(entity.getClientCameraPosVec(tickDelta));
        cir.setReturnValue(LightmapTextureManager.pack(getBlockLight(entity, blockPos), 15));
    }
}
