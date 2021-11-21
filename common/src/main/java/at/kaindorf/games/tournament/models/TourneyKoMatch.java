package at.kaindorf.games.tournament.models;

import java.util.List;

public class TourneyKoMatch extends TourneyMatch {
  private TourneyKoMatch rematch;

  public TourneyKoMatch(List<TourneyTeam> teams) {
    super(teams, currentId++);
    this.rematch = null;
  }

  public TourneyKoMatch( List<TourneyTeam> teams, TourneyKoMatch rematch) {
    super(teams, currentId++);
    this.rematch = rematch;
  }

  public TourneyKoMatch getRematch() {
    return rematch;
  }

  public void setRematch(TourneyKoMatch rematch) {
    this.rematch = rematch;
  }
}
