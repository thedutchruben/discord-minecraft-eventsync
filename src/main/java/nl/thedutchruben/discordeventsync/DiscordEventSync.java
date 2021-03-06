package nl.thedutchruben.discordeventsync;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.thedutchruben.discordeventsync.events.DiscordEventsUpdateEvent;
import nl.thedutchruben.discordeventsync.exeptions.DiscordApiErrorException;
import nl.thedutchruben.discordeventsync.extentions.PlaceholderAPIExpansion;
import nl.thedutchruben.discordeventsync.framework.Event;
import nl.thedutchruben.discordeventsync.holograms.EventHologram;
import nl.thedutchruben.discordeventsync.utils.Colors;
import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.commands.CommandRegistry;
import nl.thedutchruben.mccore.config.UpdateCheckerConfig;
import nl.thedutchruben.mccore.utils.config.FileManager;
import nl.thedutchruben.mccore.utils.hologram.Hologram;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * This plugin wil sync the event's from discord to minecraft
 */
public final class DiscordEventSync extends JavaPlugin {
    private static DiscordEventSync instance;
    private final FileManager fileManager = new FileManager(this);
    private Map<String, EventHologram> eventHologramMap = new HashMap<>();
    private String guildId = "";
    private String botCode = "";
    private String discordUrl = "";

