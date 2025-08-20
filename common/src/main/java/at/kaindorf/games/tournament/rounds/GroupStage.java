package at.kaindorf.games.tournament.rounds;

import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.TourneyGroup;
import at.kaindorf.games.tournament.models.TourneyGroupMatch;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.tournament.models.TourneyTeamSorter;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class GroupStage {
    private List<TourneyGroupMatch> matchesToDo;
    private List<TourneyGroupMatch> matchesDone;

    public GroupStage(int groupSize, int groupStageRounds) {
        matchesDone = new LinkedList<>();
        matchesToDo = new LinkedList<>();

        this.generateGroupStage(groupSize, groupStageRounds);
    }

    public GroupStage() {
        matchesDone = new LinkedList<>();
        matchesToDo = new LinkedList<>();
    }

    public void matchPlayed(TourneyGroupMatch match) {
        matchesToDo.remove(match);
        matchesDone.add(match);
    }

    public boolean isFinished() {
        return matchesToDo.isEmpty();
    }

    public List<TourneyTeam> getQualifiedTeamsForKoRound(int numberOfQualifiedTeamPerGroup) {
        if (!isFinished()) {
            return null;
        }
        List<TourneyTeam> qualifiedTeams = new LinkedList<>();
        for (TourneyGroup group : Tournament.getInstance().getGroups()) {
            List<TourneyTeamSorter> teams = new LinkedList<>();
            for (TourneyTeam t : group.getTeams()) {
                teams.add(new TourneyTeamSorter(t.getId(), t.getStatistics(), true));
            }
            teams = teams.stream().sorted().collect(Collectors.toList());
            for (int i = 0; i < teams.size(); i++) {
                if (i >= numberOfQualifiedTeamPerGroup) {
                    break;
                }
                int teamId = teams.get(i).getId();
                qualifiedTeams.add(group.getTeams().stream().filter(t -> t.getId() == teamId).findFirst().orElse(null));
            }
        }

        return qualifiedTeams;
    }

    public void addToDoMatch(TourneyGroupMatch match) {
        matchesToDo.add(match);
    }

    public void addDoneMatch(TourneyGroupMatch match) {
        matchesDone.add(match);
    }

    @SneakyThrows
    private void generateGroupStage(int groupSize, int groupStageRounds) {
        List<TourneyTeam> teams = Tournament.getInstance().getTeams();
        List<TourneyGroup> groups = Tournament.getInstance().getGroups();

        Bukkit.getLogger().info(teams.size() + "");
        Bukkit.getLogger().info(groups.size() + "");

        if (teams.size() <= 1) {
            return;
        }


        int anzGroups = (int) Math.ceil(teams.size() / (double) groupSize);
        // remove the possibility that a group contains only one team
        while (teams.size() / anzGroups < 2) {
            anzGroups--;
        }

        // create groups
        for (int i = 0; i < anzGroups; i++) {
            Tournament.getInstance().addGroup("Group " + (char) ('A' + i));
        }

        // add teams into groups
        Collections.shuffle(teams);
        for (int i = 0; i < teams.size(); i++) {
            groups.get(i % anzGroups).addTeam(teams.get(i));
        }

        // generate Matches
        for (int round = 0; round < groupStageRounds; round++) {
            for (TourneyGroup g : groups) {
                this.addToDoMatch(new TourneyGroupMatch(g.getTeams(), round));
            }
        }

//    groups.forEach(g -> Bukkit.getLogger().info(g.getName() + ": "+g.getTeams().stream().map(TourneyTeam::getName).reduce((t1, t2) -> t1 + ", "+t2).orElse("")));
//    this.getMatchesToDo().forEach(m -> Bukkit.getLogger().info(m.getId() + ": " + m.getGroup().getName() +", "+ m.getRound() + ", "+m.getTeams().stream().map(TourneyTeam::getName).reduce((t1, t2) -> t1 + "|"+t2).orElse("")));
    }
}
