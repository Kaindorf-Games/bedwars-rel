package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import net.minecraft.server.v1_8_R3.ExceptionInvalidNumber;
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
            sender.sendMessage(ChatColor.RED+"Team size must be a positive integer");
            return true;
        }
        Tournament.getInstance().setLanTeamSizes(teams);
        sender.sendMessage(ChatColor.GREEN+"Team size set to "+teams);

        return true;
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }

    @Override
    public String getCommand() {
        return "lanTeamSize";
    }

    @Override
    public String getDescription() {
        return "current="+ Tournament.getInstance().getLanTeamSizes();
    }

    @Override
    public String getName() {
        return "lanTeamSize";
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
