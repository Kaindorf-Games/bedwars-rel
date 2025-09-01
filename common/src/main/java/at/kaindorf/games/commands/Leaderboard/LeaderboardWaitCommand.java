package at.kaindorf.games.commands.Leaderboard;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class LeaderboardWaitCommand extends BaseCommand implements ICommand {
    public LeaderboardWaitCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("leaderboard." + this.getPermission())) {
            return false;
        }

        if (BedwarsRel.getInstance().getActiveLeaderboard() == null) {
            sender.sendMessage(ChatColor.RED + BedwarsRel._l("leaderboard.errors.notfound"));
            return true;
        }

        if (!args.isEmpty() && StringUtils.isNumeric(args.get(0))) {
            BedwarsRel.getInstance().getActiveLeaderboard().setWaitBetweenUpdates(Integer.parseInt(args.get(0)));
            sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("leaderboard.errors.durset"));
        } else if (!StringUtils.isNumeric(args.get(0))) {
            sender.sendMessage(ChatColor.RED + BedwarsRel._l("leaderboard.errors.durargumentint"));
        } else {
            sender.sendMessage(ChatColor.RED + BedwarsRel._l("leaderboard.errors.durargumentreq"));
        }

        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[]{"duration"};
    }

    @Override
    public CommandArgument[] getNewArguments() {
        return new CommandArgument[]{new CommandArgument("duration", Integer.class)};
    }

    @Override
    public String getCommand() {
        return "wait";
    }

    @Override
    public String getDescription() {
        return BedwarsRel._l("commands.leaderboardwait.description");
    }

    @Override
    public String getName() {
        return BedwarsRel._l("commands.leaderboardwait.name");
    }

    @Override
    public String getPermission() {
        return "manager";
    }
}
