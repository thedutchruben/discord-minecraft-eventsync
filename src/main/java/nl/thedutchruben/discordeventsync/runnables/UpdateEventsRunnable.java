package nl.thedutchruben.discordeventsync.runnables;

import nl.thedutchruben.discordeventsync.DiscordEventSync;
import nl.thedutchruben.discordeventsync.events.DiscordEventsUpdateEvent;
import nl.thedutchruben.discordeventsync.utils.Colors;
import nl.thedutchruben.mccore.runnables.ASyncRepeatingTask;
import org.bukkit.Bukkit;

import java.util.logging.Level;

@ASyncRepeatingTask(startTime = 20*60*15,repeatTime=20*60*15)
public class UpdateEventsRunnable implements Runnable {

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        DiscordEventSync.getInstance().importEvents().whenComplete((unused, throwable) -> {
            if(throwable != null){
                Bukkit.getLogger().log(Level.WARNING,Colors.WARNING.getColor()+ "Someting whent wrong reloading discord event's");
                throwable.printStackTrace();
                return;
            }
            Bukkit.getPluginManager().callEvent(new DiscordEventsUpdateEvent(unused));
        });
    }
}
