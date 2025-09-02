package at.kaindorf.games.tournament.rounds;

import at.kaindorf.games.tournament.models.TourneyKoMatch;
import at.kaindorf.games.tournament.models.TourneyMatch;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.tournament.models.TourneyTeamSorter;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class KoRound implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<TourneyKoMatch> matchesTodo;
    private List<TourneyKoMatch> matchesDone;
    private transient KoRound dependsOn;
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
                matchesTodo.add(new TourneyKoMatch(teams, match));
        }

        Collections.shuffle(this.matchesTodo);
    }

    public void generateFinal(List<TourneyTeam> teams) {
        TourneyKoMatch match = new TourneyKoMatch(teams);
        matchesTodo.add(match);
        if (rematch)
            matchesTodo.add(new TourneyKoMatch(teams, match));
    }

    public void matchPlayed(TourneyKoMatch match) {
        matchesTodo.remove(match);
        matchesDone.add(match);
    }

    public boolean isFinished() {
        return matchesTodo.size() == 0;
    }

    public List<TourneyTeam> getWinnersOfCurrentKoRound(int qualifiedForNextKoRound) {
        if (!isFinished()) {
            return null;
        }
        List<TourneyTeam> qualifiedTeams = new LinkedList<>();
        List<TourneyKoMatch> matches = matchesDone.stream().filter(m -> m.getTeams() != null).collect(Collectors.toList());
        for (TourneyKoMatch match : matches) {
            if (rematch && match.getRematch() == null) {
                continue;
            }
            List<Integer> matchIds = new LinkedList<>();
            matchIds.add(match.getId());
            if (match.getRematch() != null) {
                matchIds.add(match.getRematch().getId());
            }
            List<TourneyTeamSorter> ranking = new LinkedList<>();
            for (TourneyTeam team : match.getTeams()) {
                ranking.add(new TourneyTeamSorter(team.getId(), team.getStatistics(), matchIds));
            }

            ranking = ranking.stream().sorted().collect(Collectors.toList());
            for (int i = 0; i < ranking.size(); i++) {
                if (i >= qualifiedForNextKoRound) {
                    break;
                }
                int teamId = ranking.get(i).getId();
                qualifiedTeams.add(match.getTeams().stream().filter(t -> t.getId() == teamId).findFirst().orElse(null));
            }

        }
        return qualifiedTeams;
    }

    public void addToDoMatch(TourneyKoMatch match) {
        matchesTodo = addAndSortMatches(match, matchesTodo);

    }

    public void addDoneMatch(TourneyKoMatch match) {
        matchesDone = addAndSortMatches(match, matchesDone);
    }

    private List<TourneyKoMatch> addAndSortMatches(TourneyKoMatch newMatch, List<TourneyKoMatch> matches) {
        matches.add(newMatch);
        return matches.stream().sorted(Comparator.comparingInt(TourneyMatch::getId)).collect(Collectors.toList());
    }

    public TourneyKoMatch getMatchPerId(int id) {
        Optional<TourneyKoMatch> match = matchesTodo.stream().filter(m -> m.getId() == id).findFirst();
        if (match.isPresent()) {
            return match.get();
        }

        match = matchesDone.stream().filter(m -> m.getId() == id).findFirst();
        return match.orElse(null);

    }
}
