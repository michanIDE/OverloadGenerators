package net.michanide.overloadgenerators.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;

public class ServerCrashGeneratorMapData extends SavedData{
    // Constants
    public static final String DATA_NAME = "overgen_server_crash_generator_data";
    public static final int NOT_READY = -2;
    public static final int NO_ENTRY = -1;

    private final Map<UUID, Integer> generatorCrashCounts = new HashMap<>();

    private boolean initialized = false;

    public static ServerCrashGeneratorMapData load(CompoundTag nbt) {
        ServerCrashGeneratorMapData data = new ServerCrashGeneratorMapData();

        ListTag crashCountsList = nbt.getList("generatorCrashCounts", net.minecraft.nbt.Tag.TAG_COMPOUND);
        for (int i = 0; i < crashCountsList.size(); i++) {
            CompoundTag entryTag = crashCountsList.getCompound(i);
            String uuidString = entryTag.getString("UUID");
            UUID uuid = UUID.fromString(uuidString);
            int count = entryTag.getInt("count");
            data.generatorCrashCounts.put(uuid, count);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag crashCountsList = new ListTag();
        for (Map.Entry<UUID, Integer> entry : generatorCrashCounts.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("UUID", entry.getKey().toString());
            entryTag.putInt("count", entry.getValue());
            crashCountsList.add(entryTag);
        }
        nbt.put("generatorCrashCounts", crashCountsList);
        return nbt;
    }

    public int getOrSubscribeCrashCount(UUID generatorUUID) {
        if (!initialized) {
            return NOT_READY;
        }
        
        if (!generatorCrashCounts.containsKey(generatorUUID)) {
            generatorCrashCounts.put(generatorUUID, 0);
            this.setDirty();
            return 0;
        } else {
            return generatorCrashCounts.get(generatorUUID);
        }
    }

    void updateGeneratorsCrashCount() {
        for (UUID generatorUUID : generatorCrashCounts.keySet()) {
            int currentCount = generatorCrashCounts.get(generatorUUID);
            generatorCrashCounts.put(generatorUUID, currentCount + 1);
        }
        this.setDirty();
    }

    // Increaces crash count for all generators by 1
    public void increaseGeneratorsCrashCount(){
        throw new UnsupportedOperationException("Successfully increased crash counts by actually crashing the server!");
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean value) {
        this.initialized = value;
    }
}
