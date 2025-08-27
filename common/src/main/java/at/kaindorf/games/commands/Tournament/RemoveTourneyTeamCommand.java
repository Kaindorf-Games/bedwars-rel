package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoveTourneyTeamCommand extends BaseCommand implements ICommand {
  public RemoveTourneyTeamCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    if (args.size() > 0) {
      String name = args.stream().reduce((n1, n2) -> n1 + " " + n2).get();

      if (Tournament.getInstance().deleteTourneyTeamByName(name)) {
        sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("tourney.info.teamremoved"));
      } else {
        sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.teamnotexists"));
      }
      return true;
    }

    return false;
  }

  @Override
  public CommandArgument[] getNewArguments() {
    return new CommandArgument[]{new CommandArgument("teamName", String.class)};
  }

  @Override
  public String getCommand() {
    return "removeTeam";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.tourney.removeteam.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.removeteam.name");
  }

  @Override
  public String getPermission() {
    return "manage";
  }

  @Override
  public List<BedwarsRel.Mode> blockDuringMode() {
    return Arrays.asList(BedwarsRel.Mode.NORMAL);
  }
}
