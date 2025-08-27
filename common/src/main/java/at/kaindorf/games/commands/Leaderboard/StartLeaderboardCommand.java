package at.kaindorf.games.commands.Leaderboard;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.leaderboard.leaderboards.LeaderBoardType;
import at.kaindorf.games.leaderboard.observer.LeaderboardBase;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class StartLeaderboardCommand extends BaseCommand implements ICommand {
    public StartLeaderboardCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("leaderboard." + this.getPermission())) {
            return false;
        }
        LeaderboardBase activeLeaderboard = BedwarsRel.getInstance().getActiveLeaderboard();


        if (args.size() == 1) { // continue already started Leaderboard
            String name = args.get(0);

            if (activeLeaderboard != null && activeLeaderboard.getName().equalsIgnoreCase(name)) {
                activeLeaderboard.setActive(true);
                sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("leaderboard.errors.alreadyactive", ImmutableMap.of("name", name)));
            } else {
                sender.sendMessage(ChatColor.RED + BedwarsRel._l("leaderboard.errors.notfoundlong", ImmutableMap.of("name", name)));
            }
        } else if (args.size() == 2) { // start new Leaderboard
            String name = args.get(0);
            String typeName = args.get(1);

            LeaderboardBase prev = activeLeaderboard;

            LeaderBoardType type = LeaderBoardType.fromValue(typeName);
            activeLeaderboard = LeaderboardBase.findLeaderboard(type, name);
            if (activeLeaderboard == null) {
                activeLeaderboard = prev;
                sender.sendMessage(ChatColor.RED + BedwarsRel._l("leaderboard.errors.cannotbecreated", ImmutableMap.of("type", type.value)));
            } else {
                activeLeaderboard.setActive(true);
                sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("leaderboard.info.started", ImmutableMap.of("name", name)));
            }
        } else {
            sender.sendMessage(ChatColor.RED + BedwarsRel._l("invalidparamCount"));
        }

        BedwarsRel.getInstance().setActiveLeaderboard(activeLeaderboard);
        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[]{"name <type>"};
    }

    @Override
    public CommandArgument[] getNewArguments() {
        return new CommandArgument[]{new CommandArgument("name", String.class), new CommandArgument("type", true, LeaderBoardType.class)};
    }

    @Override
    public String getCommand() {
        return "start-leaderboard";
    }

    @Override
    public String getDescription() {
        return BedwarsRel._l("commands.startleaderboard.description");
    }

    @Override
    public String getName() {
        return BedwarsRel._l("commands.startleaderboard.name");
    }

    @Override
    public String getPermission() {
        return "manager";
    }
}
