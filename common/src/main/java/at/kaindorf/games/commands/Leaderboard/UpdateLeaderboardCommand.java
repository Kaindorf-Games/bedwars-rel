package at.kaindorf.games.commands.Leaderboard;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class UpdateLeaderboardCommand extends BaseCommand implements ICommand {
    public UpdateLeaderboardCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("leaderboard." + this.getPermission())) {
            return false;
        }

        if(BedwarsRel.getInstance().getActiveLeaderboard() !=  null) {
            BedwarsRel.getInstance().getActiveLeaderboard().updateDataToLeaderboard(BedwarsRel.getInstance().getActiveLeaderboard(), true);
            sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("leaderboard.info.updated"));
            return true;
        }
        sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("leaderboard.errors.notfound"));
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
        return "update";
    }

    @Override
    public String getDescription() {
        return BedwarsRel._l("commands.updateleaderboard.description");
    }

    @Override
    public String getName() {
        return BedwarsRel._l("commands.updateleaderboard.name");
    }

    @Override
    public String getPermission() {
        return "manager";
    }
}
