package at.kaindorf.games.tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.game.Team;
import at.kaindorf.games.tournament.models.*;
import at.kaindorf.games.tournament.rounds.GroupStage;
import at.kaindorf.games.tournament.rounds.KoRound;
import at.kaindorf.games.tournament.rounds.KoStage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class GameLoop extends BukkitRunnable {
  private final int qualifiedTeams;
  private final boolean rematchKo, rematchFinal;
  private final List<Game> games;
  private final Tournament tournament;
  private int counter = 5;

  public GameLoop(int qualifiedTeams, boolean rematchKo, boolean rematchFinal) {
    this.qualifiedTeams = qualifiedTeams;
    this.rematchKo = rematchKo;
    this.rematchFinal = rematchFinal;

    games = BedwarsRel.getInstance().getGameManager().getGames();
    tournament = Tournament.getInstance();
  }

  private boolean checkIfTournamentIsStopped() {
    if (tournament.getGroupStage().isFinished() && tournament.getKoStage() == null) {
      return false;
    }
    if (tournament.isHardStop() || (tournament.isSoftStop() && !areGamesRunning() && counter == 0)) {
      Bukkit.getLogger().info("Do the Stop");
      stopAllGames();
      tournament.cancel();
      return true;
    } else if (tournament.isSoftStop()) {
      if (!areGamesRunning()) decrementCounter();

      Bukkit.getLogger().info("wait for Soft Stop: " + counter);
      return true;
    }
    return false;
  }

  @Override
  public void run() {
    if (checkIfTournamentIsStopped()) {
      return;
    }

    tournament.identifyPlayers();
    GroupStage groupStage = tournament.getGroupStage();
    KoStage koStage = tournament.getKoStage();

    Bukkit.getLogger().info("-------");
//    getWaitingGames().forEach(g -> Bukkit.getLogger().info(g.getName()));
//    groupStage.getMatchesToDo().stream().map(m -> (TourneyMatch) m).forEach(m -> Bukkit.getLogger().info(m.toString()));
//    Bukkit.getLogger().info("Groupstage finished: "+groupStage.isFinished());
//    Bukkit.getLogger().info("KoStage value: "+(koStage==null));
//    Bukkit.getLogger().info("KoStage finished: "+(koStage != null && koStage.isFinished()));

    if (!groupStage.isFinished()) {
      Bukkit.getLogger().info("Group Stage");
      List<TourneyMatch> matches = groupStage.getMatchesToDo().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList());
      removeMatchesWithOneTeam(matches, true);
      tryToStartGames(matches, getWaitingGames());
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
        List<TourneyMatch> matches = koRound.getMatchesTodo().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList());
        removeMatchesWithOneTeam(matches, false);
        tryToStartGames(matches, getWaitingGames());
      }
    }
  }

  private void removeMatchesWithOneTeam(List<TourneyMatch> matches, boolean groupStage) {
    for (TourneyMatch match : matches) {
      if (match.getTeams().size() == 1) {
        match.getTeams().get(0).addStatistic(new TourneyGameStatistic(TourneyGameStatistic.currentId++, match, 0, 0, true, 0));
        if (groupStage) {
          Tournament.getInstance().getGroupStage().matchPlayed((TourneyGroupMatch) match);
        } else {
          Tournament.getInstance().getKoStage().currentKoRound().matchPlayed((TourneyKoMatch) match);
        }
      }
    }
  }

  private void tryToStartGames(List<TourneyMatch> matches, List<Game> waitingGames) {
    int index = 0;
    while (waitingGames.size() > 0 && index < matches.size()) {
      TourneyMatch match = matches.get(index);

      if (!areTeamsReady(match.getTeams())) {
        index++;
        continue;
      }

      Game game = waitingGames.remove(0);
      game.setMatch(match);
      setTeamsPlaying(match.getTeams(), game);
      assignTeamColors(match.getTeams(), game);
      throwPlayersIntoTheGame(match.getTeams().stream().map(TourneyTeam::getPlayers).reduce(this::connectPlayerLists).get(), game);
      // add team statistics to the teams
      match.getTeams().forEach(team -> team.addStatistic(new TourneyGameStatistic(TourneyGameStatistic.currentId++, match, 0, 0, false, 0)));
      match.setGame(game);
      index++;
    }
  }

  private void assignTeamColors(List<TourneyTeam> teams, Game game) {
    List<Team> teamColors = new ArrayList<>(game.getTeams().values());
    Collections.shuffle(teamColors); // ensure that not always the same colors are used

    for (int i = 0; i < teams.size(); i++) {
      teams.get(i).setTeamColor(teamColors.get(i).getColor());
    }
  }

  private void throwPlayersIntoTheGame(List<TourneyPlayer> players, Game game) {
    game.kickAllPlayers();

    for (TourneyPlayer tourneyPlayer : players) {
      if (tourneyPlayer.getPlayer() != null && tourneyPlayer.getPlayer().isOnline()) {
        game.playerJoins(tourneyPlayer.getPlayer());

        // throw players into their team
        Optional<TourneyTeam> teamOfPlayer = Tournament.getInstance().getTourneyTeamOfPlayer(tourneyPlayer.getPlayer());
        if (teamOfPlayer.isPresent()) {
          Team team = game.getTeam(teamOfPlayer.get().getTeamColor().name().toLowerCase());
          game.playerJoinTeam(tourneyPlayer.getPlayer(), team);
        }
      }
    }
  }

  private List<Game> getWaitingGames() {
    return games.stream().filter(g -> g.getMatch() == null).collect(Collectors.toList());
  }

  private boolean areTeamsReady(List<TourneyTeam> teams) {
    for (TourneyTeam team : teams) {
      if (!team.isReady()) {
        return false;
      }
    }
    return true;
  }

  private void setTeamsPlaying(List<TourneyTeam> teams, Game game) {
    teams.forEach(t -> t.setGame(game));
  }

  private List<TourneyPlayer> connectPlayerLists(List<TourneyPlayer> p1, List<TourneyPlayer> p2) {
    List<TourneyPlayer> players = new LinkedList<>();
    players.addAll(p1);
    players.addAll(p2);
    return players;
  }

  private boolean areGamesRunning() {
    return games.stream().anyMatch(g -> g.getState() == GameState.RUNNING);
  }

  private void stopAllGames() {
    games.forEach(Game::stop);
    games.forEach(g -> g.setState(GameState.WAITING));
  }

  private void decrementCounter() {
    counter--;
  }
}
