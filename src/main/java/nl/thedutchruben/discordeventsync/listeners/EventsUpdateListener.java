package nl.thedutchruben.discordeventsync.listeners;

import nl.thedutchruben.discordeventsync.DiscordEventSync;
import nl.thedutchruben.discordeventsync.events.DiscordEventCreateEvent;
import nl.thedutchruben.discordeventsync.events.DiscordEventsUpdateEvent;
import nl.thedutchruben.discordeventsync.events.DiscordRoundUpdateEvent;
import nl.thedutchruben.discordeventsync.holograms.EventHologram;
import nl.thedutchruben.mccore.listeners.TDRListener;
import nl.thedutchruben.mccore.utils.hologram.Hologram;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


@TDRListener
public class EventsUpdateListener implements Listener {

    @EventHandler
    public void onEventsUpdate(DiscordEventsUpdateEvent event) {
        DiscordEventSync.getInstance().getEventHologramMap().forEach((s, eventHologram) -> {
            eventHologram.spawnHologram();
        });
    }

    @EventHandler
    public void onEventsUpdate(DiscordEventCreateEvent event) {
        DiscordEventSync.getInstance().getEventHologramMap().forEach((s, eventHologram) -> {
            eventHologram.spawnHologram();
        });
    }

    @EventHandler
    public void onRoundUpdate(DiscordRoundUpdateEvent event){
        DiscordEventSync.getInstance().getEventHologramMap().forEach((s, eventHologram) -> {
            if(eventHologram.getType() == EventHologram.Type.COMING_EVENTS){
                eventHologram.spawnHologram();
            }
        });
    }
}
