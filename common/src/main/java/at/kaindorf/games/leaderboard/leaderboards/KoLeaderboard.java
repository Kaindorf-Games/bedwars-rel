package at.kaindorf.games.leaderboard.leaderboards;

import at.kaindorf.games.leaderboard.helpers.GroupColumn;
import at.kaindorf.games.leaderboard.helpers.KoBracket;
import at.kaindorf.games.leaderboard.helpers.KoGroup;
import at.kaindorf.games.leaderboard.observer.LeaderboardBase;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.CurrentState;
import at.kaindorf.games.tournament.models.TourneyGroup;
import at.kaindorf.games.tournament.rounds.KoRound;
import at.kaindorf.games.tournament.rounds.KoStage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@Data
@EqualsAndHashCode(callSuper = false)
public class KoLeaderboard extends LeaderboardBase implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<KoBracket> koStage = null;
    private List<KoGroup> groups = null;
    private List<GroupColumn> columns;
    private Map<Integer, String> sortedBy;
    private int groupStageQualifiedTeams;

    public KoLeaderboard() {
        this.type = "KoGroup";
        this.columns = GroupColumn.initKoLeaderboardColumns();

        this.sortedBy = new HashMap<>();
        sortedBy.put(1, "Points");
        sortedBy.put(2, "Wins");
        sortedBy.put(3, "Beds Destroyed");
        sortedBy.put(4, "Final Kills");
    }

    public void setStages(List<TourneyGroup> groups, KoStage koStage, CurrentState state) {
        this.groupStageQualifiedTeams = Tournament.getInstance().getQualifiedTeams();
        this.koStage = new LinkedList<>();

        if ((state == CurrentState.KO_STAGE || state == CurrentState.TRANSITION) && koStage != null) {
            Set<Integer> rounds = koStage.getKoRounds().keySet().stream().map(k -> k * -1).collect(Collectors.toSet());

            int matches = 1;
            for (Integer round : rounds) {
                KoRound koRound = koStage.getKoRounds().get(round * -1);
                this.koStage.add(new KoBracket(round * -1, matches, koRound, matches == 1, koStage.getQualifiedForNextRound()));
                matches *= 2;
            }
        }

        if (state == CurrentState.GROUP_STAGE || state == CurrentState.TRANSITION) {
            this.groups = new LinkedList<>();
            for (TourneyGroup group : groups) {
                this.groups.add(new KoGroup(group.getName(), group.getTeams()));
            }
        }

        updateDataToLeaderboard(this);
    }

}
