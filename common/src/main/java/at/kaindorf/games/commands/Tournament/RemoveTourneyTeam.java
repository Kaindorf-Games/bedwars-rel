package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class RemoveTourneyTeam extends BaseCommand implements ICommand {
  public RemoveTourneyTeam(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      sender.sendMessage(ChatWriter.wrongPermissionMessage());
      return false;
    }

    if (args.size() > 0) {
      String name = args.stream().reduce((n1, n2) -> n1 + " " + n2).get();

      if (Tournament.getInstance().deleteTourneyTeamByName(name)) {
        sender.sendMessage(ChatColor.GREEN+"Team deleted");
      } else {
        sender.sendMessage(ChatColor.RED+"Team not found (404)");
      }
      return true;
    }

    return false;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"teamName"};
  }

  @Override
  public String getCommand() {
    return "teamRemove";
  }

  @Override
  public String getDescription() {
    return "removes a team";
  }

  @Override
  public String getName() {
    return "remove team";
  }

  @Override
  public String getPermission() {
    return "manage";
  }

  @Override
  public BedwarsRel.Mode blockDuringMode() {
    return BedwarsRel.Mode.NORMAL;
  }
}
