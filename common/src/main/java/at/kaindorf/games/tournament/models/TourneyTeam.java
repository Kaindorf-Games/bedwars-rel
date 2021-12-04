package at.kaindorf.games.tournament.models;

import at.kaindorf.games.game.Game;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class TourneyTeam {
  private int id;
  private List<TourneyPlayer> players;
  private List<TourneyTeamStatistics> statistics;
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

  public void addStatistic(TourneyTeamStatistics teamStatistics) {
    this.statistics.add(teamStatistics);
  }

  public boolean inGame() {
    return game != null;
  }

  public boolean isReady() {
    long onlinePlayers = players.stream().map(TourneyPlayer::getPlayer).filter(p -> p != null && p.isOnline()).count();

//    return onlinePlayers >= 2 && !inGame();
    return onlinePlayers >= 1 && !inGame();
  }
}

