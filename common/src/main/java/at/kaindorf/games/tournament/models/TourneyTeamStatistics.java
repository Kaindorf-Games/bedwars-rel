package at.kaindorf.games.tournament.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourneyTeamStatistics {
  private TourneyMatch match;
  private int finalKills, destroyedBeds;
  private boolean win;

  public void setWin() {
    this.win = true;
  }

  public void addFinalKill() {
    this.finalKills++;
  }

  public void addDestroyedBed() {
    this.destroyedBeds++;
  }
}
