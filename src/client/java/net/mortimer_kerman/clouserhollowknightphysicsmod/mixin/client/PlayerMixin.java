package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin.client;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

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

    private PlayerMixin(World world) {
        super(EntityType.PLAYER, world);
    }

    @Inject(at = @At("HEAD"), method = "tick()V")
    private void tick(CallbackInfo info)
    {
        if (!getWorld().isClient) return;

        if (ClouserHollowKnightPhysicsModClient.knockbackCooldown > 0)
        {
            ClouserHollowKnightPhysicsModClient.knockbackCooldown--;
            if (IsKnockbackOn()) return;
        }

        if(abilities.flying || isFallFlying() || isInFluid() || !ArePhysicsOn()) return;

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

        double motionX = inputVec.x * slipperness;
        double motionY = inputVec.y + (getVelocity().y / 0.98);
        double motionZ = inputVec.z * slipperness;

        setVelocity(motionX, motionY, motionZ);
    }

    @Override
    protected double knockbackStrength(double strength)
    {
        if (!IsKnockbackOn()) return 0;

        if (getWorld().isClient) return strength;

        MinecraftServer server = getServer();
        if (server == null) return strength;

        PacketByteBuf data = PacketByteBufs.create();
        data.writeInt(20);

        server.execute(() -> ServerPlayNetworking.send(server.getPlayerManager().getPlayer(getUuid()), ClouserHollowKnightPhysicsMod.KNOCKBACK_COOLDOWN, data));
        return strength;
    }

    private static boolean ArePhysicsOn() { return ClouserHollowKnightPhysicsModClient.physicsOn; }
    private static boolean IsMovementOn() { return ClouserHollowKnightPhysicsModClient.movementOn; }
    private static boolean IsKnockbackOn() { return ClouserHollowKnightPhysicsModClient.knockbackOn; }
}
