package net.michanide.overloadgenerators.util;

import net.michanide.overloadgenerators.OverloadGenerators;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

@Mod.EventBusSubscriber(modid = OverloadGenerators.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlobalTickHandler {
    private static double cachedCPUUsage = 0;
    private static long cachedTickTime = 0;
    private static long lastTick = -1;
    private static int CPUUsageCounter = 20;
    private static int tickTimeCounter = 0;
    private static int tickTimeCounterMax = 100;
    private static long[] prevTicks = null;
    private static boolean isCPUUsageActive = true;
    private static boolean isTickTimeActive = true;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        long currentTick = event.world.getGameTime();
        if (currentTick != lastTick && event.phase == TickEvent.Phase.END && !event.world.isClientSide) {
            if (isCPUUsageActive) {
                lastTick = currentTick;
                if(CPUUsageCounter > 19){
                    CPUUsageCounter = 0;
                    cachedCPUUsage = getCPUUsage();
                    // System.out.println("CPU Usage: " + cachedCPUUsage);
                    isCPUUsageActive = false;
                } else {
                    CPUUsageCounter++;
                }
            }
            if (isTickTimeActive) {
                MinecraftServer server = event.world.getServer();
                if (server != null) {
                    long[] tickTimes = server.tickTimes;
                    tickTimeCounterMax = tickTimes.length;
                    cachedTickTime = tickTimes[tickTimeCounter]; // Last tick time in ns
                    System.out.println("Last tick time: " + cachedTickTime + " ns(" + tickTimeCounterMax + ")");
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

    private static double getCPUUsage() {
        
        double cpuUsage;

        try{
            SystemInfo systemInfo = new SystemInfo();
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

    public static double getCachedCPUUsage() {
        isCPUUsageActive = true;
        return cachedCPUUsage;
    }

    public static long getCachedTickTime() {
        isTickTimeActive = true;
        return cachedTickTime;
    }
}