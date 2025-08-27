package at.kaindorf.games.commands.Leaderboard;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class StopLeaderboardCommand extends BaseCommand implements ICommand {
    public StopLeaderboardCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("leaderboard." + this.getPermission())) {
            return false;
        }

        Gson gson = new Gson();
        String json = gson.toJson(BedwarsRel.getInstance().getActiveLeaderboard(), BedwarsRel.getInstance().getActiveLeaderboard().getClass());

        if (BedwarsRel.getInstance().getActiveLeaderboard() != null) {
            BedwarsRel.getInstance().getActiveLeaderboard().setActive(false);
            sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("leaderboard.info.stopped"));
            return true;
        }

        sender.sendMessage(ChatColor.RED + BedwarsRel._l("leaderboard.errors.notfound"));
        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[]{};
    }

    @Override
    public CommandArgument[] getNewArguments() {
        return new CommandArgument[0];
    }

    @Override
    public String getCommand() {
        return "stop-leaderboard";
    }

    @Override
    public String getDescription() {
        return BedwarsRel._l("commands.stopleaderboard.description");
    }

    @Override
    public String getName() {
        return BedwarsRel._l("commands.stopleaderboard.name");
    }

    @Override
    public String getPermission() {
        return "manager";
    }
}
