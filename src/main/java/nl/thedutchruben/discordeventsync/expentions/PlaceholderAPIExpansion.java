package nl.thedutchruben.discordeventsync.expentions;

import lombok.SneakyThrows;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import nl.thedutchruben.discordeventsync.Discordeventsync;
import nl.thedutchruben.discordeventsync.framework.Event;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "discordeventsync";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.valueOf(Discordeventsync.getIntance().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return Discordeventsync.getIntance().getDescription().getVersion();
    }

    @SneakyThrows
    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        //%discordeventsync_count%
        if (params.equals("count")) {
            return String.valueOf(Discordeventsync.getIntance().getDiscordEvents().size());
        }
        Event event = Discordeventsync.getIntance().getNextEvent().get();

        //%discordeventsync_next_event_name%
        if(params.equals("next_event_name")){
            if(event == null){
                return "No event found";
            }else{
                return event.getName();
            }
        }

        //%discordeventsync_next_event_description%
        if(params.equals("next_event_description")){
            if(event == null){
                return "No event found";
            }else{
                return event.getDescription();
            }
        }

        //%discordeventsync_next_event_date%
        if(params.equals("next_event_date")){
            if(event == null){
                return "No event found";
            }else{
                return event.formattedDate();
            }
        }

        //%discordeventsync_next_event_count%
        if(params.equals("next_event_count")){
            if(event == null){
                return "No event found";
            }else{
                return String.valueOf(event.interestedCount().get());
            }
        }



        return null;
    }
}
