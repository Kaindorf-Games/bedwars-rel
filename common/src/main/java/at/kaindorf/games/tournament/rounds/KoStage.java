package at.kaindorf.games.tournament.rounds;

import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.TourneyMatch;
import at.kaindorf.games.tournament.models.TourneyTeam;
import lombok.Data;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class KoStage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<Integer, KoRound> koRounds;
    private int currentKoRound, qualifiedForNextRound, createdKoRounds;
    private boolean rematch, rematchFinal, finished = false;

    public KoStage(int qualifiedForNextKoRound, boolean rematch, boolean rematchFinal) {
        currentKoRound = 0;
        createdKoRounds = 0;
        koRounds = new HashMap<>();
        this.qualifiedForNextRound = qualifiedForNextKoRound;
        this.rematch = rematch;
        this.rematchFinal = rematchFinal;
    }

    public KoRound currentKoRound() {
        return koRounds.get(currentKoRound);
    }

    public void addKoRound(KoRound koRound) {
        createdKoRounds++;
        koRounds.put(createdKoRounds, koRound);
    }

    private List<TourneyTeam> getWinnerOfCurrentKoRound() {
        return koRounds.get(currentKoRound).getWinnersOfCurrentKoRound(qualifiedForNextRound);
    }

    public void nextKoRound() {
        List<TourneyTeam> teams = getWinnerOfCurrentKoRound();

        // it was a final
        if (!koRounds.containsKey(currentKoRound + 1)) {
            teams = currentKoRound().getMatchesDone().get(0).getTeams();
            TourneyTeam winner = Tournament.getInstance().findWinner(teams, currentKoRound().getMatchesDone().stream().map(m -> (TourneyMatch) m).collect(Collectors.toList()));
            Tournament.getInstance().announceWinner(winner);
            this.finished = true;
            return;
        }

        currentKoRound++;
        Collections.shuffle(teams);
        koRounds.get(currentKoRound).generateMatches(teams);
    }

    public void startKoRound() {
        currentKoRound++;
    }

    public void generateKoStage(List<TourneyTeam> teams) {
        int teamsRemaining = teams.size();
        Bukkit.getLogger().info("Teams Remaining: " + teamsRemaining);
        if (teamsRemaining <= 4) {
            KoRound finalRound = new KoRound(null, rematchFinal);
            finalRound.generateFinal(teams);
            addKoRound(finalRound);
            finalRound.getMatchesTodo().forEach(m -> Bukkit.getLogger().info(m.toString()));
            startKoRound();
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
        startKoRound();
    }
}
