package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.communication.dto.Leaderboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;

import java.util.*;
import java.util.stream.Collectors;

public class ClearLeaderboardCommand extends BaseCommand implements ICommand {

    public ClearLeaderboardCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("bw." + this.getPermission())) {
            return false;
        }

        Map<UUID, String> uuids = new HashMap<>();
        if(BedwarsRel.getInstance().isLeaderBoardActive()) {
            uuids = BedwarsRel.getInstance().getServer().getOnlinePlayers().stream().collect(Collectors.toMap(Player::getUniqueId, Player::getName));
        }
        Leaderboard.setInstance(new Leaderboard(uuids, false));
        Leaderboard.getInstance().updateDataToLeaderboard(Leaderboard.getInstance());

        sender.sendMessage(ChatColor.GREEN+"Leaderboard has been cleared!!!");
        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }

    @Override
    public String getCommand() {
        return "clear-leaderboard";
    }

    @Override
    public String getDescription() {
        return "clear leaderboard";
    }

    @Override
    public String getName() {
        return "clear-leaderboard";
    }

    @Override
    public String getPermission() {
        return "setup";
    }
}
