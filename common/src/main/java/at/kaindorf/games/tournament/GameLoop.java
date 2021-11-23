package at.kaindorf.games.tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.tournament.models.TourneyMatch;
import at.kaindorf.games.tournament.models.TourneyPlayer;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.tournament.models.TourneyTeamStatistics;
import at.kaindorf.games.tournament.rounds.GroupStage;
import at.kaindorf.games.tournament.rounds.KoRound;
import at.kaindorf.games.tournament.rounds.KoStage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class GameLoop extends BukkitRunnable {
  private final JavaPlugin plugin;
  private final int qualifiedTeams;
  private final boolean rematchKo, rematchFinal;
  private final List<Game> games;
  private Tournament tournament;

  public GameLoop(JavaPlugin plugin, int qualifiedTeams, boolean rematchKo, boolean rematchFinal) {
    this.plugin = plugin;
    this.qualifiedTeams = qualifiedTeams;
    this.rematchKo = rematchKo;
    this.rematchFinal = rematchFinal;

    games = BedwarsRel.getInstance().getGameManager().getGames();
    tournament = Tournament.getInstance();
  }

  @Override
  public void run() {
    if (tournament.isHardStop()) {
      Bukkit.getLogger().info("Hard Stop");
      stopAllGames();
      tournament.cancel();
    } else if (tournament.isSoftStop() && !areGamesRunning()) {
      tournament.cancel();
    } else if (tournament.isSoftStop()) {
      return;
    }

    tournament.identifyPlayers();
    GroupStage groupStage = tournament.getGroupStage();
    KoStage koStage = tournament.getKoStage();

    Bukkit.getLogger().info("-------");
    getWaitingGames().forEach(g -> Bukkit.getLogger().info(g.getName()));
//    groupStage.getMatchesToDo().stream().map(m -> (TourneyMatch) m).forEach(m -> Bukkit.getLogger().info(m.toString()));

    if (!groupStage.isFinished()) {
      Bukkit.getLogger().info("Group Stage");
      tryToStartGames(groupStage.getMatchesToDo().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList()), getWaitingGames());
    } else if (koStage == null) {
      Bukkit.getLogger().info("transition");
      List<TourneyTeam> teams = groupStage.getQualifiedTeamsForKoRound(qualifiedTeams);
      tournament.generateKoMatches(teams, 2, rematchKo, rematchFinal);
    } else if (!koStage.isFinished()) {
      Bukkit.getLogger().info("Ko Stage");
      KoRound koRound = koStage.currentKoRound();
      if (koRound.isFinished()) {
        koStage.nextKoRound();
      } else {
        tryToStartGames(koRound.getMatchesTodo().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList()), getWaitingGames());
      }
    }
  }

  private void tryToStartGames(List<TourneyMatch> matches, List<Game> waitingGames) {
    int index = 0;
    while (waitingGames.size() > 0 && index < matches.size()) {
      TourneyMatch match = matches.get(index);


      if (areTeamsPlaying(match.getTeams())) {
        index++;
        continue;
      }

      Game game = waitingGames.remove(0);
      game.setMatch(match);
      setTeamsPlaying(match.getTeams(), game);
      throwPlayersIntoTheGame(match.getTeams().stream().map(TourneyTeam::getPlayers).reduce(this::connectPlayerLists).get(), game);
      // add team statistics to the teams
      match.getTeams().forEach(team -> team.addStatistic(new TourneyTeamStatistics(TourneyTeamStatistics.currentId++, match, 0, 0, false)));
      index++;
    }
  }

  private void throwPlayersIntoTheGame(List<TourneyPlayer> players, Game game) {
    game.kickAllPlayers();

    for (TourneyPlayer tourneyPlayer : players) {
      if (tourneyPlayer.getPlayer() != null) {
        game.playerJoins(tourneyPlayer.getPlayer());
      }
    }
  }

  private List<Game> getWaitingGames() {
    return games.stream().filter(g -> g.getState() == GameState.WAITING).collect(Collectors.toList());
  }

  private boolean areTeamsPlaying(List<TourneyTeam> teams) {
    for (TourneyTeam team : teams) {
      if (team.inGame()) {
        return true;
      }
    }
    return false;
  }

  private void setTeamsPlaying(List<TourneyTeam> teams, Game game) {
    teams.forEach(t -> t.setGame(game));
  }

  private List<TourneyPlayer> connectPlayerLists(List<TourneyPlayer> p1, List<TourneyPlayer> p2) {
    p1.addAll(p2);
    return p1;
  }

  private boolean areGamesRunning() {
    return games.stream().anyMatch(g -> g.getState() == GameState.RUNNING);
  }

  private void stopAllGames() {
    games.forEach(g->g.stop());
  }
}
