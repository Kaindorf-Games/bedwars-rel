package at.kaindorf.games.tournament.rounds;

import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.TourneyProperties;
import at.kaindorf.games.tournament.models.*;
import at.kaindorf.games.utils.Pair;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class GroupStage {
  private List<TourneyGroupMatch> matchesToDo;
  private List<TourneyGroupMatch> matchesDone;

  public GroupStage() {
    matchesDone = new LinkedList<>();
    matchesToDo = new LinkedList<>();
  }

  @SneakyThrows
  public boolean readGroupStageFromFile() {
    if(!TourneyProperties.groupStageFile.exists()) return false;
    YamlConfiguration yaml = new YamlConfiguration();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(new FileInputStream(TourneyProperties.groupStageFile), "UTF-8"));
    yaml.load(reader);
    for (String key : yaml.getKeys(false)) {
      List<String> teamNames = yaml.getStringList(key);
      // translating teamNames into TourneyTeam Objects
      List<TourneyTeam> teams = teamNames.stream().map(tn -> Tournament.getInstance().getTeams().stream().filter(t -> t.getName().equals(tn)).findFirst().orElse(null)).collect(Collectors.toList());
      matchesToDo.add(new TourneyGroupMatch(teams));
    }
    return true;
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
}
