package at.kaindorf.games.tournament;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TourneyTeamStatistics {
  private TourneyMatch match;
  private int kills, destroyedBeds;
  private boolean win;

}
