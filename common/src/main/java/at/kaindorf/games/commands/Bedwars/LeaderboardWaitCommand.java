package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.communication.dto.Leaderboard;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class LeaderboardWaitCommand extends BaseCommand implements ICommand {
    public LeaderboardWaitCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("bw." + this.getPermission())) {
            return false;
        }

        if(!args.isEmpty() && StringUtils.isNumeric(args.get(0))) {
            Leaderboard.getInstance().setWaitDuration(Integer.parseInt(args.get(0)));
            sender.sendMessage(ChatColor.GREEN+"Duration has been set");
        } else if(!StringUtils.isNumeric(args.get(0))) {
            sender.sendMessage(ChatColor.RED+"Argument duration has to be an int!");
        } else {
            sender.sendMessage(ChatColor.RED + "Argument duration required!");
        }

        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[] {"duration"};
    }

    @Override
    public String getCommand() {
        return "wait-leaderboard";
    }

    @Override
    public String getDescription() {
        return "After which duration the leaderboard should only be updated";
    }

    @Override
    public String getName() {
        return "wait-leaderboard";
    }

    @Override
    public String getPermission() {
        return "setup";
    }
}
