package at.kaindorf.games.tournament.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TourneyGameStatistic {
  private int id;
  private TourneyMatch match;
  private int finalKills, destroyedBeds;
  private boolean win;
  public static int currentId = 0;

  public TourneyGameStatistic(int id, TourneyMatch match, int finalKills, int destroyedBeds, boolean win) {
    this.id = id;
    this.match = match;
    this.finalKills = finalKills;
    this.destroyedBeds = destroyedBeds;
    this.win = win;

    if(id > currentId) {
      currentId = id;
    }
  }

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
