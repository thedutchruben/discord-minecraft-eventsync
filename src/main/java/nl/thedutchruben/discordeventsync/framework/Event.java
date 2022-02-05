package nl.thedutchruben.discordeventsync.framework;

import com.google.gson.*;
import lombok.SneakyThrows;
import nl.thedutchruben.discordeventsync.Discordeventsync;
import nl.thedutchruben.discordeventsync.exeptions.DiscordApiErrorException;
import nl.thedutchruben.discordeventsync.utils.Colors;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

public class Event {
    /**
     * Format that the user wand
     */
    private static final DateFormat playerDateFormat = new SimpleDateFormat(Discordeventsync.getIntance().
            getFileManager().getConfig("config.yml").get().getString("setting.dateformat"));
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
                    throw new DiscordApiErrorException("The discord api gave a 500 error check : https://discordstatus.com/ for issues");
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

    public static CompletableFuture<Void> createEvent(CommandSender commandSender,String name, String date, String time, String place){
//        TimeZone.getDefault().
        return CompletableFuture.supplyAsync(() -> {
            URL url = null;
            try {
                url = new URL("https://discordapp.com/api/guilds/588284432687955978/scheduled-events");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty ("Authorization", "Bot ");
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                String jsonInputString = "{\n" +
                        "   \"entity_metadata\":{\n" +
                        "      \"location\":\""+place+"\"\n" +
                        "   },\n" +
                        "   \"name\":\""+name+"\",\n" +
                        "   \"scheduled_start_time\":\"2023-07-16T19:20:30.45+01:00\",\n" +
                        "    \"scheduled_end_time\":\"2023-07-16T19:21:30.45+01:00\",\n" +
                        "   \"entity_type\":\"3\",\n" +
                        "   \"privacy_level\":\"2\"\n" +
                        "}";
                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                if(e.getMessage().split("code: ")[1].split(" ")[0] != null){
                    if(e.getMessage().split("code: ")[1].split(" ")[0] == "403"){
                        commandSender.sendMessage(Colors.WARNING.getColor() + "The bot doesn't have permission to create event's");
                        commandSender.sendMessage(Colors.WARNING.getColor() + "Give the bot permission in your discord server!");}
                    }

            }

            return null;
        });
    }
}
