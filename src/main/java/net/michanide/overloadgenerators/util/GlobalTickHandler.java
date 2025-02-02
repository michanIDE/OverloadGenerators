package net.michanide.overloadgenerators.util;

import net.michanide.overloadgenerators.OverloadGenerators;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

@Mod.EventBusSubscriber(modid = OverloadGenerators.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlobalTickHandler {
    private static double cachedCPUUsage = 0;
    private static long lastTick = -1;
    private static int counter = 20;
    private static long[] prevTicks = null;
    private static boolean isActive = false;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (isActive && event.phase == TickEvent.Phase.END && !event.world.isClientSide) {
            long currentTick = event.world.getGameTime();
            if (currentTick != lastTick) {
                lastTick = currentTick;
                if(counter > 19){
                    counter = 0;
                    cachedCPUUsage = getCPUUsage();
                    isActive = false;
                } else {
                    counter++;
                }
            }
        }
    }

    private static double getCPUUsage() {
        
        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        double cpuUsage;

        if(prevTicks != null){
            cpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks);
        } else {
            cpuUsage = 0.0;
        }
        prevTicks = processor.getSystemCpuLoadTicks();

        return cpuUsage;
    }

    public static double getCachedCPUUsage() {
        isActive = true;
        return cachedCPUUsage;
    }
}