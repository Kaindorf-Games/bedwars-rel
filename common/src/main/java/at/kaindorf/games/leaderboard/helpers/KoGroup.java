package at.kaindorf.games.leaderboard.helpers;

import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.tournament.models.TourneyTeamSorter;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Data
public class KoGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<GroupMember> teams;

    public KoGroup(String name, List<TourneyTeam> teams) {
        this.name = name;
        this.teams = new LinkedList<>();

        for (TourneyTeam tourneyTeam : teams) {
            TourneyTeamSorter ts = new TourneyTeamSorter(tourneyTeam.getId(), tourneyTeam.getStatistics(), true);
            this.teams.add(new GroupMember(tourneyTeam.getName(), tourneyTeam.getShortname(), ts));
        }
    }
}
