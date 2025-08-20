package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.TourneyPlayer;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LANLeaveTeamCommand extends BaseCommand implements ICommand {
    public LANLeaveTeamCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if(!sender.hasPermission("tourney."+this.getPermission())) {
            ChatWriter.wrongPermissionMessage(sender);
            return true;
        }

        if (Tournament.getInstance().getGroups().size() > 0) {
            sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.tournamentalreadyrunningteamleave"));
            return true;
        }

        String uuid = Bukkit.getServer().getPlayer(sender.getName()).getUniqueId().toString();
        Optional<TourneyPlayer> playerOptional = Tournament.getInstance().getPlayers().stream().filter(p -> p.getUuid().equals(uuid)).findFirst();

        if(playerOptional.isPresent()) {
            TourneyPlayer player = playerOptional.get();
            Tournament.getInstance().getPlayers().remove(player);

            if(player.getTeam().getPlayers().size() == 1) {
                Tournament.getInstance().removeTeam(player.getTeam().getName());
            } else {
                player.getTeam().getPlayers().remove(player);
            }
            sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("tourney.info.teamleft"));
        } else {
            sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.arentinteam"));
        }


        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }

    @Override
    public String getCommand() {
        return "leaveTeam";
    }

    @Override
    public String getDescription() {
        return BedwarsRel._l("commands.tourney.lanleaveteam.description");
    }

    @Override
    public String getName() {
        return BedwarsRel._l("commands.tourney.lanleaveteam.name");
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
