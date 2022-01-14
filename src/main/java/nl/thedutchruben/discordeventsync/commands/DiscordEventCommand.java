package nl.thedutchruben.discordeventsync.commands;

import nl.thedutchruben.mccore.commands.Command;
import nl.thedutchruben.mccore.commands.SubCommand;
import org.bukkit.command.CommandSender;

@Command(command = "discordevent",console = true,description = "Manage and see your discord event's",permission = "discordeventsync.command.discordevent")
public class DiscordEventCommand {

    @SubCommand(subCommand = " ", description = "")
    public void info(CommandSender sender, String[] params){
        sender.sendMessage("----Discord Events----");
        sender.sendMessage("/discordevent create");
        sender.sendMessage("/discordevent list");
        sender.sendMessage("/discordevent reload");
    }

    @SubCommand(subCommand = "create", description = "",params = 2)
    public void create(CommandSender sender, String[] params){

    }

    @SubCommand(subCommand = "list", description = "")
    public void list(CommandSender sender, String[] params){
        sender.sendMessage("----Discord Events----");
    }

    @SubCommand(subCommand = "reload", description = "")
    public void reload(CommandSender sender, String[] params){

    }
}
