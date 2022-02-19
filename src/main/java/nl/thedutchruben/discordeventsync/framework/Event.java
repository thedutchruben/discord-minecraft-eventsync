package nl.thedutchruben.discordeventsync.framework;

import com.google.gson.*;
import lombok.SneakyThrows;
import nl.thedutchruben.discordeventsync.DiscordEventSync;
import nl.thedutchruben.discordeventsync.exeptions.DiscordApiErrorException;
import nl.thedutchruben.discordeventsync.utils.Colors;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

public class Event {
    /**
     * Format that the user wand
     */
    private static DateFormat playerDateFormat = new SimpleDateFormat(DiscordEventSync.getInstance().
            getFileManager().getConfig("config.yml").get().getString("setting.dateformat"));
    private String id;
    private String name;
    private String description = "";
    private String startDate = "";
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
        SimpleDateFormat discordFormat = new SimpleDateFormat("yyy-MM-dd");

        try {
            return playerDateFormat.format(discordFormat.parse(startDate.replace("T"," ").split("\\+")[0]));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate.replace("T"," ").split("\\+")[0];
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
                    url = new URL("https://discordapp.com/api/guilds/"+ DiscordEventSync.getInstance().getGuildId() +"/scheduled-events/"+id+"/users");

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty ("Authorization", "Bot " + DiscordEventSync.getInstance().getBotCode());
                con.setRequestMethod("GET");
                BufferedReader br = null;
                if (con.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line = br.readLine();
                    JsonArray jsonArray = (JsonArray) JsonParser.parseString(line);
                    this.count = jsonArray.size();
                }else if (con.getResponseCode() == 500) {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    br.close();
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

    public static CompletableFuture<Boolean> createEvent(CommandSender commandSender,String name, String date, String startTime,String endTime, String place){
        SimpleDateFormat discordFormat = new SimpleDateFormat("yyy-MM-dd");
        Date userDate;
        playerDateFormat.setLenient(false);
        try {
            userDate = playerDateFormat.parse(date);
            Date now = new Date(System.currentTimeMillis());

            if(!now.before(userDate)){
                commandSender.sendMessage(Colors.WARNING.getColor() + "The date has to be in the future");
                return CompletableFuture.completedFuture(false);
            }

            Calendar calendar  = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 5);
            if(userDate.after(calendar.getTime())){
                commandSender.sendMessage(Colors.WARNING.getColor() + "The date has to be within 5 years");
                return CompletableFuture.completedFuture(false);
            }

        } catch (ParseException e) {
            commandSender.sendMessage(Colors.WARNING.getColor() + "The date format is not correct. The correct format is "+ Colors.HIGH_LIGHT.getColor() + DiscordEventSync.getInstance().
                    getFileManager().getConfig("config.yml").get().getString("setting.dateformat"));
            return CompletableFuture.completedFuture(false);
        }

        try {
            DateTimeFormatter strictTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                    .withResolverStyle(ResolverStyle.STRICT);
            LocalTime.parse(startTime, strictTimeFormatter);
            LocalTime.parse(endTime, strictTimeFormatter);
        }catch (DateTimeParseException exception){
            commandSender.sendMessage(Colors.WARNING.getColor() + "The time format is not correct. The correct format is "+ Colors.HIGH_LIGHT.getColor() + "HH:mm:ss");
            return CompletableFuture.completedFuture(false);
        }


        return CompletableFuture.supplyAsync(() -> {
            URL url = null;
            try {
                url = new URL("https://discordapp.com/api/guilds/588284432687955978/scheduled-events");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty ("Authorization", "Bot " + DiscordEventSync.getInstance().getBotCode());
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                String jsonInputString = "{\n" +
                        "   \"entity_metadata\":{\n" +
                        "      \"location\":\""+place+"\"\n" +
                        "   },\n" +
                        "   \"name\":\""+name+"\",\n" +
                        "   \"scheduled_start_time\":\""+discordFormat.format(userDate)+"T"+startTime+getCurrentTimezoneOffset()+"\",\n" +
                        "    \"scheduled_end_time\":\""+discordFormat.format(userDate)+"T"+endTime+getCurrentTimezoneOffset()+"\",\n" +
                        "   \"entity_type\":\"3\",\n" +
                        "   \"privacy_level\":\"2\"\n" +
                        "}";
                System.out.println(jsonInputString);
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
                }catch (IOException e){

                    switch (con.getResponseCode()){
                        case 401:
                            commandSender.sendMessage(Colors.WARNING.getColor() + "The botcode is incorrect in the discord.yml");
                            break;
                        case 403:
                            commandSender.sendMessage(Colors.WARNING.getColor() + "The bot doesn't have permission to create event's");
                            commandSender.sendMessage(Colors.WARNING.getColor() + "Give the bot permission in your discord server!");
                            break;
                        case 500:
                            commandSender.sendMessage(Colors.WARNING.getColor() + "There are some issues at the discord services");
                            break;
                        default:
                            commandSender.sendMessage(Colors.WARNING.getColor() + e.getMessage());
                            break;
                    }
                    return false;
                }

            } catch (IOException e) {
                e.printStackTrace();
                if(e.getMessage().split("code: ")[1].split(" ")[0] != null){
                    if(e.getMessage().split("code: ")[1].split(" ")[0] == "403"){
                        commandSender.sendMessage(Colors.WARNING.getColor() + "The bot doesn't have permission to create event's");
                        commandSender.sendMessage(Colors.WARNING.getColor() + "Give the bot permission in your discord server!");}
                    }
                return false;
            }

            return true;
        });
    }

    public static String getCurrentTimezoneOffset() {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;

        return offset;
    }
}
