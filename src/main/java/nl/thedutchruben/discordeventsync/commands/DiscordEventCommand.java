package nl.thedutchruben.discordeventsync.commands;

import nl.thedutchruben.discordeventsync.DiscordEventSync;
import nl.thedutchruben.discordeventsync.events.DiscordEventCreateEvent;
import nl.thedutchruben.discordeventsync.framework.Event;
import nl.thedutchruben.discordeventsync.utils.Colors;
import nl.thedutchruben.mccore.commands.Command;
import nl.thedutchruben.mccore.commands.Default;
import nl.thedutchruben.mccore.commands.SubCommand;
import nl.thedutchruben.mccore.utils.hologram.Hologram;
import nl.thedutchruben.mccore.utils.message.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Command(command = "discordevent",console = true,description = "Manage and see your discord event's",permission = "discordeventsync.command.discordevent")
public class DiscordEventCommand {

    @Default
    @SubCommand(subCommand = "help",permission = "discordeventsync.command.discordevent.help")
    public void help(CommandSender sender, List<String> params){
        sender.sendMessage(Colors.TEXT.getColor() +"----"+Colors.HIGH_LIGHT.getColor()+"Discord Events"+Colors.TEXT.getColor()+"----");
        MessageUtil.sendClickableCommandHover(sender,Colors.TEXT.getColor()+"/discordevent list"
                ,"discordevent list","Click to see the event's");
        sender.sendMessage(Colors.TEXT.getColor()+"/discordevent info <discordevent>");
        sender.sendMessage(Colors.TEXT.getColor()+"/discordevent create <name> <date> <time> <place>");
        sender.sendMessage(Colors.TEXT.getColor()+"/discordevent reload");
    }

    @SubCommand(subCommand = "info",params = 1,usage = "<discordevent>",permission = "discordeventsync.command.discordevent.info")
    public void info(CommandSender sender, List<String> params){
        DiscordEventSync.getInstance().getDiscordEvents().stream().filter(event -> event.getName().equalsIgnoreCase(params.get(1).replace("_"," "))).findFirst().ifPresentOrElse(event -> {
            event.interestedCount().whenCompleteAsync((integer, throwable) -> {
                sender.sendMessage(Colors.HIGH_LIGHT.getColor()+event.getName());
                sender.sendMessage(Colors.TEXT.getColor() +" Location: " +Colors.HIGH_LIGHT.getColor() + event.getLocation());
                sender.sendMessage(Colors.TEXT.getColor() +" StartTime: " +Colors.HIGH_LIGHT.getColor() + event.formattedDate());
                if(event.getDescription() != null){
                    sender.sendMessage(Colors.TEXT.getColor() +" Description: " +Colors.HIGH_LIGHT.getColor() + event.getDescription());
                }
                sender.sendMessage(Colors.TEXT.getColor() +" People with interest: " +Colors.HIGH_LIGHT.getColor() + integer);

            });

        },() -> sender.sendMessage(Colors.WARNING.getColor() + "Event not found"));
    }


    @SubCommand(subCommand = "create", description = "",params = 5, usage = "<name> <date> <starttime> <endtime> <place>",permission = "discordeventsync.command.discordevent.create")
    public void create(CommandSender sender, List<String> params){
        String name = params.get(1);
        String date = params.get(2);
        String startTime = params.get(3);
        String endTime = params.get(4);
        String place = params.get(5);
        Event.createEvent(sender,name,date,startTime,endTime,place).whenComplete((unused, throwable) -> {
            if(throwable != null){
                sender.sendMessage(Colors.WARNING.getColor()+ "Someting whent wrong");
                throwable.printStackTrace();
            }else{
                if(unused){
                    sender.sendMessage(Colors.SUCCESS.getColor() + "Event created");
                }else{
                    sender.sendMessage(Colors.WARNING.getColor()+ "Someting whent wrong");
                }
            }
        });
    }

    @SubCommand(subCommand = "list", permission = "discordeventsync.command.discordevent.list")
    public void list(CommandSender sender, List<String> params){
        sender.sendMessage(Colors.TEXT.getColor() +"----"+Colors.HIGH_LIGHT.getColor()+"Discord Events"+Colors.TEXT.getColor()+"----");
        for (Event discordEvent : DiscordEventSync.getInstance().getDiscordEvents()) {
            MessageUtil.sendClickableCommandHover(sender,Colors.HIGH_LIGHT.getColor()+discordEvent.getName()
                    ,"discordevent info " + discordEvent.getName().replace(" ", "_"),"Click for more information");
            sender.sendMessage(Colors.TEXT.getColor() +" Location: " +Colors.HIGH_LIGHT.getColor() + discordEvent.getLocation());
            sender.sendMessage(Colors.TEXT.getColor() +" StartTime: " +Colors.HIGH_LIGHT.getColor() + discordEvent.formattedDate());
            sender.sendMessage(" ");
        }
    }

    @SubCommand(subCommand = "reload", permission = "discordeventsync.command.discordevent.reload")
    public void reload(CommandSender sender, List<String> params){
        DiscordEventSync.getInstance().reloadConfig();
        DiscordEventSync.getInstance().importEvents().whenComplete((unused, throwable) -> {
           if(throwable != null){
               sender.sendMessage(Colors.WARNING.getColor()+ "Someting whent wrong");
               throwable.printStackTrace();
           }else{
                sender.sendMessage(Colors.SUCCESS.getColor() + "Event's reloaded");
           }
        });
    }

    @SubCommand(params = 1, console = false ,subCommand = "sethologram", permission = "discordeventsync.command.discordevent.sethologram")
    public void setHologram(CommandSender commandSender, List<String> params){
        Player player = (Player) commandSender;
        String type = params.get(1);
        Hologram hologram = new Hologram();
        hologram.setLocation(player.getLocation());
    }
}
