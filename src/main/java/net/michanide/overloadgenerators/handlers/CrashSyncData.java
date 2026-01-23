package net.michanide.overloadgenerators.handlers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class CrashSyncData extends SavedData{
    public static final String DATA_NAME = "overgen_crash_checker";
    public static final String CLEAN_SHUTDOWN_KEY = "cleanShutdown";
    public static final String TOTAL_CRASH_COUNT_KEY = "totalCrashCount";
    
    private boolean cleanShutdown = true;
    private long totalCrashCount = 0L;
    
    // Standard Forge SavedData boilerplate
    public static CrashSyncData load(CompoundTag nbt) {
        CrashSyncData data = new CrashSyncData();
        data.cleanShutdown = nbt.getBoolean(CLEAN_SHUTDOWN_KEY);
        data.totalCrashCount = nbt.getLong(TOTAL_CRASH_COUNT_KEY);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putBoolean(CLEAN_SHUTDOWN_KEY, cleanShutdown);
        nbt.putLong(TOTAL_CRASH_COUNT_KEY, totalCrashCount);
        return nbt;
    }

    public boolean wasClean() {
        return cleanShutdown;
    }

    public void setClean(boolean value) {
        this.cleanShutdown = value;
        this.setDirty(); // Ensures it saves to disk
    }

    public long getTotalCrashCount() {
        return totalCrashCount;
    }

    public void setTotalCrashCount(long time) {
        this.totalCrashCount = time;
        this.setDirty(); // Ensures it saves to disk
    }
}
