package at.kaindorf.games.tournament.models;

import at.kaindorf.games.game.Game;
import at.kaindorf.games.tournament.TourneyProperties;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class TourneyTeam {
  private int id;
  private List<TourneyPlayer> players;
  private List<TourneyGameStatistic> statistics;
  private String name;
  private Game game;

  private static int currentId = 0;

  public TourneyTeam(String name) {
    this.name = name;
    this.players = new LinkedList<>();
    this.statistics = new LinkedList<>();
    game = null;
    this.id = currentId++;
  }

  public TourneyTeam(int id, String name) {
    this.name = name;
    this.players = new LinkedList<>();
    this.statistics = new LinkedList<>();
    game = null;
    this.id = id;

    if(this.id > currentId) {
      currentId = this.id;
    }
  }

  public void addPlayer(TourneyPlayer player) {
    this.players.add(player);
  }

  public void addStatistic(TourneyGameStatistic teamStatistics) {
    this.statistics.add(teamStatistics);
  }

  public boolean inGame() {
    return game != null;
  }

  public boolean isReady() {
    long onlinePlayers = players.stream().map(TourneyPlayer::getPlayer).filter(p -> p != null && p.isOnline()).count();

//    return onlinePlayers >= 2 && !inGame() && !paused;
    return onlinePlayers >= 1 && !inGame() && !paused;
  }

  public int calculatePoints(CurrentState state) {
    int points = 0;

    for(TourneyGameStatistic stat : statistics) {
      if(state != null) {
        if(!(state == CurrentState.GROUP_STAGE && stat.getMatch() instanceof TourneyGroupMatch) && !(state == CurrentState.KO_STAGE && stat.getMatch() instanceof TourneyKoMatch)) {
          continue;
        }
      }
      points += stat.getFinalKills() * TourneyProperties.pointsForFinalKill;
      points += stat.getDestroyedBeds() * TourneyProperties.pointsForBed;
      points += (stat.isWin() ? 1 : 0) * TourneyProperties.pointsForWin;
    }

    return points;
  }

  public int calculatePoints() {
    return calculatePoints(null);
  }
}

