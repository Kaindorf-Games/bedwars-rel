package at.kaindorf.games.tournament.models;

import at.kaindorf.games.game.Game;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class TourneyMatch {
  protected List<TourneyTeam> teams;
  protected int id;
  private Game game;
  private boolean aborted = false;

  protected static int currentId = 0;

  public TourneyMatch(List<TourneyTeam> teams, int id) {
    this.teams = teams;
    this.id = id;
    if(id > currentId) {
      currentId = id;
    }

    // randomize order of the teams
    // otherwise teams are mostly in the same bedwars team
    Collections.shuffle(this.teams);
  }

  @Override
  public String toString() {
    if(teams != null) {
      return " -> " + teams.stream().map(TourneyTeam::getName).reduce((t1, t2) -> t1 + ", " + t2).orElse("");
    } else {
      return "is a rematch";
    }
  }

  public boolean isRunning() {
    return game != null;
  }

  public static void resetId() {
    currentId = 0;
  }

  public int getMaxPlayersOfTeam() {
    return this.teams.stream().mapToInt(t -> t.getPlayers().size()).max().orElse(0);
  }
}
