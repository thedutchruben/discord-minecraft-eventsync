package nl.thedutchruben.discordeventsync.runnables;

import nl.thedutchruben.discordeventsync.Discordeventsync;
import nl.thedutchruben.discordeventsync.framework.Event;
import nl.thedutchruben.mccore.runnables.ASyncRepeatingTask;

import java.util.Optional;

@ASyncRepeatingTask(startTime = 20*60,repeatTime = 20*60)
public class RoundEventsRunnable implements Runnable{
    private int current = 0;
    private static Optional<Event> currentEvent = Optional.empty();

    @Override
    public void run() {
        System.out.println(current+1 <= Discordeventsync.getIntance().getDiscordEvents().size());
        if(current+1 <= Discordeventsync.getIntance().getDiscordEvents().size()){
            current++;
        }else {
            current = 0;
        }

        System.out.println(current);

        currentEvent = Optional.of(Discordeventsync.getIntance().getDiscordEvents().get(current));
    }

    public static Optional<Event> getCurrentEvent() {
        return currentEvent;
    }
}
