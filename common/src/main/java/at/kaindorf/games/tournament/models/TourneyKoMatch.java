package at.kaindorf.games.tournament.models;

import java.util.List;

public class TourneyKoMatch extends TourneyMatch {
  private TourneyKoMatch rematch;

  public TourneyKoMatch(List<TourneyTeam> teams) {
    super(teams);
    this.rematch = null;
  }

  public TourneyKoMatch(TourneyKoMatch rematch) {
    super(null);
    this.rematch = rematch;
  }

  public TourneyKoMatch getRematch() {
    return rematch;
  }

  public void setRematch(TourneyKoMatch rematch) {
    this.rematch = rematch;
  }
}
