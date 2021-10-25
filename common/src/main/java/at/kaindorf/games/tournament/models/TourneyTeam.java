package at.kaindorf.games.tournament.models;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class TourneyTeam {
  private List<TourneyPlayer> players;
  private List<TourneyTeamStatistics> statistics;
  private String name;

  public TourneyTeam(String name) {
    this.name = name;
    this.players = new LinkedList<>();
    this.statistics = new LinkedList<>();
  }

  public void addPlayer(TourneyPlayer player) {
    this.players.add(player);
  }

  public void addStatistic(TourneyTeamStatistics teamStatistics) {
    this.statistics.add(teamStatistics);
  }

}

