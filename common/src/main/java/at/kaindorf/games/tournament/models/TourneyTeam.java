package at.kaindorf.games.tournament.models;

import at.kaindorf.games.game.Game;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class TourneyTeam {
  private List<TourneyPlayer> players;
  private List<TourneyTeamStatistics> statistics;
  private String name;
  private Game game;

  public TourneyTeam(String name) {
    this.name = name;
    this.players = new LinkedList<>();
    this.statistics = new LinkedList<>();
    game = null;
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
}

