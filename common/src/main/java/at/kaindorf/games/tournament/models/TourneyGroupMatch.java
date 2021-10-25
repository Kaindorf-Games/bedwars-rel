package at.kaindorf.games.tournament.models;

import java.util.List;

public class TourneyGroupMatch extends TourneyMatch {
  private TourneyGroup group;

  public TourneyGroupMatch(List<TourneyTeam> teams) {
    super(teams);
    group = null;
  }

  @Override
  public String toString() {
    return group.getName() + " -> " + teams.stream().map(TourneyTeam::getName).reduce((t1, t2) -> t1 + ", " + t2).orElse("");
  }

  public TourneyTeamStatistics getMatchStatistics(TourneyTeam team) {
    return team.getStatistics().stream().filter(stat -> stat.getMatch().equals(this)).findFirst().orElse(null);
  }

  public TourneyGroup getGroup() {
    return group;
  }

  public void setGroup(TourneyGroup group) {
    this.group = group;
  }
}
