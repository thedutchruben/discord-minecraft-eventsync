package nl.thedutchruben.discordeventsync.framework;

import com.google.gson.*;
import lombok.SneakyThrows;
import nl.thedutchruben.discordeventsync.Discordeventsync;
import nl.thedutchruben.discordeventsync.exeptions.DiscordApiErrorException;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class Event {

    private String id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String location;

    private int count = 0;
    private long lastChecked = 0;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public String getName(){
        return name;
    }

    public String formattedDate(){
        return startDate;
    }

    public String getLocation(){
        return location;
    }

    @SneakyThrows
    public CompletableFuture<Integer> interestedCount(){
        return CompletableFuture.supplyAsync(() -> {
            if(lastChecked == 0 || lastChecked <= System.currentTimeMillis()){
                URL url;
                try {
                    url = new URL("https://discordapp.com/api/guilds/"+ Discordeventsync.getIntance().getGuildId() +"/scheduled-events/"+id+"/users");

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty ("Authorization", "Bot " + Discordeventsync.getIntance().getBotCode());
                con.setRequestMethod("GET");
                BufferedReader br = null;
                if (con.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line = br.readLine();
                    JsonArray jsonArray = (JsonArray) JsonParser.parseString(line);
                    this.count = jsonArray.size();
                }else if (con.getResponseCode() == 500) {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    new DiscordApiErrorException("The discord api gave a 500 error check : https://discordstatus.com/ for issues");
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }

                br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.lastChecked = System.currentTimeMillis() + 1000 * 60 * 60 * 10;
            }

            return count;
        });

    }

    public static CompletableFuture<Void> createEvent(String name, String date, String time, String place){
        return CompletableFuture.supplyAsync(() -> {

            return null;
        });
    }
}
