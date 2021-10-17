package at.kaindorf.games.tournament;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class TourneyGroup {
  private String name;
  private List<TourneyTeam> teams;

  public TourneyGroup(String name) {
    this.name = name;
    this.teams = new LinkedList<>();
  }

  public void addTeam(TourneyTeam team) {
    teams.add(team);
  }
}
