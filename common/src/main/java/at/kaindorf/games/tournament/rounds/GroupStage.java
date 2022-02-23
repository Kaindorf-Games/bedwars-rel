package at.kaindorf.games.tournament.rounds;

import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.models.CurrentState;
import at.kaindorf.games.tournament.models.TourneyGroup;
import at.kaindorf.games.tournament.models.TourneyGroupMatch;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.utils.Pair;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.*;
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

  public List<TourneyGroupMatch> getRemainingMatchesForTeam(TourneyTeam team) {
    return matchesToDo.stream().filter(m -> m.getTeams().stream().anyMatch(t -> t.getName().equals(team.getName()))).collect(Collectors.toList());
  }

  public boolean isFinished() {
    return matchesToDo.size() == 0;
  }

  public List<TourneyTeam> getQualifiedTeamsForKoRound(int numberOfQualifiedTeamPerGroup) {
    if(!isFinished()) {
      return null;
    }
    List<TourneyTeam> qualifiedTeams = new LinkedList<>();
    for (TourneyGroup group : Tournament.getInstance().getGroups()) {
      List<Pair<TourneyTeam, Integer>> teams = new LinkedList<>();
      for (TourneyTeam t : group.getTeams()) {
        teams.add(new Pair<>(t, -1*t.calculatePoints(CurrentState.GROUP_STAGE)));
      }
      teams.sort(Comparator.comparingInt(Pair::getSecond));
      qualifiedTeams.addAll(teams.stream().sorted(Comparator.comparingInt(Pair::getSecond)).limit(numberOfQualifiedTeamPerGroup).map(Pair::getFirst).collect(Collectors.toList()));
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

    if(teams.size() == 0) {
      return;
    }

    // create groups
    int anzGroups = (int) Math.ceil(teams.size()/(double) groupSize);
    for (int i = 0;i<anzGroups;i++) {
      Tournament.getInstance().addGroup("Group "+(char)('A'+i));
    }

    // add teams into groups
    Collections.shuffle(teams);
    for (int i = 0;i<teams.size(); i++) {
      groups.get(i%anzGroups).addTeam(teams.get(i));
    }

    // generate Matches
    for(int round = 0; round < groupStageRounds; round++) {
      for(TourneyGroup g: groups) {
        for(int t = 0;t < g.getTeams().size(); t++) {
          for(int j = t+1; j<g.getTeams().size();j++) {
            this.addToDoMatch(new TourneyGroupMatch(Arrays.asList(g.getTeams().get(t), g.getTeams().get(j)), round));
          }
        }
      }
    }

//    groups.forEach(g -> Bukkit.getLogger().info(g.getName() + ": "+g.getTeams().stream().map(TourneyTeam::getName).reduce((t1, t2) -> t1 + ", "+t2).orElse("")));
//    this.getMatchesToDo().forEach(m -> Bukkit.getLogger().info(m.getId() + ": " + m.getGroup().getName() +", "+ m.getRound() + ", "+m.getTeams().stream().map(TourneyTeam::getName).reduce((t1, t2) -> t1 + "|"+t2).orElse("")));
  }
}
