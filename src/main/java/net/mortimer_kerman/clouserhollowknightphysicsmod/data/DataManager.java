package net.mortimer_kerman.clouserhollowknightphysicsmod.data;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.mortimer_kerman.clouserhollowknightphysicsmod.ClouserHollowKnightPhysicsMod;

import java.util.HashMap;
import java.util.UUID;

public class DataManager extends PersistentState
{
    public HashMap<UUID, PlayerData> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            playerNbt.putBoolean("fastFall", playerData.fastFall);

            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);

        return nbt;
    }

    public static DataManager createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
    {
        DataManager state = new DataManager();

        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();

            playerData.fastFall = playersNbt.getCompound(key).getBoolean("fastFall");

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    private static Type<DataManager> type = new Type<>(DataManager::new, DataManager::createFromNbt, null);

    public static DataManager getServerState(MinecraftServer server)
    {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
        DataManager state = persistentStateManager.getOrCreate(type, ClouserHollowKnightPhysicsMod.MOD_ID);
        state.markDirty();

        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player)
    {
        DataManager serverState = getServerState(player.getWorld().getServer());
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
    }
}
