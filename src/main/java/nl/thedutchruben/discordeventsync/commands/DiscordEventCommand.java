package nl.thedutchruben.discordeventsync.commands;

import nl.thedutchruben.discordeventsync.Discordeventsync;
import nl.thedutchruben.discordeventsync.framework.Event;
import nl.thedutchruben.discordeventsync.utils.Colors;
import nl.thedutchruben.mccore.commands.Command;
import nl.thedutchruben.mccore.commands.Default;
import nl.thedutchruben.mccore.commands.SubCommand;
import nl.thedutchruben.mccore.utils.message.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(command = "discordevent",console = true,description = "Manage and see your discord event's",permission = "discordeventsync.command.discordevent")
public class DiscordEventCommand {

    @Default
    @SubCommand(subCommand = "help",permission = "discordeventsync.command.discordevent.help")
    public void help(CommandSender sender, String[] params){
        sender.sendMessage(Colors.TEXT.getColor() +"----"+Colors.HIGH_LIGHT.getColor()+"Discord Events"+Colors.TEXT.getColor()+"----");
        MessageUtil.sendClickableCommandHover(sender,Colors.TEXT.getColor()+"/discordevent list"
                ,"discordevent list","Click to see the event's");
        sender.sendMessage(Colors.TEXT.getColor()+"/discordevent info <discordevent>");
        sender.sendMessage(Colors.TEXT.getColor()+"/discordevent create <name> <date> <time> <place>");
        sender.sendMessage(Colors.TEXT.getColor()+"/discordevent reload");
    }

    @SubCommand(subCommand = "info",params = 1,usage = "<discordevent>",permission = "discordeventsync.command.discordevent.info")
    public void info(CommandSender sender, String[] params){
        Discordeventsync.getIntance().getDiscordEvents().stream().filter(event -> event.getName().equalsIgnoreCase(params[1].replace("_"," "))).findFirst().ifPresentOrElse(event -> {
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


    @SubCommand(subCommand = "create", description = "",params = 5, usage = "<name> <date> <starttime> <endtime> <place>",permission = "discordeventsync.command.create")
    public void create(CommandSender sender, String[] params){
        String name = params[1];
        String date = params[2];
        String startTime = params[3];
        String endTime = params[4];
        String place = params[5];
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
    public void list(CommandSender sender, String[] params){
        sender.sendMessage(Colors.TEXT.getColor() +"----"+Colors.HIGH_LIGHT.getColor()+"Discord Events"+Colors.TEXT.getColor()+"----");
        for (Event discordEvent : Discordeventsync.getIntance().getDiscordEvents()) {
            MessageUtil.sendClickableCommandHover(sender,Colors.HIGH_LIGHT.getColor()+discordEvent.getName()
                    ,"discordevent info " + discordEvent.getName().replace(" ", "_"),"Click for more information");
            sender.sendMessage(Colors.TEXT.getColor() +" Location: " +Colors.HIGH_LIGHT.getColor() + discordEvent.getLocation());
            sender.sendMessage(Colors.TEXT.getColor() +" StartTime: " +Colors.HIGH_LIGHT.getColor() + discordEvent.formattedDate());
            sender.sendMessage(" ");
        }
    }

    @SubCommand(subCommand = "reload", permission = "discordeventsync.command.discordevent.reload")
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
