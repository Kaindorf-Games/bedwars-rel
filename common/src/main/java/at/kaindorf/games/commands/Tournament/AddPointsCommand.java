package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

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
      sender.sendMessage(ChatColor.GREEN+"Points were added");
    } else {
      sender.sendMessage(ChatColor.RED+"Either the team doesn't exist or the team hasn't played any matches so far");
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
    return "add points to the team";
  }

  @Override
  public String getName() {
    return "addpoints";
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
