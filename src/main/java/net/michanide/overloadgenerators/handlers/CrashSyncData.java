package net.michanide.overloadgenerators.handlers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class CrashSyncData extends SavedData{
    public static final String DATA_NAME = "overgen_crash_checker";

    private boolean cleanShutdown = true;

    // Standard Forge SavedData boilerplate
    public static CrashSyncData load(CompoundTag nbt) {
        CrashSyncData data = new CrashSyncData();
        data.cleanShutdown = nbt.getBoolean("cleanShutdown");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putBoolean("cleanShutdown", cleanShutdown);
        return nbt;
    }

    public void setClean(boolean value) {
        this.cleanShutdown = value;
        this.setDirty(); // Ensures it saves to disk
    }

    public boolean wasClean() {
        return cleanShutdown;
    }
}
