package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LANSetTeamSizeCommand extends BaseCommand implements ICommand {

    public LANSetTeamSizeCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if(!sender.hasPermission("tourney."+this.getPermission())) {
            ChatWriter.wrongPermissionMessage(sender);
            return true;
        }

        int teams;
        try {
            teams = Integer.parseInt(args.get(0));
            if (teams <= 0) {
                throw new Exception();
            }
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.teamsizeint"));
            return true;
        }
        Tournament.getInstance().setLanTeamSizes(teams);
        sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("tourney.info.teamsizeset", ImmutableMap.of("size", ""+teams)));

        return true;
    }

    @Override
    public CommandArgument[] getNewArguments() {
        return new CommandArgument[0];
    }

    @Override
    public String getCommand() {
        return "lanTeamSize";
    }

    @Override
    public String getDescription() {
        return BedwarsRel._l("commands.tourney.lansetteamsize.description");
    }

    @Override
    public String getName() {
        return BedwarsRel._l("commands.tourney.lansetteamsize.name");
    }

    @Override
    public String getPermission() {
        return "manage";
    }

    @Override
    public List<BedwarsRel.Mode> blockDuringMode() {
        return Arrays.asList(BedwarsRel.Mode.NORMAL, BedwarsRel.Mode.TOURNAMENT);
    }
}
