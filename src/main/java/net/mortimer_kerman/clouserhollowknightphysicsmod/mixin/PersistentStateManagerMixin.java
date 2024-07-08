package net.mortimer_kerman.clouserhollowknightphysicsmod.mixin;

import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PersistentStateManager.class)
public class PersistentStateManagerMixin {
    @Shadow @Final private Map<String, PersistentState> loadedStates;

    @Inject(method = "save", at=@At(value = "HEAD"))
    public void onSave(CallbackInfo ci)
    {
        loadedStates.forEach((id, state) -> System.out.println(id + " is " + ((state == null) ? "null" : "valid")));
    }
}
