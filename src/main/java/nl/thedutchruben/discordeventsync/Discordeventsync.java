package nl.thedutchruben.discordeventsync;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.thedutchruben.discordeventsync.exeptions.DiscordApiErrorException;
import nl.thedutchruben.discordeventsync.framework.Event;
import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.commands.CommandRegistry;
import nl.thedutchruben.mccore.commands.TabComplete;
import nl.thedutchruben.mccore.utils.config.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * This plugin wil sync the event's from discord to minecraft
 */
public final class Discordeventsync extends JavaPlugin {
    private static Discordeventsync intance;
    private FileManager fileManager = new FileManager(this);
    private String guildId = "";
    private String botCode = "";
    /**
     * List with the events but loaded in cache
     */
    private List<Event> discordEvents = new ArrayList<>();
    @Override
    public void onEnable() {
        intance = this;
        new Mccore(this);
        FileManager.Config config = fileManager.getConfig("discord.yml");
        FileConfiguration configfileConfiguration = config.get();
        configfileConfiguration.addDefault("discord.guildId", "");
        configfileConfiguration.addDefault("discord.botCode", "");
        config.copyDefaults(true).save();

        this.guildId = configfileConfiguration.getString("discord.guildId");
        this.botCode = configfileConfiguration.getString("discord.botCode");

        importEvents().whenComplete((unused, throwable) -> {
            if(throwable != null){
                Bukkit.getLogger().log(Level.WARNING,"Event's not loaded");
                throwable.printStackTrace();
                return;
            }
            Bukkit.getLogger().log(Level.INFO,"Event's loaded");

        });

        CommandRegistry.getTabCompletable().put("discordevent", commandSender -> {
            Set<String> events = new HashSet<>();
            for (Event discordEvent : getDiscordEvents()) {
                events.add(discordEvent.getName());
            }
            return events;
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public CompletableFuture<Void> importEvents(){

        return CompletableFuture.supplyAsync(() -> {
            List<Event> events = new ArrayList<>();
            try {
                URL url = new URL("https://discordapp.com/api/guilds/"+guildId+"/scheduled-events");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty ("Authorization", "Bot " + botCode);
                con.setRequestMethod("GET");
                BufferedReader br = null;
                if (con.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line = br.readLine();
                    JsonArray jsonArray = (JsonArray) JsonParser.parseString(line);
                    for (JsonElement jsonElement : jsonArray) {
                        Event event = new Event();
                        JsonObject eventObject = jsonElement.getAsJsonObject();
                        event.setId(eventObject.get("id").getAsString());
                        event.setName(eventObject.get("name").getAsString());

                        if (!eventObject.get("description").isJsonNull()) {
                            event.setDescription(eventObject.get("description").getAsString());
                        }
                        event.setStartDate(eventObject.get("scheduled_start_time").getAsString());

                        if (!eventObject.get("scheduled_end_time").isJsonNull()) {
                            event.setEndDate(eventObject.get("scheduled_end_time").getAsString());
                        }

                        if (!eventObject.get("entity_metadata").isJsonNull()) {
                            event.setLocation(eventObject.get("entity_metadata").getAsJsonObject().get("location").getAsString());
                        } else {
                            event.setLocation("Discord");
                        }
                        events.add(event);
                    }
                }else if (con.getResponseCode() == 500) {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    new DiscordApiErrorException("The discord api gave a 500 error check : https://discordstatus.com/ for issues");
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }

                br.close();
            } catch (IOException | ClassCastException e) {
                e.printStackTrace();
            }

            this.discordEvents = events;
            return null;
        });
    }

    public static Discordeventsync getIntance() {
        return intance;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public List<Event> getDiscordEvents() {
        return discordEvents;
    }

    public String getBotCode() {
        return botCode;
    }

    public String getGuildId() {
        return guildId;
    }
}
