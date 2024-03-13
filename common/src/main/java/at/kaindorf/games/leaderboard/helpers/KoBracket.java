package at.kaindorf.games.leaderboard.helpers;

import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.TourneyKoMatch;
import at.kaindorf.games.tournament.rounds.KoRound;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class KoBracket implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<KoMatch> matches;
    private int round;
    private int amountOfMatches;
    private int qualifiedTeams;

    public KoBracket(int round, int amountOfMatches, KoRound koRound, boolean isFinal, int qualifiedTeams) {
        this.matches = new LinkedList<>();
        this.round = round;
        this.amountOfMatches = amountOfMatches;
        this.qualifiedTeams = qualifiedTeams;

        if (isFinal) {
            this.qualifiedTeams = 1;
        }

        if (koRound.getMatchesDone() != null && koRound.getMatchesTodo() != null) {
            List<TourneyKoMatch> matches = Stream.concat(koRound.getMatchesDone().stream(), koRound.getMatchesTodo().stream()).collect(Collectors.toList());
            for (TourneyKoMatch match : matches) {
                if (isFinal && Tournament.getInstance().isRematchFinal() && match.getRematch() == null) {
                    continue;
                } else if (Tournament.getInstance().isRematchKo() && match.getRematch() == null) {
                    continue;
                }

                String status = "not played";
                if(koRound.getMatchesDone().contains(match)) {
                    status = "finished";
                } else if(match.isRunning()) {
                    status = "ongoing";
                }

                this.matches.add(new KoMatch(match, status));
            }
        }
    }
}
