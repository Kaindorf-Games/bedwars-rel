package at.kaindorf.games.utils;

import at.kaindorf.games.BedwarsRel;
import org.bukkit.ChatColor;

public class ChatWriter {

  public static String pluginMessage(String str) {
    return ChatColor.translateAlternateColorCodes('&',
        BedwarsRel.getInstance().getConfig().getString("chat-prefix",
            ChatColor.GRAY + "[" + ChatColor.AQUA + "BedWars" + ChatColor.GRAY + "]"))
        + " " + ChatColor.WHITE + str;
  }

}
