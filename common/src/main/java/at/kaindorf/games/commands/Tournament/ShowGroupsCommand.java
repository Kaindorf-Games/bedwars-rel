package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.CurrentState;
import at.kaindorf.games.tournament.models.TourneyGroup;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowGroupsCommand extends BaseCommand implements ICommand {

    public ShowGroupsCommand(BedwarsRel plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (!sender.hasPermission("tourney." + this.getPermission())) {
            ChatWriter.wrongPermissionMessage(sender);
            return false;
        }

        if (Tournament.getInstance().getGroups().size() == 0) {
            sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.nogroups"));
            return true;
        }

        int page = 1;
        if (args.size() > 0) {
            try {
                page = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.nonum"));
                return false;
            }
        }

        String output = buildChatOutput();

        ChatWriter.paginateOutput(sender, output, page);

        return true;
    }

    private String buildChatOutput() {
        List<TourneyGroup> groups = Tournament.getInstance().getGroups();
        StringBuilder sb = new StringBuilder();

        for (TourneyGroup group : groups) {
            sb.append(ChatColor.YELLOW + "" + group.getName() + "\n");
            group.getTeams().forEach(t -> {
                String paused = "";
                if (t.isPaused()) paused = " ("+BedwarsRel._l("tourney.others.paused")+")";
                sb.append(ChatColor.YELLOW + "  " + t.getName() + paused + " - " + t.calculatePoints(CurrentState.GROUP_STAGE) + " "+BedwarsRel._l("tourney.others.points")+"\n");
            });
        }

        return sb.toString();
    }


    @Override
    public CommandArgument[] getNewArguments() {
        return new CommandArgument[]{new CommandArgument("page", true)};
    }

    @Override
    public String getCommand() {
        return "showgroups";
    }

    @Override
    public String getDescription() {
        return BedwarsRel._l("commands.tourney.showgroups.description");
    }

    @Override
    public String getName() {
        return BedwarsRel._l("commands.tourney.showgroups.name");
    }

    @Override
    public String getPermission() {
        return "player";
    }

    @Override
    public List<BedwarsRel.Mode> blockDuringMode() {
        return Arrays.asList(BedwarsRel.Mode.NORMAL);
    }
}
