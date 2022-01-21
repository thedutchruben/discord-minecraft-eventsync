package nl.thedutchruben.discordeventsync;

import nl.thedutchruben.discordeventsync.framework.Event;
import nl.thedutchruben.mccore.Mccore;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This plugin wil sync the event's from discord to minecraft
 */
public final class Discordeventsync extends JavaPlugin {
    private static Discordeventsync intance;
    /**
     * List with the events but loaded in cache
     */
    private List<Event> discordEvents;
    @Override
    public void onEnable() {
        intance = this;
        new Mccore(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public CompletableFuture<Void> importEvents(){

        return CompletableFuture.supplyAsync(() -> {
            return null;
        });
    }

    public static Discordeventsync getIntance() {
        return intance;
    }

    public List<Event> getDiscordEvents() {
        return discordEvents;
    }
}
