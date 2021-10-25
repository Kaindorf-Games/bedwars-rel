package at.kaindorf.games.tournament.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TourneyTeamStatistics {
  private TourneyMatch match;
  private int finalKills, destroyedBeds;
  private boolean win;

}
