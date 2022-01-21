package nl.thedutchruben.discordeventsync.commands;

import nl.thedutchruben.discordeventsync.Discordeventsync;
import nl.thedutchruben.discordeventsync.framework.Event;
import nl.thedutchruben.discordeventsync.utils.Colors;
import nl.thedutchruben.mccore.commands.Command;
import nl.thedutchruben.mccore.commands.Default;
import nl.thedutchruben.mccore.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@Command(command = "discordevent",console = true,description = "Manage and see your discord event's",permission = "discordeventsync.command.discordevent")
public class DiscordEventCommand {

    @Default
    @SubCommand(subCommand = "info")
    public void info(CommandSender sender, String[] params){
        sender.sendMessage(Colors.TEXT.getColor() +"----"+Colors.HIGH_LIGHT.getColor()+"Discord Events"+Colors.TEXT.getColor()+"----");
        sender.sendMessage("/discordevent create");
        sender.sendMessage("/discordevent list");
        sender.sendMessage("/discordevent reload");
    }

    @SubCommand(subCommand = "create", description = "",params = 3, usage = "<name> <date> <time> <place>")
    public void create(CommandSender sender, String[] params){
        String name = params[0];
        String date = params[1];
        String time = params[2];
        String place = params[3];
        Event.createEvent(name,date,time,place).whenComplete((unused, throwable) -> {
            if(throwable != null){
                sender.sendMessage(Colors.WARNING.getColor()+ "Someting whent wrong");
                throwable.printStackTrace();
            }else{
                sender.sendMessage(Colors.SUCCESS.getColor() + "Event created");
            }
        });
    }

    @SubCommand(subCommand = "list", description = "")
    public void list(CommandSender sender, String[] params){
        sender.sendMessage("----Discord Events----");
        for (Event discordEvent : Discordeventsync.getIntance().getDiscordEvents()) {
            sender.sendMessage(discordEvent.getName() + " " + discordEvent.getLocation() + " " + discordEvent.getLocation());
        }
    }

    @SubCommand(subCommand = "reload", description = "")
    public void reload(CommandSender sender, String[] params){
        Discordeventsync.getIntance().importEvents().whenComplete((unused, throwable) -> {
           if(throwable != null){
               sender.sendMessage(Colors.WARNING.getColor()+ "Someting whent wrong");
               throwable.printStackTrace();
           }else{
                sender.sendMessage(Colors.SUCCESS.getColor() + "Event's reloaded");
           }
        });
    }
}
