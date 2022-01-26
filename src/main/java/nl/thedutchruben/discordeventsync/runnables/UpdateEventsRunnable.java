package nl.thedutchruben.discordeventsync.runnables;

import nl.thedutchruben.discordeventsync.Discordeventsync;
import nl.thedutchruben.discordeventsync.utils.Colors;
import nl.thedutchruben.mccore.runnables.ASyncRepeatingTask;
import org.bukkit.Bukkit;

import java.util.logging.Level;

@ASyncRepeatingTask(startTime = 20*60*10,repeatTime = 20*60*10)
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
        Discordeventsync.getIntance().importEvents().whenComplete((unused, throwable) -> {
            if(throwable != null){
                Bukkit.getLogger().log(Level.WARNING,Colors.WARNING.getColor()+ "Someting whent wrong");
                throwable.printStackTrace();
            }else{
                Bukkit.getLogger().log(Level.INFO,Colors.SUCCESS.getColor() + "Event's reloaded");
            }
        });
    }
}
