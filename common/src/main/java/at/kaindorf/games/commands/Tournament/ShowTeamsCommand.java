package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.CurrentState;
import at.kaindorf.games.tournament.models.TourneyPlayer;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowTeamsCommand extends BaseCommand implements ICommand {
  public ShowTeamsCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    if (Tournament.getInstance().getTeams().size() == 0) {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.teamsnotfound"));
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
    List<TourneyTeam> teams = Tournament.getInstance().getTeams();
    for (TourneyTeam team : teams) {
      for (TourneyPlayer player : team.getPlayers()) {
        if (player.getPlayer() == null) {
          player.initPlayer();
        }
      }
    }
    StringBuilder sb = new StringBuilder();

    for (TourneyTeam team : teams) {
      String paused = "";
      if (team.isPaused()) paused = " ("+BedwarsRel._l("tourney.others.paused")+")";

      sb.append(ChatColor.YELLOW + team.getName() + " [" + team.getShortname() + "]" + paused + "\n");
      team.getPlayers().forEach(p -> {
        sb.append(ChatColor.YELLOW + "  " + (p.getPlayer() == null ? p.getUuid() : p.getPlayer().getName()) + "\n");
      });
      sb.append(ChatColor.YELLOW + "  "+BedwarsRel._l("tourney.others.pointsgroup")+" " + team.calculatePoints(CurrentState.GROUP_STAGE) + "\n");
      sb.append(ChatColor.YELLOW + "  "+BedwarsRel._l("tourney.others.pointsko")+" " + team.calculatePoints(CurrentState.KO_STAGE) + "\n\n");
    }
    return sb.toString();
  }

  @Override
  public CommandArgument[] getNewArguments() {
    return new CommandArgument[]{new CommandArgument("page", true)};
  }

  @Override
  public String getCommand() {
    return "showteams";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.tourney.showteams.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.showteams.name");
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
