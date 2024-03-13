package at.kaindorf.games.leaderboard.helpers;

import at.kaindorf.games.tournament.models.TourneyKoMatch;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.tournament.models.TourneyTeamSorter;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
public class KoMatch implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<GroupMember> teams;
    private String status;

    public KoMatch(TourneyKoMatch match, String status) {
        this.teams = new LinkedList<>();
        this.status = status;
        for (TourneyTeam tourneyTeam : match.getTeams()) {
            List<Integer> matchIds = new LinkedList<>();
            matchIds.add(match.getId());
            if (match.getRematch() != null) {
                matchIds.add(match.getRematch().getId());
            }

            TourneyTeamSorter ts = new TourneyTeamSorter(tourneyTeam.getId(), tourneyTeam.getStatistics(), matchIds);
            this.teams.add(new GroupMember(tourneyTeam.getName(), tourneyTeam.getShortname(), ts));
        }
    }


}
