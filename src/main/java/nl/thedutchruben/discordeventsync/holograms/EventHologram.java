package nl.thedutchruben.discordeventsync.holograms;

import lombok.Data;
import nl.thedutchruben.discordeventsync.DiscordEventSync;
import nl.thedutchruben.discordeventsync.runnables.RoundEventsRunnable;
import nl.thedutchruben.mccore.utils.config.FileManager;
import nl.thedutchruben.mccore.utils.hologram.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
public class EventHologram {
    private transient List<Hologram> holograms = null;
    private Location location;
    private Type type;
    private String name;

    public EventHologram(Location location, Type type, String name) {
        this.location = location;
        this.type = type;
        this.name = name;
    }

    public static EventHologram load(FileManager.Config config){
        return new EventHologram((Location) config.get().get("location",Location.class),Type.valueOf(config.get().getString("type")),config.get().getString("name"));
    }

    public void save(){
       FileManager.Config config =  DiscordEventSync.getInstance().getFileManager().getConfig("/hologram/" + name + ".yml");
       config.get().set("name",name);
       config.get().set("type",type.name());
       config.get().set("location",location);
       config.save();
    }

    public void spawnHologram(){
        List<String> text = new ArrayList<>();
            if(holograms != null){
                for (Hologram hologram : holograms) {
                    hologram.removeHologram();
                }
            }


        switch (type){
            case NEXT_EVENT:
                DiscordEventSync.getInstance().getNextEvent().ifPresentOrElse((nextEvent -> {
                    FileManager.Config config  = DiscordEventSync.getInstance().getFileManager().getConfig("config.yml");
                    FileConfiguration configfileConfiguration = config.get();
                    configfileConfiguration.getStringList("setting.hologram.nextHologram").forEach(line -> {
                        if(line.contains("{EVENT_DESCRIPTION}")){
                            if(nextEvent.getDescription() != null || !Objects.equals(nextEvent.getDescription(), "") || !Objects.equals(nextEvent.getDescription(), " ")){
                                Arrays.asList(nextEvent.getDescription().split("(?<=\\G.{" + 50 + "})")).forEach(s -> text.add(ChatColor.translateAlternateColorCodes('&',line).replace("{EVENT_DESCRIPTION}",s)));
                            }
                        }else{
                            text.add(ChatColor.translateAlternateColorCodes('&',line).replace("{EVENT_NAME}",nextEvent.getName())
                                    .replace("{EVENT_LOCATION}", nextEvent.getLocation())
                                    .replace("{EVENT_DATE}", nextEvent.formattedDate()));
                        }
                    });
                }),() -> {
                    text.add(ChatColor.DARK_RED + "There are no event's planned yet!");

                });
                holograms = Hologram.createHolograms(location,text,0.3);
                break;
            case COMING_EVENTS:
                RoundEventsRunnable.getComingUp().ifPresentOrElse(event -> {
                    FileManager.Config config  = DiscordEventSync.getInstance().getFileManager().getConfig("config.yml");
                    FileConfiguration configfileConfiguration = config.get();
                    configfileConfiguration.getStringList("setting.hologram.comingUp").forEach(line -> {
                        if(line.contains("{EVENT_DESCRIPTION}")){
                            if(event.getDescription() != null || !Objects.equals(event.getDescription(), "") || !Objects.equals(event.getDescription(), " ")){
                                Arrays.asList(event.getDescription().split("(?<=\\G.{" + 50 + "})")).forEach(s -> text.add(ChatColor.translateAlternateColorCodes('&',line).replace("{EVENT_DESCRIPTION}",s)));
                            }
                        }else{
                            text.add(ChatColor.translateAlternateColorCodes('&',line).replace("{EVENT_NAME}",event.getName())
                                    .replace("{EVENT_LOCATION}", event.getLocation())
                                    .replace("{EVENT_DATE}", event.formattedDate()));
                        }
                    });
                },() -> {
                    text.add(ChatColor.DARK_RED + "There are no event's planned yet!");
                });
                holograms = Hologram.createHolograms(location,text,0.3);
                break;
            default:
                text.add("Wrong type");
                holograms = Hologram.createHolograms(location,text,0.3);
                break;
        }

        for (Hologram hologram : holograms) {
            hologram.spawnHologram();
        }
    }



    public enum Type{
        NEXT_EVENT,
        COMING_EVENTS
    }
}
