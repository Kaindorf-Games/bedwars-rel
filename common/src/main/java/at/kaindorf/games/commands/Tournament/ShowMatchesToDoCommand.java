package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.CurrentState;
import at.kaindorf.games.tournament.models.TourneyMatch;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.tournament.rounds.GroupStage;
import at.kaindorf.games.tournament.rounds.KoStage;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShowMatchesToDoCommand extends BaseCommand {

  public ShowMatchesToDoCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    if (!Tournament.getInstance().isTournamentRunning()) {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.tournamentnotrunning"));
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

    GroupStage gs = Tournament.getInstance().getGroupStage();
    KoStage ks = Tournament.getInstance().getKoStage();
    List<TourneyMatch> matches;

    if (Tournament.getInstance().getCurrentState() == CurrentState.GROUP_STAGE) {
      matches = gs.getMatchesToDo().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList());
    } else if (Tournament.getInstance().getCurrentState() == CurrentState.KO_STAGE) {
      matches = ks.currentKoRound().getMatchesTodo().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList());
    } else {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.tournamentintrans"));
      return true;
    }

    if(matches.size() == 0) {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.nomatchestodo"));
      return true;
    }

    String out = matches.stream().map(this::buildChatOutput).reduce((m1, m2) -> m1 + m2).orElse(BedwarsRel._l("tourney.errors.nomatches"));

    ChatWriter.paginateOutput(sender, out, page);

    return true;
  }

  private String buildChatOutput(TourneyMatch match) {
    StringBuilder sb = new StringBuilder();

    sb.append(BedwarsRel._l("tourney.others.match")+" " + match.getId() + ": " + ((match.isRunning()) ? " ("+BedwarsRel._l("tourney.others.running")+")\n" : "\n"));
    sb.append("  " + match.getTeams().stream().map(TourneyTeam::getName).reduce((t1, t2) -> t1 + ", " + t2).orElse("") + "\n");

    return sb.toString();
  }

  @Override
  public CommandArgument[] getNewArguments() {
    return new CommandArgument[]{new CommandArgument("page", true)};
  }

  @Override
  public String getCommand() {
    return "showMatches";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.tourney.showmatchestodo.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.showmatchestodo.name");
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
