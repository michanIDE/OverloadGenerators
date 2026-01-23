package net.michanide.overloadgenerators.handlers;

import net.michanide.overloadgenerators.OverloadGenerators;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OverloadGenerators.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerLifecycleHandler {
    public static final long NOT_READY = -1L;

    private static boolean crashDetected = false;
    private static long totalCrashCount = NOT_READY;

    // TODO: Change event from ServerStartedEvent to tick event
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel level = event.getServer().overworld();
        DimensionDataStorage storage = level.getDataStorage();
        CrashSyncData crashSyncData = storage.computeIfAbsent(CrashSyncData::load, CrashSyncData::new, CrashSyncData.DATA_NAME);

        if (!crashSyncData.wasClean()) {
            crashDetected = true;
            totalCrashCount = crashSyncData.getTotalCrashCount() + 1;
            crashSyncData.setTotalCrashCount(totalCrashCount);
        } else {
            crashDetected = false;
            totalCrashCount = crashSyncData.getTotalCrashCount();
        }

        // Set to false immediately for the current session
        crashSyncData.setClean(false);
        storage.save(); // Ensure data is saved
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ServerLevel level = event.getServer().overworld();
        CrashSyncData data = level.getDataStorage().computeIfAbsent(CrashSyncData::load, CrashSyncData::new, CrashSyncData.DATA_NAME);
        
        // This only runs if the server doesn't crash
        data.setClean(true);
    }

    public static boolean getCrashDetected() {
        return crashDetected;
    }

    public static long getTotalCrashCount() {
        return totalCrashCount;
    }
    
}
