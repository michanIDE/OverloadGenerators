package net.michanide.overloadgenerators.util;

import net.michanide.overloadgenerators.OverloadGenerators;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OverloadGenerators.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerLifecycleHandler {
    private static boolean crashDetected = false;

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel level = event.getServer().overworld();
        CrashSyncData data = level.getDataStorage().computeIfAbsent(CrashSyncData::load, CrashSyncData::new, "crash_checker");

        System.out.println("Previous clean shutdown: " + data.wasClean());

        if (!data.wasClean()) {
            crashDetected = true;

            // Logic to handle crash globally if needed
        }

        // Set to false immediately for the current session
        data.setClean(false);
        level.getDataStorage().save(); // Ensure data is saved
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ServerLevel level = event.getServer().overworld();
        CrashSyncData data = level.getDataStorage().computeIfAbsent(CrashSyncData::load, CrashSyncData::new, "crash_checker");
        
        // This only runs if the server doesn't crash
        data.setClean(true);
    }

    public static boolean getCrashDetected() {
        return crashDetected;
    }
    
}
