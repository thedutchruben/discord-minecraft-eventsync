package nl.thedutchruben.discordeventsync.commands;

import nl.thedutchruben.discordeventsync.DiscordEventSync;
import nl.thedutchruben.discordeventsync.utils.Colors;
import nl.thedutchruben.mccore.commands.Command;
import nl.thedutchruben.mccore.commands.Default;
import nl.thedutchruben.mccore.commands.SubCommand;
import nl.thedutchruben.mccore.utils.message.MessageUtil;
import org.bukkit.command.CommandSender;

@Command(command = "discord",console = true,description = "Get the url of the discord",permission = "discordeventsync.command.discord")
public class DiscordCommand {

    @Default
    @SubCommand(subCommand = "link")
    public void link(CommandSender sender, String[] params){
        MessageUtil.sendUrlMessage(sender,Colors.HIGH_LIGHT.getColor()+"Click here to join the discord server!"
                , DiscordEventSync.getInstance().getDiscordUrl(),"Click to join the discord server");
    }
}