    /**
     * List with the events but loaded in cache
     */
    private LinkedList<Event> discordEvents = new LinkedList<>();
    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this, 14214);
        instance = this;
        Mccore mccore = new Mccore(this,"discordeventsync","62489023a9d73be8f49d1b54");
        setupConfig();
        importEvents().whenComplete((unused, throwable) -> {
            if(throwable != null){
                Bukkit.getLogger().log(Level.WARNING,"Event's not loaded");
                throwable.printStackTrace();
                return;
            }
            Bukkit.getLogger().log(Level.INFO, "Event's loaded");
            Bukkit.getScheduler().runTask(DiscordEventSync.getInstance(),() -> Bukkit.getPluginManager().callEvent(new DiscordEventsUpdateEvent(unused)));

        });

        CommandRegistry.getTabCompletable().put("discordevent", commandSender -> {
            Set<String> events = new HashSet<>();
            for (Event discordEvent : getDiscordEvents()) {
                events.add(discordEvent.getName());
            }
            return events;
        });

        CommandRegistry.getTabCompletable().put("eventhologram", commandSender -> eventHologramMap.keySet());
        CommandRegistry.getTabCompletable().put("eventType", commandSender -> {
            Set<String> options = new HashSet<>();
            for (EventHologram.Type value : EventHologram.Type.values()) {
                options.add(value.name());
            }

            return options;
        });

        metrics.addCustomChart(new SimplePie("has_events",() -> String.valueOf(!discordEvents.isEmpty())));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().log(Level.INFO, "PlaceholderAPI expansion implemented");
            metrics.addCustomChart(new SimplePie("addons_use", () -> "PlaceholderAPI"));
            new PlaceholderAPIExpansion().register();
        }

        FileManager.Config config  = fileManager.getConfig("config.yml");
        FileConfiguration configfileConfiguration = config.get();
        if (configfileConfiguration.getBoolean("setting.updatecheck.enabled",true)) {
            mccore.startUpdateChecker(new UpdateCheckerConfig("discordeventsync.updatechecker",configfileConfiguration.getInt("setting.updatecheck.updatetime")));
        }
        loadHolograms();


    }

    public void loadHolograms(){
        for (final File fileEntry : Objects.requireNonNull(new File(getDataFolder(), "hologram/").listFiles())) {
            FileManager.Config config =  DiscordEventSync.getInstance().getFileManager().getConfig("/hologram/" + fileEntry.getName());
            if (config != null) {
                    EventHologram eventHologram = EventHologram.load(config.get());
                    this.eventHologramMap.put(eventHologram.getName(),eventHologram);
                    eventHologram.spawnHologram();
            }
        }
    }
    public void setupConfig(){
        FileManager.Config discordConfig = fileManager.getConfig("discord.yml");
        FileConfiguration discordConfigConfiguration = discordConfig.get();
        discordConfigConfiguration.addDefault("discord.url", "https://discord.gg/yourawsomeserver");
        discordConfigConfiguration.addDefault("discord.guildId", "");
        discordConfigConfiguration.addDefault("discord.botCode", "");
        discordConfig.copyDefaults(true).save();

        FileManager.Config config  = fileManager.getConfig("config.yml");
        FileConfiguration configfileConfiguration = config.get();
        configfileConfiguration.addDefault("setting.updatecheck.enabled",true);
        configfileConfiguration.addDefault("setting.updatecheck.permission","discordeventsync.update");
        configfileConfiguration.addDefault("setting.updatecheck.updatetime",20*60*5);

        configfileConfiguration.addDefault("setting.dateformat","dd-MM-yyyy");
        configfileConfiguration.addDefault("setting.hologram.nextHologram",Arrays.asList("&7Next event:","&7&l{EVENT_NAME}","&7&l{EVENT_START_TIME} {EVENT_DATE}","{EVENT_DESCRIPTION}","Location: {EVENT_LOCATION}"));
        configfileConfiguration.addDefault("setting.hologram.comingUp",Arrays.asList("&7ComingUp:","&7&l{EVENT_NAME}","&7&l{EVENT_START_TIME} {EVENT_DATE}","{EVENT_DESCRIPTION}","Location: {EVENT_LOCATION}"));

        config.copyDefaults(true).save();


        this.guildId = discordConfigConfiguration.getString("discord.guildId");
        this.botCode = discordConfigConfiguration.getString("discord.botCode");
        this.discordUrl = discordConfigConfiguration.getString("discord.url");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (EventHologram value : eventHologramMap.values()) {
            for (Hologram hologram : value.getHolograms()) {
                hologram.removeHologram();
            }
        }
        this.eventHologramMap.clear();
        this.discordEvents.clear();
    }

    public CompletableFuture<LinkedList<Event>> importEvents(){

        return CompletableFuture.supplyAsync(() -> {
            LinkedList<Event> events = new LinkedList<>();
            try {
                URL url = new URL("https://discordapp.com/api/guilds/"+guildId+"/scheduled-events");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty ("Authorization", "Bot " + botCode);
                con.setRequestMethod("GET");
                if (con.getResponseCode() == 200) {
                    try(InputStreamReader reader = new InputStreamReader(con.getInputStream())){
                        try(BufferedReader br = new BufferedReader(reader)){
                            JsonArray jsonArray = JsonParser.parseString(br.readLine()).getAsJsonArray();

                            for (JsonElement jsonElement : jsonArray) {
                                JsonObject eventObject = jsonElement.getAsJsonObject();
                                Event event = new Event();

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
                            Collections.sort(events);
                            this.discordEvents = events;

                            return events;
                        }
                    }
                }else if (con.getResponseCode() == 500) {
                    throw new DiscordApiErrorException("The discord api gave a 500 error check : https://discordstatus.com/ for issues");
                }
            } catch (IOException | ClassCastException | DiscordApiErrorException e) {
                e.printStackTrace();
            }

            this.discordEvents = events;
            Bukkit.getScheduler().runTask(this,() -> Bukkit.getPluginManager().callEvent(new DiscordEventsUpdateEvent(events)));
            return null;
        });
    }

    public static DiscordEventSync getInstance() {
        return instance;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public List<Event> getDiscordEvents() {
        return discordEvents;
    }

    public Optional<Event> getNextEvent(){
        if (getDiscordEvents().isEmpty()){
            return Optional.empty();
        }

        return Optional.of(getDiscordEvents().get(0));
    }

    public String getBotCode() {
        return botCode;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getDiscordUrl() {
        return discordUrl;
    }

    public Map<String, EventHologram> getEventHologramMap() {
        return eventHologramMap;
    }
}
