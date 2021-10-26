package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import at.kaindorf.games.utils.Saver;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class ClearTournamentCommand extends BaseCommand implements ICommand {
    public ClearTournamentCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("tourney." + this.getPermission())) {
            sender.sendMessage(ChatWriter.wrongPermissionMessage());
            return false;
        }
        if (args.size() == 0) {
            Tournament.getInstance().clear();
            Saver.clear();
            Tournament.getInstance().clearRunningTournament();
        }

        if (args.size() > 0) {
            if (args.get(0).equalsIgnoreCase("saves")) {
                Saver.clear();
            } else if (args.get(0).equalsIgnoreCase("config")) {
                Tournament.getInstance().clear();
            } else if(args.get(0).equalsIgnoreCase("runningTournament")) {
                Tournament.getInstance().clearRunningTournament();
            }
        }

        sender.sendMessage(ChatColor.GREEN + "Clearing done!");
        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[]{};
    }

    @Override
    public String getCommand() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Clears all tournament configuration (teams, players, matches, ...)";
    }

    @Override
    public String getName() {
        return "Clear Tournament";
    }

    @Override
    public String getPermission() {
        return "manage";
    }
}
