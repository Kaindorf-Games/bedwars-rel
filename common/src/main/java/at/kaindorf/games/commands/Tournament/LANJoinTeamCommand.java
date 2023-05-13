package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.exceptions.exist.TournamentEntityExistsException;
import at.kaindorf.games.exceptions.missing.TournamentEntityMissingException;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LANJoinTeamCommand extends BaseCommand implements ICommand {

    public LANJoinTeamCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if(!sender.hasPermission("tourney."+this.getPermission())) {
            ChatWriter.wrongPermissionMessage(sender);
            return true;
        }
        Optional<TourneyTeam> team = Tournament.getInstance().getTeamPerName(args.get(0));
        if (!team.isPresent()) {
            sender.sendMessage(ChatColor.RED+"Team doesn't exist");
            return true;
        }

        if (team.get().getPlayers().size() >= Tournament.getInstance().getLanTeamSizes()) {
            sender.sendMessage(ChatColor.RED+"Team is full");
            return true;
        }

        try
        {
            Tournament.getInstance().addPlayer(Bukkit.getServer().getPlayer(sender.getName()).getUniqueId().toString(), args.get(0));
            sender.sendMessage(ChatColor.GREEN+ "Team joined");
        } catch (TournamentEntityExistsException | TournamentEntityMissingException ex) {
            sender.sendMessage(ChatColor.RED+ex.getMessage());
        }

        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[]{"team"};
    }

    @Override
    public String getCommand() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "join other team manually";
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getPermission() {
        return "player";
    }

    @Override
    public List<BedwarsRel.Mode> blockDuringMode() {
        return Arrays.asList(BedwarsRel.Mode.NORMAL, BedwarsRel.Mode.TOURNAMENT);
    }
}
