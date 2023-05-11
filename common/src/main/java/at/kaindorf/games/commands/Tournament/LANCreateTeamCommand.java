package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.exceptions.TournamentEntityExistsException;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.utils.ChatWriter;
import com.sun.xml.internal.bind.v2.TODO;
import lombok.SneakyThrows;
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
        try {
            TourneyTeam team = Tournament.getInstance().addTeam(args.get(0));
            Tournament.getInstance().addPlayer(sender.getName(), team.getName());
            sender.sendMessage(ChatColor.GREEN + "Team created");
        } catch (TournamentEntityExistsException ex) {
            sender.sendMessage(ChatColor.RED+"Name of Team exists already");
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
        return "Create your own custom team";
    }

    @Override
    public String getName() {
        return "create";
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
