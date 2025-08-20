package at.kaindorf.games.utils;

import at.kaindorf.games.BedwarsRel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.material.Bed;
import org.bukkit.util.ChatPaginator;

import java.util.Collections;
import java.util.HashMap;

public class ChatWriter {

    public static String pluginMessage(String str) {
        return ChatColor.translateAlternateColorCodes('&',
                BedwarsRel.getInstance().getConfig().getString("chat-prefix",
                        ChatColor.GRAY + "[" + ChatColor.AQUA + "BedWars" + ChatColor.GRAY + "]"))
                + " " + ChatColor.WHITE + str;
    }

    public static void wrongPermissionMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.wrongPermission"));
    }

    public static boolean errorMessage(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.RED + msg);
        return true;
    }

    public static void paginateOutput(CommandSender sender, String output, int page) {
        ChatPaginator.ChatPage chatPage = ChatPaginator.paginate(output, page);
        for (String line : chatPage.getLines()) {
            sender.sendMessage(line);
        }

        String pageOutput = BedwarsRel._l("default.pages", ImmutableMap.of("current", "" + chatPage.getPageNumber(), "max", "" + chatPage.getTotalPages()));
        sender.sendMessage(ChatColor.GREEN + "---------- " + pageOutput + " ----------");
    }

}
