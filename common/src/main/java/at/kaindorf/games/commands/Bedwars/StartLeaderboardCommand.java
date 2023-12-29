package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.leaderboard.observer.LeaderboardBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class StartLeaderboardCommand extends BaseCommand implements ICommand {
    public StartLeaderboardCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("bw." + this.getPermission())) {
            return false;
        }
        LeaderboardBase activeLeaderboard = BedwarsRel.getInstance().getActiveLeaderboard();


        if (args.size() == 1) { // continue already started Leaderboard
            String name = args.get(0);

            if (activeLeaderboard != null && activeLeaderboard.getName().equalsIgnoreCase(name)) {
                activeLeaderboard.setActive(true);
                sender.sendMessage(ChatColor.GREEN + "Leaderboard " + name + " is active again");
            } else {
                sender.sendMessage(ChatColor.RED + "Leaderboard " + name + " doesn't exist!");
            }
        } else if (args.size() == 2) { // start new Leaderboard
            String name = args.get(0);
            String type = args.get(1);

            LeaderboardBase prev = activeLeaderboard;
            activeLeaderboard = LeaderboardBase.findLeaderboard(type, name);
            if (activeLeaderboard == null) {
                activeLeaderboard = prev;
                sender.sendMessage(ChatColor.RED + "Leaderboard with type " + type + " cannot be created");
            } else {
                activeLeaderboard.setActive(true);
                sender.sendMessage(ChatColor.GREEN + "Leaderboard " + name + " started");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Too few or too many arguments");
        }

        BedwarsRel.getInstance().setActiveLeaderboard(activeLeaderboard);
        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[]{"name <type>"};
    }

    @Override
    public String getCommand() {
        return "start-leaderboard";
    }

    @Override
    public String getDescription() {
        return "start or continue leaderboard";
    }

    @Override
    public String getName() {
        return "start-leaderboard";
    }

    @Override
    public String getPermission() {
        return "setup";
    }
}
