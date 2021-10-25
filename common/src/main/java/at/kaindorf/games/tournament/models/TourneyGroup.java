package at.kaindorf.games.tournament.models;

import lombok.Data;

import java.util.ArrayList;

@Data
public class TourneyGroup {
  private String name;
  private ArrayList<TourneyTeam> teams;

  public TourneyGroup(String name) {
    this.name = name;
    this.teams = new ArrayList<>();
  }

  public void addTeam(TourneyTeam team) {
    teams.add(team);
  }
}
