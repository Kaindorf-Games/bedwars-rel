package at.kaindorf.games.tournament.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;

import java.util.List;

@Data
public class TourneyMatch {
  protected List<TourneyTeam> teams;
  protected int id;

  protected static int currentId = 0;

  public TourneyMatch(List<TourneyTeam> teams, int id) {
    this.teams = teams;
    this.id = id;
    if(id > currentId) {
      currentId = id;
    }
  }

  @Override
  public String toString() {
    if(teams != null) {
      return " -> " + teams.stream().map(TourneyTeam::getName).reduce((t1, t2) -> t1 + ", " + t2).orElse("");
    } else {
      return "is a rematch";
    }
  }
}
