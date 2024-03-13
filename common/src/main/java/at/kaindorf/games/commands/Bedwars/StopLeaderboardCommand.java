package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
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

        Gson gson = new Gson();
        String json = gson.toJson(BedwarsRel.getInstance().getActiveLeaderboard(), BedwarsRel.getInstance().getActiveLeaderboard().getClass());

        if (BedwarsRel.getInstance().getActiveLeaderboard() != null) {
            BedwarsRel.getInstance().getActiveLeaderboard().setActive(false);
            sender.sendMessage(ChatColor.GREEN + "Leaderboard stopped");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "No Leaderboard found to stop");
        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[]{};
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
