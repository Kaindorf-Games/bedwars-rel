package at.kaindorf.games.tournament.models;

import lombok.Data;

import java.util.ArrayList;

@Data
public class TourneyGroup {
  private String name;
  private ArrayList<TourneyTeam> teams;
  private int id;

  public static int currentId = 0;

  public TourneyGroup(int id, String name) {
    this.name = name;
    this.id = id;
    this.teams = new ArrayList<>();

    if(this.id > currentId) {
      currentId = this.id;
    }
  }

  public void addTeam(TourneyTeam team) {
    teams.add(team);
    team.setGroup(this);
  }
}
