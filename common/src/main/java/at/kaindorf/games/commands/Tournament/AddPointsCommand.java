package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.material.Bed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddPointsCommand extends BaseCommand {
  public AddPointsCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    int points = Integer.parseInt(args.remove(0));
    String team = args.stream().reduce((s1, s2) -> s1 + " " +s2).orElse("");

    boolean res = Tournament.getInstance().addPointsToTeam(team, points);

    if(res) {
      sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("tourney.info.pointsadded"));
    } else {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.cantaddpoints"));
    }

    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"points<int>", "teamName"};
  }

  @Override
  public String getCommand() {
    return "addpoints";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.tourney.addpoints.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.addpoints.name");
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
