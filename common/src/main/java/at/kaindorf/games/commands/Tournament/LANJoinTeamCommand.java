package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
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

        if (Tournament.getInstance().getGroups().size() > 0) {
            sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.tournamentalreadyrunningteamjoin"));
            return true;
        }

        Optional<TourneyTeam> team = Tournament.getInstance().getTeamPerName(args.get(0));
        if (!team.isPresent()) {
            sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.teamnotexists"));
            return true;
        }

        if (team.get().getPlayers().size() >= Tournament.getInstance().getLanTeamSizes()) {
            sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.teamfull"));
            return true;
        }

        try
        {
            Tournament.getInstance().addPlayer(Bukkit.getServer().getPlayer(sender.getName()).getUniqueId().toString(), args.get(0));
            sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("tourney.errors.teamjoined"));
        } catch (TournamentEntityExistsException | TournamentEntityMissingException ex) {
            sender.sendMessage(ChatColor.RED+ex.getMessage());
        }

        return true;
    }

    @Override
    public CommandArgument[] getNewArguments() {
        return new CommandArgument[]{new CommandArgument("team", String.class)};
    }

    @Override
    public String getCommand() {
        return "joinTeam";
    }

    @Override
    public String getDescription() {
        return BedwarsRel._l("commands.tourney.lanjointeam.description");
    }

    @Override
    public String getName() {
        return BedwarsRel._l("commands.tourney.lanjointeam.name");
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
