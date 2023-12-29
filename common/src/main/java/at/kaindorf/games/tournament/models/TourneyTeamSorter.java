package at.kaindorf.games.tournament.models;

import lombok.Data;

import java.util.List;

@Data
public class TourneyTeamSorter implements Comparable<TourneyTeamSorter> {

    private int id;
    private double points;
    private double finalKills;
    private double destroyedBeds;
    private double wins;
    // not required for tournament mode, but for leaderboard
    private double extraPoints;
    private double gamesPlayed;

    private void init(int id) {
        this.id = id;
        this.points = 0;
        this.finalKills = 0;
        this.destroyedBeds = 0;
        this.wins = 0;
        this.extraPoints = 0;
        this.gamesPlayed = 0;
    }

    public TourneyTeamSorter(int id, List<TourneyGameStatistic> stats, boolean groupMatches) {
        init(id);
        for (TourneyGameStatistic statistic : stats) {
            if ((groupMatches && statistic.getMatch() instanceof TourneyGroupMatch) || (!groupMatches && statistic.getMatch() instanceof TourneyKoMatch)) {
                addAttributes(statistic);
            }
        }
    }

    public TourneyTeamSorter(int id, List<TourneyGameStatistic> stats, List<Integer> matchIds) {
        init(id);
        for (TourneyGameStatistic statistic : stats) {
            if (matchIds.contains(statistic.getMatch().getId())) {
                addAttributes(statistic);
            }
        }
    }

    private void addAttributes(TourneyGameStatistic statistic) {
        this.finalKills += statistic.getFinalKills();
        this.destroyedBeds += statistic.getDestroyedBeds();
        this.gamesPlayed += 1d;
        this.wins += statistic.isWin() ? 1d : 0d;
        this.extraPoints += statistic.getExtraPoints();
        this.points = statistic.calculatePoints();
    }

    @Override
    public int compareTo(TourneyTeamSorter other) {
        if (other.points == this.points) {
            if (other.wins == this.wins) {
                if (other.destroyedBeds == this.destroyedBeds) {
                    if (other.finalKills == this.finalKills) {
                        return 0;
                    }
                    return Double.compare(other.finalKills, this.finalKills);
                }
                return Double.compare(other.destroyedBeds, this.destroyedBeds);
            }
            return Double.compare(other.wins, this.wins);
        }
        return Double.compare(other.points, this.points);
    }
}
