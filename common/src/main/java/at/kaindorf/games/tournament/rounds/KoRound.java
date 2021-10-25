package at.kaindorf.games.tournament.rounds;

import at.kaindorf.games.tournament.TourneyProperties;
import at.kaindorf.games.tournament.models.TourneyKoMatch;
import at.kaindorf.games.tournament.models.TourneyMatch;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.tournament.models.TourneyTeamStatistics;
import at.kaindorf.games.utils.Pair;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class KoRound {
  private List<TourneyKoMatch> matchesTodo;
  private List<TourneyKoMatch> matchesDone;
  private KoRound dependsOn;
  private boolean rematch;

  public KoRound(KoRound dependsOn, boolean rematch) {
    this.matchesTodo = new LinkedList<>();
    this.matchesDone = new LinkedList<>();
    this.dependsOn = dependsOn;
    this.rematch = rematch;
  }

  public void generateMatches(List<TourneyTeam> teams) {
    for (int i = 0; i < teams.size() / 4; i++) {
      TourneyKoMatch match = new TourneyKoMatch(Arrays.asList(teams.get(4 * i), teams.get(4 * i + 1), teams.get(4 * i + 2), teams.get(4 * i + 3)));
      matchesTodo.add(match);
      if (rematch)
        matchesTodo.add(new TourneyKoMatch(match));
    }
  }

  public void matchPlayed(TourneyKoMatch match) {
    matchesTodo.remove(match);
    matchesDone.add(match);
  }

  public boolean isKoRoundFinished() {
    return matchesTodo.size() == 0;
  }

  public List<TourneyTeam> getWinnersOfCurrentKoRound(int qualifiedForNextKoRound) {
    if (!isKoRoundFinished()) {
      return null;
    }
    List<Pair<Integer, TourneyTeam>> teams = new LinkedList<>();

    List<TourneyKoMatch> firstRoundMatches = matchesDone.stream().filter(m -> m.getTeams() != null).collect(Collectors.toList());
    for (TourneyKoMatch match : firstRoundMatches) {
      match.getTeams().forEach(t -> {
        int points = calculatePointsForMatchAndTeam(match, t);
        if (rematch) {
          TourneyKoMatch rematch = matchesDone.stream().filter(match1 -> match1.getRematch().equals(match)).findFirst().get();
          points += calculatePointsForMatchAndTeam(rematch, t);
        }

        teams.add(new Pair<>(points*-1, t));
      });
    }
    return teams.stream().sorted(Comparator.comparingInt(Pair::getFirst)).limit(qualifiedForNextKoRound).map(Pair::getSecond).collect(Collectors.toList());
  }

  private int calculatePointsForMatchAndTeam(TourneyMatch match, TourneyTeam t) {
    Optional<TourneyTeamStatistics> optional = t.getStatistics().stream().filter(st -> st.getMatch().equals(match)).findFirst();
    if (optional.isPresent()) {
      TourneyTeamStatistics statistics = optional.get();
      int points = statistics.getDestroyedBeds() * Integer.parseInt(String.valueOf(TourneyProperties.get("pointsForBed")));
      points += statistics.getFinalKills() * Integer.parseInt(String.valueOf(TourneyProperties.get("pointsForFinalKills")));
      points += (statistics.isWin() ? 1 : 0) * Integer.parseInt(String.valueOf(TourneyProperties.get("pointsForWin")));
      return points;
    }
    return 0;
  }
}
