package at.kaindorf.games.tournament.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TourneyMatch {
  protected List<TourneyTeam> teams;
}
