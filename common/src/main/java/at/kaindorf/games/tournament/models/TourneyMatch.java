package at.kaindorf.games.tournament.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;

import java.util.List;

@Data
@AllArgsConstructor
public class TourneyMatch {
  protected List<TourneyTeam> teams;
  protected int id;

  protected static int currentId = 0;

  @Override
  public String toString() {
    if(teams != null) {
      return " -> " + teams.stream().map(TourneyTeam::getName).reduce((t1, t2) -> t1 + ", " + t2).orElse("");
    } else {
      return "is a rematch";
    }
//    teams.forEach(team -> team.getPlayers().forEach(p -> Bukkit.getLogger().info("" + (p.getPlayer() == null))));
  }
}
