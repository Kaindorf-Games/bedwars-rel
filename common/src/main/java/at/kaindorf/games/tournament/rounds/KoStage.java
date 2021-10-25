package at.kaindorf.games.tournament.rounds;

import at.kaindorf.games.tournament.models.TourneyKoMatch;
import at.kaindorf.games.tournament.models.TourneyTeam;
import lombok.Data;

import java.util.*;

@Data
public class KoStage {
  private Map<Integer, KoRound> koRounds;
  private int currentKoRound, qualifiedForNextRound, createdKoRounds;
  private boolean rematch, rematchFinal;

  public KoStage(int qualifiedForNextKoRound, boolean rematch, boolean rematchFinal) {
    currentKoRound = 0;
    createdKoRounds = 0;
    koRounds = new HashMap<>();
    this.qualifiedForNextRound = qualifiedForNextKoRound;
    this.rematch = rematch;
    this.rematchFinal = rematchFinal;
  }

  public void addKoRound(KoRound koRound) {
    createdKoRounds++;
    koRounds.put(createdKoRounds, koRound);
  }

  public List<TourneyKoMatch> getMatchesOfCurrentKoRound() {
    return (currentKoRound == 0) ? new LinkedList<>() : koRounds.get(currentKoRound).getMatchesTodo();
  }

  public void matchInCurrentKoRoundPlayed(TourneyKoMatch match) {
    koRounds.get(currentKoRound).matchPlayed(match);
  }

  public List<TourneyTeam> getWinnerOfCurrentKoRound() {
    return koRounds.get(currentKoRound).getWinnersOfCurrentKoRound(qualifiedForNextRound);
  }

  public void nextKoRound() {
    currentKoRound++;
  }

  public void generateKoStage(List<TourneyTeam> teams) {
    int teamsRemaining = teams.size();
    if (teamsRemaining == 4) {
      KoRound finalRound = new KoRound(null, rematchFinal);
      finalRound.generateMatches(teams);
      addKoRound(finalRound);
      return;
    }
    Collections.shuffle(teams);

    KoRound firstKoRound = new KoRound(null, rematch);
    firstKoRound.generateMatches(teams);
    addKoRound(firstKoRound);

    KoRound lastKoRound = firstKoRound;
    teamsRemaining = teamsRemaining / 4 * qualifiedForNextRound;
    while (teamsRemaining >= 4) {
      if (teamsRemaining == 4) {
        lastKoRound = new KoRound(lastKoRound, rematchFinal);
      } else {
        lastKoRound = new KoRound(lastKoRound, rematch);
      }
      addKoRound(lastKoRound);
      teamsRemaining = teamsRemaining / 4 * qualifiedForNextRound;
    }

    // start Ko Stage Mode
    nextKoRound();
  }
}
