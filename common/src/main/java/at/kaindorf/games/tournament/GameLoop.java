package at.kaindorf.games.tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.tournament.models.TourneyKoMatch;
import at.kaindorf.games.tournament.models.TourneyMatch;
import at.kaindorf.games.tournament.models.TourneyPlayer;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.tournament.rounds.GroupStage;
import at.kaindorf.games.tournament.rounds.KoRound;
import at.kaindorf.games.tournament.rounds.KoStage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GameLoop extends BukkitRunnable {
  private final JavaPlugin plugin;
  private final int qualifiedTeams;
  private final boolean rematchKo, rematchFinal;
  private final List<Game> games;

  public GameLoop(JavaPlugin plugin, int qualifiedTeams, boolean rematchKo, boolean rematchFinal) {
    this.plugin = plugin;
    this.qualifiedTeams = qualifiedTeams;
    this.rematchKo = rematchKo;
    this.rematchFinal = rematchFinal;

    games = BedwarsRel.getInstance().getGameManager().getGames();
  }

  @Override
  public void run() {
    Bukkit.getLogger().info("start Tournament");
    Tournament.getInstance().identifyPlayers();
    GroupStage groupStage = Tournament.getInstance().getGroupStage();
    KoStage koStage = Tournament.getInstance().getKoStage();

    Bukkit.getLogger().info("-------");
    getWaitingGames().forEach(g -> Bukkit.getLogger().info(g.getName()));
//    groupStage.getMatchesToDo().stream().map(m -> (TourneyMatch) m).forEach(m -> Bukkit.getLogger().info(m.toString()));

    if(!groupStage.isFinished()) {
      Bukkit.getLogger().info("Group Stage");
      tryToStartGames(groupStage.getMatchesToDo().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList()), getWaitingGames());
    } else if(koStage == null) {
      Bukkit.getLogger().info("transition");
      List<TourneyTeam> teams = groupStage.getQualifiedTeamsForKoRound(qualifiedTeams);
      Tournament.getInstance().generateKoMatches(teams, 2, rematchKo, rematchFinal);
    } else if(!koStage.isFinished()) {
      Bukkit.getLogger().info("Ko Stage");
      KoRound koRound = koStage.currentKoRound();
      if(koRound.isFinished()) {
        koStage.nextKoRound();
      } else {
        tryToStartGames(koRound.getMatchesTodo().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList()), getWaitingGames());
      }
    }

   /* Tournament.getInstance().identifyPlayers();
    GroupStage groupStage = Tournament.getInstance().getGroupStage();
    // Group Stage Loop
    while (!groupStage.isFinished() && isRunning) {
      Bukkit.getLogger().info("-------");
      getWaitingGames().forEach(g -> Bukkit.getLogger().info(g.getName()));
      groupStage.getMatchesToDo().stream().map(m -> (TourneyMatch) m).forEach(m -> Bukkit.getLogger().info(m.toString()));

      tryToStartGames(groupStage.getMatchesToDo().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList()), getWaitingGames());

      sleep();
    }
    List<TourneyTeam> teams = groupStage.getQualifiedTeamsForKoRound(qualifiedTeams);
    Tournament.getInstance().generateKoMatches(teams, 2, rematchKo, rematchFinal);


    // Ko Stage Loop
    KoStage koStage = Tournament.getInstance().getKoStage();
    while (!koStage.isFinished() && isRunning) {
      KoRound koRound = koStage.getKoRounds().get(koStage.getCurrentKoRound());

      while (koRound.isFinished()  && isRunning) {
        tryToStartGames(koRound.getMatchesTodo().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList()), getWaitingGames());

        sleep();
      }
      koStage.nextKoRound();
    }*/
  }

  private void sleep() {
    try {
      // look if players joint after tournament start
      long elapsedTime = identifyPlayers();
      Thread.sleep((5000 - elapsedTime <= 0) ? 0 : 5000 - elapsedTime);
    } catch (InterruptedException e) {
      Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
    }
  }

  private long identifyPlayers() {
    long start = System.currentTimeMillis();
    Tournament.getInstance().identifyPlayers();
    return System.currentTimeMillis() - start;
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
      index++;
    }
  }

  private void throwPlayersIntoTheGame(List<TourneyPlayer> players, Game game) {
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
}
