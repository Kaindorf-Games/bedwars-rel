package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.communication.dto.Leaderboard;
import at.kaindorf.games.communication.dto.LeaderboardPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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


        BedwarsRel.getInstance().setLeaderBoardActive(true);
        if(!args.isEmpty()) {
            Leaderboard.getInstance().setName(args.get(0));
        }

        for(Player p: Bukkit.getServer().getOnlinePlayers()) {
            Leaderboard.getInstance().addMember(String.valueOf(p.getUniqueId()), p.getDisplayName(), false);
        }
        Leaderboard.getInstance().updateDataToLeaderboard(Leaderboard.getInstance());

        sender.sendMessage(ChatColor.GREEN + "Leaderboard started");
        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[] {};
    }

    @Override
    public String getCommand() {
        return "start-leaderboard";
    }

    @Override
    public String getDescription() {
        return "start leaderboard";
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
