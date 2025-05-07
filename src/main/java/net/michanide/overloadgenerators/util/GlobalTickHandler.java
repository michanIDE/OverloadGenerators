package net.michanide.overloadgenerators.util;

import java.util.List;

import org.checkerframework.checker.units.qual.s;
import org.lwjgl.system.CallbackI.S;

import net.michanide.overloadgenerators.OverloadGenerators;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.PowerSource;

@Mod.EventBusSubscriber(modid = OverloadGenerators.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlobalTickHandler {
    private static double cachedCPUUsage = 0;
    private static long cachedTickTime = 0;
    // private static double cachedPowerUsage = 0;
    private static long lastTick = -1;
    private static int systemInfoCounter = 20;
    private static int tickTimeCounter = 0;
    private static int tickTimeCounterMax = 100;
    private static long[] prevTicks = null;
    private static boolean isSystemInfoActive = true;
    private static boolean isTickTimeActive = true;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        long currentTick = event.world.getGameTime();
        if (currentTick != lastTick && event.phase == TickEvent.Phase.END && !event.world.isClientSide) {

            lastTick = currentTick;

            // SystemInfo 
            if (systemInfoCounter > 19) {
                systemInfoCounter = 0;

                if (isSystemInfoActive) {
                    try {
                        SystemInfo systemInfo = new SystemInfo();
                        
                        // CPU Usage
                        cachedCPUUsage = getCPUUsage(systemInfo);

                        // Power Usage
                        // cachedPowerUsage = getPowerUsage(systemInfo);
                        // System.out.println(cachedPowerUsage);

                        isSystemInfoActive = false;

                    } catch (Exception e) {
                        cachedCPUUsage = 0.0;
                        // cachedPowerUsage = 0.0;
                        isSystemInfoActive = false;
                    }
                }
            } else {
                systemInfoCounter++;
            }

            // Tick Time
            if (isTickTimeActive) {
                MinecraftServer server = event.world.getServer();
                if (server != null) {
                    long[] tickTimes = server.tickTimes;
                    cachedTickTime = tickTimes[tickTimeCounter]; // Last tick time in ns
                }
                if(cachedTickTime > 0){
                    isTickTimeActive = false;
                }
            }
            tickTimeCounter++;
            if (tickTimeCounter >= tickTimeCounterMax) {
                tickTimeCounter = 0;
            }
        }
    }

    private static double getCPUUsage(SystemInfo systemInfo) {
        
        double cpuUsage;

        try{
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            if(prevTicks != null){
                cpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks);
            } else {
                cpuUsage = 0.0;
            }
            prevTicks = processor.getSystemCpuLoadTicks();
        } catch (Exception e){
            cpuUsage = 0.0;
        }
        return cpuUsage;
    }

    // private static double getPowerUsage(SystemInfo systemInfo) {
    //     try{
    //         List<PowerSource> powerSources = systemInfo.getHardware().getPowerSources(); 
    //         double powerUsage = 0.0;
    //         for(PowerSource powerSource : powerSources){
    //             powerUsage += powerSource.getPowerUsageRate();
    //             System.out.print(powerSource.getName());
    //             System.out.println(" Added");
    //         }
    //         return powerUsage;
    //     } catch (Exception e){
    //         System.out.println("cannnot get power usage");
    //         return 0.0;
    //     }
    // }

    public static double getCachedCPUUsage() {
        isSystemInfoActive = true;
        return cachedCPUUsage;
    }

    public static long getCachedTickTime() {
        isTickTimeActive = true;
        return cachedTickTime;
    }
}