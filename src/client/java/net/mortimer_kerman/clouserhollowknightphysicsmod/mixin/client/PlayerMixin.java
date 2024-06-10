package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;

import net.mortimer_kerman.clouserhollowknightphysicsmod.ClouserHollowKnightPhysicsMod;
import net.mortimer_kerman.clouserhollowknightphysicsmod.ClouserHollowKnightPhysicsModClient;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntityMixin
{
    @Shadow @Final private PlayerAbilities abilities;

    @Shadow public abstract void addExhaustion(float exhaustion);

    @Inject(at = @At("HEAD"), method = "tick()V")
    private void tick(CallbackInfo info)
    {
        if (!getWorld().isClient) return;

        if (ClouserHollowKnightPhysicsModClient.knockbackCooldown > 0)
        {
            ClouserHollowKnightPhysicsModClient.knockbackCooldown--;
            if (IsKnockbackOn()) return;
        }

        if(abilities.flying || isFallFlying() || isInFluid()) return;

        Vec3d velocity = getVelocity();

        double motionX = velocity.x;
        double motionY = velocity.y;
        double motionZ = velocity.z;

        if (ArePhysicsOn())
        {
            boolean hasSpeed = hasStatusEffect(StatusEffects.SPEED);
            StatusEffectInstance speedEffect = getStatusEffect(StatusEffects.SPEED);
            double speedBonus = (hasSpeed && (speedEffect != null) ? (speedEffect.getAmplifier() + 1) : 0)*0.2D;

            boolean hasSlowness = hasStatusEffect(StatusEffects.SLOWNESS);
            StatusEffectInstance slownEffect = getStatusEffect(StatusEffects.SLOWNESS);
            double slownMalus = MathHelper.clamp((hasSlowness && (slownEffect != null) ? (slownEffect.getAmplifier() + 1) : 0)*0.15D,0D,1D);

            double walkSpeed = isSprinting() ? 0.280 : 0.215;

            Vec3d forwardVec = getRotationVecClient().withAxis(Direction.Axis.Y,0).normalize().multiply(walkSpeed*(1+speedBonus)*(1-slownMalus));
            Vec3d leftVec = forwardVec.rotateY((float)Math.PI/2);

            Vec3d inputVec = Vec3d.ZERO;

            if (MinecraftClient.getInstance().options.forwardKey.isPressed()) inputVec = inputVec.add(forwardVec);
            if (MinecraftClient.getInstance().options.backKey.isPressed()) inputVec = inputVec.subtract(forwardVec);
            if (MinecraftClient.getInstance().options.leftKey.isPressed()) inputVec = inputVec.add(leftVec);
            if (MinecraftClient.getInstance().options.rightKey.isPressed()) inputVec = inputVec.subtract(leftVec);

            double slipperness = 0.91;
            if (isOnGround()) slipperness = getWorld().getBlockState(getBlockPos().down()).getBlock().getSlipperiness() * 0.91;

            motionX = inputVec.x * slipperness;
            motionY = inputVec.y + (motionY / 0.98);
            motionZ = inputVec.z * slipperness;
        }

        boolean startingJump = false;

        if(HollowKnightJumpOn())
        {
            if (MinecraftClient.getInstance().options.jumpKey.isPressed() && CanJump())
            {
                if(!isJumping)
                {
                    if (jumpsAmount < 1 || (DoubleJumpOn() && jumpsAmount < 2))
                    {
                        jumpsAmount++;
                        jumpTicks = 6;
                        fallDistance = 0;
                        recordJump();
                        startingJump = true;
                    }
                    isJumping = true;
                }

                if (jumpTicks > 0)
                {
                    jumpTicks--;
                    motionY = 0.4;
                }
            }
            else
            {
                jumpTicks = 0;
                isJumping = false;
            }

            if (isOnGround() && !startingJump)
            {
                jumpsAmount = 0;
                isJumping = false;
            }
            else if (jumpsAmount == 0) jumpsAmount = 1;
        }

        setVelocity(motionX, motionY, motionZ);
    }

    private int jumpTicks = 0;
    private int jumpsAmount = 0;
    private boolean isJumping = false;

    @Override
    protected double knockbackStrength(double strength)
    {
        if (!IsKnockbackOn()) return 0;

        if (getWorld().isClient) return strength;

        MinecraftServer server = getServer();
        if (server == null) return strength;

        PacketByteBuf data = PacketByteBufs.create();
        data.writeInt(20);

        server.execute(() -> ServerPlayNetworking.send(((ServerPlayerEntity)(Object)this), ClouserHollowKnightPhysicsMod.KNOCKBACK_COOLDOWN, data));
        return strength;
    }

    @Override
    protected void onLookDirectionChange(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci)
    {
        ci.cancel();
        float f = (float)cursorDeltaY * 0.15f;
        float g = (float)cursorDeltaX * 0.15f;

        if (!ClouserHollowKnightPhysicsModClient.axisYOn) f = 0;
        if (!ClouserHollowKnightPhysicsModClient.axisXOn) g = 0;

        this.setPitch(this.getPitch() + f);
        this.setYaw(this.getYaw() + g);
        this.setPitch(MathHelper.clamp(this.getPitch(), -90.0f, 90.0f));
        this.prevPitch += f;
        this.prevYaw += g;
        this.prevPitch = MathHelper.clamp(this.prevPitch, -90.0f, 90.0f);
        if (this.getVehicle() != null) {
            this.getVehicle().onPassengerLookAround(((PlayerEntity)(Object)this));
        }
    }

    @Inject(at = @At("HEAD"), method = "jump()V", cancellable = true)
    protected void onJump(CallbackInfo ci)
    {
        if (HollowKnightJumpOn() || !CanJump()) ci.cancel();
    }

    private void recordJump()
    {
        MinecraftClient.getInstance().execute(() -> ClientPlayNetworking.send(ClouserHollowKnightPhysicsMod.JUMP_RECORD, PacketByteBufs.create()));
        if (isSprinting()) addExhaustion(0.2f);
        else addExhaustion(0.05f);
    }

    private static boolean ArePhysicsOn() { return ClouserHollowKnightPhysicsModClient.physicsOn; }
    private static boolean IsKnockbackOn() { return ClouserHollowKnightPhysicsModClient.knockbackOn; }
    private static boolean DoubleJumpOn() { return ClouserHollowKnightPhysicsModClient.doubleJumpOn; }
    private static boolean CanJump() { return ClouserHollowKnightPhysicsModClient.canJump; }
    private static boolean HollowKnightJumpOn() { return ClouserHollowKnightPhysicsModClient.hollowKnightJump; }
}
