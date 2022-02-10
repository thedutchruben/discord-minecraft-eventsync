package nl.thedutchruben.discordeventsync.runnables;

import nl.thedutchruben.discordeventsync.Discordeventsync;
import nl.thedutchruben.discordeventsync.framework.Event;
import nl.thedutchruben.mccore.runnables.ASyncRepeatingTask;

@ASyncRepeatingTask(startTime = 0,repeatTime = 20*60)
public class RoundEventsRunnable implements Runnable{
    private int current;
    private static Event currentEvent;

    @Override
    public void run() {
        if(current++ <= Discordeventsync.getIntance().getDiscordEvents().size()){
            current++;
        }else {
            current = 0;
        }

        currentEvent = Discordeventsync.getIntance().getDiscordEvents().get(0);
    }

    public static Event getCurrentEvent() {
        return currentEvent;
    }
}
