package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.communication.dto.Leaderboard;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class UpdateLeaderboardCommand extends BaseCommand implements ICommand {
    public UpdateLeaderboardCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("bw." + this.getPermission())) {
            return false;
        }

        Leaderboard.getInstance().updateDataToLeaderboard(Leaderboard.getInstance() , true);
        sender.sendMessage(ChatColor.GREEN + "Leaderboard updated");
        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[]{};
    }

    @Override
    public String getCommand() {
        return "update-leaderboard";
    }

    @Override
    public String getDescription() {
        return "Forces leaderboard update";
    }

    @Override
    public String getName() {
        return "update-leaderboard";
    }

    @Override
    public String getPermission() {
        return "setup";
    }
}
