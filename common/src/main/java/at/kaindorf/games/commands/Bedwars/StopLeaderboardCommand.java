package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class StopLeaderboardCommand extends BaseCommand implements ICommand {
    public StopLeaderboardCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("bw." + this.getPermission())) {
            return false;
        }
        BedwarsRel.getInstance().setLeaderBoardActive(false);

        sender.sendMessage(ChatColor.GREEN + "Leaderboard stopped");
        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[] {};
    }

    @Override
    public String getCommand() {
        return "stop-leaderboard";
    }

    @Override
    public String getDescription() {
        return "stop the leaderboard";
    }

    @Override
    public String getName() {
        return "stop-leaderboard";
    }

    @Override
    public String getPermission() {
        return "setup";
    }
}
