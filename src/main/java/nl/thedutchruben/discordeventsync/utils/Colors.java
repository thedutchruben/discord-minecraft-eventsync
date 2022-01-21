package nl.thedutchruben.discordeventsync.utils;

import org.bukkit.ChatColor;

public enum Colors {
    TEXT(ChatColor.GRAY),
    HIGH_LIGHT(ChatColor.GOLD),
    WARNING(ChatColor.DARK_RED),
    SUCCESS(ChatColor.GREEN);

    private ChatColor color;

    Colors(ChatColor chatColor) {
        this.color = chatColor;
    }

    public ChatColor getColor() {
        return color;
    }
}
