package at.kaindorf.games.tournament.models;

import at.kaindorf.games.tournament.rounds.KoRound;
import org.bukkit.Bukkit;

import java.util.List;

public class TourneyKoMatch extends TourneyMatch {
    private TourneyKoMatch rematch;
    private int rematchId;

    public TourneyKoMatch(List<TourneyTeam> teams) {
        super(teams, currentId++);
        this.rematch = null;
    }

    public TourneyKoMatch(List<TourneyTeam> teams, TourneyKoMatch rematch) {
        super(teams, currentId++);
        this.rematch = rematch;
    }

    public TourneyKoMatch(int id, List<TourneyTeam> teams, int rematchId) {
        super(teams, id);
        this.rematchId = rematchId;
    }

    public TourneyKoMatch getRematch() {
        return rematch;
    }

    public void setRematch(TourneyKoMatch rematch) {
        this.rematch = rematch;
    }

    public void findRematch(KoRound koRound) {
        if (rematchId != -1) {
            rematch = koRound.getMatchPerId(rematchId);
        }
    }

    @Override
    public String toString() {
        if (rematch != null) {
            return "KoStage Rematch: " + rematch;
        }
        return "KoStage: " + teams.stream().map(TourneyTeam::getName).reduce((t1, t2) -> t1 + " " + t2).get();
    }
}
