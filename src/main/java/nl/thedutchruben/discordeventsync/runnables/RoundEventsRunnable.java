package nl.thedutchruben.discordeventsync.runnables;

import nl.thedutchruben.discordeventsync.DiscordEventSync;
import nl.thedutchruben.discordeventsync.framework.Event;
import nl.thedutchruben.mccore.runnables.ASyncRepeatingTask;

import java.util.Optional;

@ASyncRepeatingTask(startTime = 10,repeatTime = 20*60)
public class RoundEventsRunnable implements Runnable{
    private static int current = 0;
    private static Optional<Event> currentEvent = Optional.empty();


    @Override
    public void run() {
        if(DiscordEventSync.getInstance().getDiscordEvents().isEmpty()){
            currentEvent = Optional.empty();
            return;
        }

        if(current >= DiscordEventSync.getInstance().getDiscordEvents().size()){
            current = 0;
        }else {
            current++;
        }

        currentEvent = Optional.of(DiscordEventSync.getInstance().getDiscordEvents().get(current));
    }

    public static int getCurrent() {
        return current;
    }

    public static Optional<Event> getCurrentEvent() {
        return currentEvent;
    }
}
