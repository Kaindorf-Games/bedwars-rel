package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.*;
import at.kaindorf.games.tournament.rounds.GroupStage;
import at.kaindorf.games.tournament.rounds.KoStage;
import at.kaindorf.games.utils.ChatWriter;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SkipMatchCommand extends BaseCommand {

  public SkipMatchCommand(BedwarsRel plugin) {
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

    if (args.get(0).equals("*")) {
      skipAll(sender);
      return true;
    }

    int matchId;
    try {
      matchId = Integer.parseInt(args.get(0));
    } catch (NumberFormatException ex) {
      sender.sendMessage(BedwarsRel._l("tourney.errors.nonum"));
      return false;
    }

    skipID(sender, matchId);

    return true;
  }

  private void sendMatchDoesNotExist(CommandSender sender, int id) {
    sender.sendMessage(BedwarsRel._l("tourney.errors.matchnotexist", ImmutableMap.of("id", ""+id)));
  }

  private void verifyIfMatchIsRunning(List<TourneyMatch> matches, int id, CommandSender sender) {
    if (matches.stream().anyMatch(m -> m.getId() == id && m.isRunning())) {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.cantdeleterunningmatch"));
    }
  }

  private void skipAll(CommandSender sender) {
    GroupStage gs = Tournament.getInstance().getGroupStage();
    KoStage ks = Tournament.getInstance().getKoStage();

    if (Tournament.getInstance().getCurrentState() == CurrentState.GROUP_STAGE) {
      // remove matches from to do list
      List<TourneyGroupMatch> matches = gs.getMatchesToDo().stream().filter(m -> !m.isRunning()).collect(Collectors.toList());
      gs.setMatchesToDo(gs.getMatchesToDo().stream().filter(TourneyMatch::isRunning).collect(Collectors.toList()));
      // add skipped matches to done matches and add Statistics object to each team
      for (TourneyGroupMatch match : matches) {
        match.getTeams().forEach(team -> team.addStatistic(new TourneyGameStatistic(TourneyGameStatistic.currentId++, match, 0, 0, false, 0)));
        gs.addDoneMatch(match);
      }
    } else if (Tournament.getInstance().getCurrentState() == CurrentState.KO_STAGE) {
      // remove matches from to do list
      List<TourneyKoMatch> matches = ks.currentKoRound().getMatchesTodo().stream().filter(m -> !m.isRunning()).collect(Collectors.toList());
      ks.currentKoRound().setMatchesTodo(ks.currentKoRound().getMatchesTodo().stream().filter(TourneyMatch::isRunning).collect(Collectors.toList()));
      // add skipped matches to done matches and add Statistics object to each team
      for (TourneyKoMatch match : matches) {
        match.getTeams().forEach(team -> team.addStatistic(new TourneyGameStatistic(TourneyGameStatistic.currentId++, match, 0, 0, false,0)));
        ks.currentKoRound().addDoneMatch(match);
      }
    } else {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.tournamentintrans"));
    }
  }

  private void skipID(CommandSender sender, int id) {
    GroupStage gs = Tournament.getInstance().getGroupStage();
    KoStage ks = Tournament.getInstance().getKoStage();

    if (!gs.isFinished()) {
      // removes match from to do list
      Optional<TourneyGroupMatch> match = gs.getMatchesToDo().stream().filter(m -> !m.isRunning() && m.getId() == id).findFirst();
      gs.setMatchesToDo(gs.getMatchesToDo().stream().filter(m -> m.isRunning() || m.getId() != id).collect(Collectors.toList()));

      if (!match.isPresent()) {
        sendMatchDoesNotExist(sender, id);
      } else {
        match.get().getTeams().forEach(team -> team.addStatistic(new TourneyGameStatistic(TourneyGameStatistic.currentId++, match.get(), 0, 0, false, 0)));
        gs.addDoneMatch(match.get()); // add skipped match to done matches and add Statistics object to each team
      }
      verifyIfMatchIsRunning(gs.getMatchesToDo().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList()), id, sender);
    } else if (gs.isFinished() && ks != null) {
      // removes match from to do list
      Optional<TourneyKoMatch> match = ks.currentKoRound().getMatchesTodo().stream().filter(m -> !m.isRunning() && m.getId() == id).findFirst();
      ks.currentKoRound().setMatchesTodo(ks.currentKoRound().getMatchesTodo().stream().filter(m -> m.isRunning() || m.getId() != id).collect(Collectors.toList()));

      if (!match.isPresent()) {
        sendMatchDoesNotExist(sender, id);
      } else {
        match.get().getTeams().forEach(team -> team.addStatistic(new TourneyGameStatistic(TourneyGameStatistic.currentId++, match.get(), 0, 0, false, 0)));
        ks.currentKoRound().addDoneMatch(match.get()); // add skipped match to done matches and add Statistics object to each team
      }
      verifyIfMatchIsRunning(ks.currentKoRound().getMatchesTodo().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList()), id, sender);
    } else {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.tournamentintrans"));
    }
  }

  @Override
  public String[] getArguments() {
    return new String[]{"matchID|*"};
  }

  @Override
  public String getCommand() {
    return "skip";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.tourney.skipmatch.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.skipmatch.name");
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
