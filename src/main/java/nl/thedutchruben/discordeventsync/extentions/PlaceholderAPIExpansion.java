package nl.thedutchruben.discordeventsync.extentions;

import lombok.SneakyThrows;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import nl.thedutchruben.discordeventsync.DiscordEventSync;
import nl.thedutchruben.discordeventsync.framework.Event;
import nl.thedutchruben.discordeventsync.runnables.RoundEventsRunnable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class PlaceholderAPIExpansion extends PlaceholderExpansion{

    @Override
    public @NotNull String getIdentifier() {
        return "discordeventsync";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.valueOf(DiscordEventSync.getInstance().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return DiscordEventSync.getInstance().getDescription().getVersion();
    }

    @SneakyThrows
    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        //%discordeventsync_invite_url%
        if (params.equals("invite_url")) {
            return DiscordEventSync.getInstance().getDiscordUrl();
        }

        //%discordeventsync_count%
        if (params.equals("count")) {
            return String.valueOf(DiscordEventSync.getInstance().getDiscordEvents().size());
        }

        Event event = DiscordEventSync.getInstance().getNextEvent().get();

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


        //%discordeventsync_cycle_event_current%
        if(params.equals("cycle_event_current")){
            if(RoundEventsRunnable.getComingUp() == null){
                return "0";
            }else{
                return String.valueOf(RoundEventsRunnable.getCurrent());
            }
        }

        //%discordeventsync_cycle_event_name%
        if(params.equals("cycle_event_name")){
            if(RoundEventsRunnable.getComingUp() == null){
                return "No event found";
            }else{
                return RoundEventsRunnable.getComingUp().get().getName();
            }
        }

        //%discordeventsync_cycle_event_description%
        if(params.equals("cycle_event_description")){
            if(RoundEventsRunnable.getComingUp() == null){
                return "No event found";
            }else{
                return RoundEventsRunnable.getComingUp().get().getDescription();
            }
        }

        //%discordeventsync_cycle_event_date%
        if(params.equals("cycle_event_date")){
            if(RoundEventsRunnable.getComingUp() == null){
                return "No event found";
            }else{
                return RoundEventsRunnable.getComingUp().get().formattedDate();
            }
        }


        //%discordeventsync_cycle_event_count%
        if(params.equals("cycle_event_count")){
            if(RoundEventsRunnable.getComingUp().get() == null){
                return "No event found";
            }else{
                return String.valueOf(RoundEventsRunnable.getComingUp().get().interestedCount().get());
            }
        }

        return null;
    }

}
