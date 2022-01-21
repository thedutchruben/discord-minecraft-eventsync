package nl.thedutchruben.discordeventsync.framework;

import java.util.concurrent.CompletableFuture;

public class Event {

    public String getName(){
        return "";
    }

    public String formattedDate(){
        return "";
    }

    public String getLocation(){
        return "";
    }

    public int intrestCount(){
        return 0;
    }

    public static CompletableFuture<Void> createEvent(String name, String date, String time, String place){
        return CompletableFuture.supplyAsync(() -> {
            return null;
        });
    }
}
