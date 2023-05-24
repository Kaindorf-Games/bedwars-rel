package at.kaindorf.games.utils;

import at.kaindorf.games.BedwarsRel;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;

public class ChatWriter {

  public static String pluginMessage(String str) {
    return ChatColor.translateAlternateColorCodes('&',
        BedwarsRel.getInstance().getConfig().getString("chat-prefix",
            ChatColor.GRAY + "[" + ChatColor.AQUA + "BedWars" + ChatColor.GRAY + "]"))
        + " " + ChatColor.WHITE + str;
  }

  public static void wrongPermissionMessage(CommandSender sender) {
    sender.sendMessage(ChatColor.RED + "You don't have the required permissions");
  }

  public static boolean errorMessage(CommandSender sender, String msg) {
    sender.sendMessage(ChatColor.RED+msg);
    return true;
  }

  public static void paginateOutput(CommandSender sender, String output, int page) {
    ChatPaginator.ChatPage chatPage = ChatPaginator.paginate(output, page);
    for (String line : chatPage.getLines()) {
      sender.sendMessage(line);
    }

    sender.sendMessage(ChatColor.GREEN + "---------- "
        + "Page " + chatPage.getPageNumber() + " of " + chatPage.getTotalPages() + " ----------");
  }

}
