package at.kaindorf.games.tournament;

import at.kaindorf.games.exceptions.TournamentEntityExistsException;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Data
public class Tournament {
  private static Tournament instance;

  public static Tournament getInstance() {
    if (instance == null) {
      instance = new Tournament();
    }
    return instance;
  }

  private List<TourneyGroup> groups;
  private List<TourneyTeam> teams;
  private List<TourneyPlayer> players;
  private List<TourneyMatch> matches;

  private Tournament() {
    this.groups = new LinkedList<>();
    this.teams = new ArrayList<>();
    this.players = new ArrayList<>();
    this.matches = new ArrayList<>();
  }

  public void addGroup(String name) {
    Optional<TourneyGroup> optional = groups.stream().filter(g -> g.getName().equals(name)).findFirst();
    if (!optional.isPresent()) {
      groups.add(new TourneyGroup(name));
    }
  }

  public void addTeam(String name, String groupName) throws TournamentEntityExistsException {
    Optional<TourneyTeam> optional = teams.stream().filter(t -> t.getName().equals(name)).findFirst();
    if (optional.isPresent()) {
      throw new TournamentEntityExistsException("Team exists already: " + name);
    }
    TourneyTeam team = new TourneyTeam(name);
    teams.add(team);
    this.getGroup(groupName).addTeam(team);
  }

  public void addPlayer(String name, String teamName) throws TournamentEntityExistsException {
    Optional<TourneyPlayer> optional = players.stream().filter(p -> p.getName().equals(name)).findFirst();
    if (optional.isPresent()) {
      throw new TournamentEntityExistsException("Player exists already: " + name);
    }
    TourneyPlayer player = new TourneyPlayer(name);
    players.add(player);
    this.getTeam(teamName).addPlayer(player);
  }

  public TourneyGroup getGroup(String name) {
    return groups.stream().filter(g -> g.getName().equals(name)).findFirst().get();
  }

  public TourneyTeam getTeam(String name) {
    return teams.stream().filter(t -> t.getName().equals(name)).findFirst().get();
  }

  public void clear() {
    this.players.clear();
    this.teams.clear();
    this.groups.clear();
    this.matches.clear();
  }

  public void show() {
    String gs = groups.stream().map(TourneyGroup::getName).reduce((g1,g2) -> g1 + ", "+g2).orElse("-");
    String ts = teams.stream().map(TourneyTeam::getName).reduce((g1,g2) -> g1 + ", "+g2).orElse("-");
    String ps = players.stream().map(TourneyPlayer::getName).reduce((g1,g2) -> g1 + ", "+g2).orElse("-");

    Bukkit.getLogger().info("Groups: "+gs);
    Bukkit.getLogger().info("Teams: "+ts);
    Bukkit.getLogger().info("Players: "+ps);
  }
}
