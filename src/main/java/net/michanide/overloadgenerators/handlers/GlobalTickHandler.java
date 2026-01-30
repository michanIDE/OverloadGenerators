package net.michanide.overloadgenerators.handlers;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

import net.michanide.overloadgenerators.OverloadGenerators;
import net.michanide.overloadgenerators.config.OverGenConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OverloadGenerators.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlobalTickHandler {
    private static boolean initialized = false;

    private static int CPUUsageCounter = 20;
    private static double cachedCPUUsage = 0;
    private static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private static boolean isCPUUsageActive = true;
    private static boolean CPUGenUsesSystemInfo = true;

    private static Runtime runtime = null;
    private static double cachedMemoryUsage = 0;
    private static int memoryUsageCounter = 10; // Prevent from being used at the same time as CPU usage
    private static long totalMemory = osBean.getTotalMemorySize();
    private static boolean isMemoryUsageActive = true;
    private static boolean MemoryGenUsesSystemInfo = false;


    private static long cachedTickTime = 0;
    private static long lastTick = -1;
    private static int tickTimeCounter = 0;
    private static int tickTimeCounterMax = 100;
    private static boolean isTickTimeActive = true;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (!initialized) {
            CPUGenUsesSystemInfo = OverGenConfig.config.cpuUsageGeneratorUseSystemInfo.get();
            MemoryGenUsesSystemInfo = OverGenConfig.config.memoryUsageGeneratorUseSystemInfo.get();
            runtime = Runtime.getRuntime();
            initialized = true;
        }
        long currentTick = event.level.getGameTime();
        if (currentTick != lastTick && event.phase == TickEvent.Phase.END && !event.level.isClientSide) {

            lastTick = currentTick;

            // CPU Usage
            if (CPUUsageCounter > 19) {
                CPUUsageCounter = 0;
                if (isCPUUsageActive) {
                    isCPUUsageActive = false; // fetches info only needed
                    if (osBean == null) {
                        cachedCPUUsage = 0;
                    } else if (CPUGenUsesSystemInfo) {
                        cachedCPUUsage = osBean.getCpuLoad();
                    } else {
                        cachedCPUUsage = osBean.getProcessCpuLoad();
                    }
                }
            } else {
                CPUUsageCounter++;
            }

            // Memory Usage
            if(memoryUsageCounter > 19) {
                memoryUsageCounter = 0;
                if (isMemoryUsageActive) {
                    isMemoryUsageActive = false; // fetches info only needed
                    if (MemoryGenUsesSystemInfo) {
                        if(osBean == null) {
                            cachedMemoryUsage = 0;
                        } else {
                            long freeMemory = osBean.getFreeMemorySize();
                            long usedMemory = totalMemory - freeMemory;
                            cachedMemoryUsage = (double) usedMemory / (double) totalMemory;
                            // System.out.println("Total Memory: " + totalMemory + " Used Memory: " + usedMemory + " Free Memory: " + freeMemory + " Usage: " + cachedMemoryUsage);
                        }
                    } else {
                        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                        cachedMemoryUsage = (double) usedMemory / (double) runtime.maxMemory();
                        // System.out.println("Max Memory: " + runtime.maxMemory() + " Used Memory: " + usedMemory + " Free Memory: " + (runtime.maxMemory() - usedMemory) + " Usage: " + cachedMemoryUsage);
                    }
                }
            } else {
                memoryUsageCounter++;
            }

            // Tick Time
            if (isTickTimeActive) {
                MinecraftServer server = event.level.getServer();
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

    public static double getCachedCPUUsage() {
        isCPUUsageActive = true;
        return cachedCPUUsage;
    }

    public static long getCachedTickTime() {
        isTickTimeActive = true;
        return cachedTickTime;
    }
    public static double getCachedMemoryUsage() {
        isMemoryUsageActive = true;
        return cachedMemoryUsage;
    }
}