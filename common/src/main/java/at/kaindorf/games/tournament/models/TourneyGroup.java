package at.kaindorf.games.tournament.models;

import lombok.Data;

import java.util.ArrayList;

@Data
public class TourneyGroup {
  private String name;
  private ArrayList<TourneyTeam> teams;
  private int id;
  private static int currentId = 0;

  public TourneyGroup(String name) {
    this.name = name;
    this.teams = new ArrayList<>();

    this.id = currentId++;
  }

  public void addTeam(TourneyTeam team) {
    teams.add(team);
  }
}
