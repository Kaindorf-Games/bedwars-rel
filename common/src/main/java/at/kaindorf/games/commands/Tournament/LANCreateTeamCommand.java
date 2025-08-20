package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.exceptions.exist.TournamentEntityExistsException;
import at.kaindorf.games.exceptions.exist.TourneyPlayerExistsException;
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

public class LANCreateTeamCommand extends BaseCommand implements ICommand {

    public LANCreateTeamCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if(!sender.hasPermission("tourney."+this.getPermission())) {
            ChatWriter.wrongPermissionMessage(sender);
            return true;
        }

        if (Tournament.getInstance().getGroups().size() > 0) {
            sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.tournamentalreadyrunningteamcreation"));
            return true;
        }

        TourneyTeam team = null;
        try {
            team = Tournament.getInstance().addTeam(args.get(0));
            Tournament.getInstance().addPlayer(Bukkit.getServer().getPlayer(sender.getName()).getUniqueId().toString(), team.getName());
            sender.sendMessage(ChatColor.GREEN +  BedwarsRel._l("tourney.info.teamcreated"));
        } catch (TourneyPlayerExistsException ex) {
            Tournament.getInstance().removeTeam(team.getName());
            sender.sendMessage(ChatColor.RED+ex.getMessage());
        } catch (TournamentEntityExistsException | TournamentEntityMissingException ex) {
            sender.sendMessage(ChatColor.RED+ex.getMessage());
        }


        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[]{"name"};
    }

    @Override
    public String getCommand() {
        return "create";
    }

    @Override
    public String getDescription() {
        return BedwarsRel._l("commands.tourney.lancreateteam.description");
    }

    @Override
    public String getName() {
        return BedwarsRel._l("commands.tourney.lancreateteam.name");
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
