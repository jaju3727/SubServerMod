package net.jaju.subservermod.events.shopsystem;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.time.LocalTime;

@Mod.EventBusSubscriber
public class DailyResetEvent {

    private static boolean hasResetToday = false;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        LocalTime currentTime = LocalTime.now();
        if (currentTime.getHour() == 0 && currentTime.getMinute() == 0 && !hasResetToday) {
            SalesDataHandler.resetDailyData();
            hasResetToday = true;
        } else if (currentTime.getHour() != 0 || currentTime.getMinute() != 0) {
            hasResetToday = false;
        }
    }
}
