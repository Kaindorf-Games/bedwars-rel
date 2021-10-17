package at.kaindorf.games.tournament;

import at.kaindorf.games.exceptions.TournamentEntityExistsException;
import at.kaindorf.games.utils.Utils;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
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

  private TourneyGroup getGroup(String name) {
    return groups.stream().filter(g -> g.getName().equals(name)).findFirst().get();
  }

  private TourneyTeam getTeam(String name) {
    return teams.stream().filter(t -> t.getName().equals(name)).findFirst().get();
  }

  private TourneyGroup getGroupOfTeam(String team) {
    return groups.stream().filter(g -> g.getTeams().stream().anyMatch(t -> t.getName().equals(team))).findFirst().get();
  }

  private TourneyTeam getTeamOfPlayer(String player) {
    return teams.stream().filter(t -> t.getPlayers().stream().anyMatch(p -> p.getName().equals(player))).findFirst().get();
  }

  public void clear() {
    this.players.clear();
    this.teams.clear();
    this.groups.clear();
    this.matches.clear();
  }

  public void show() {
    String gs = groups.stream().map(TourneyGroup::getName).reduce((g1, g2) -> g1 + ", " + g2).orElse("-");
    String ts = teams.stream().map(TourneyTeam::getName).reduce((g1, g2) -> g1 + ", " + g2).orElse("-");
    String ps = players.stream().map(TourneyPlayer::getName).reduce((g1, g2) -> g1 + ", " + g2).orElse("-");

    Bukkit.getLogger().info("Groups: " + gs);
    Bukkit.getLogger().info("Teams: " + ts);
    Bukkit.getLogger().info("Players: " + ps);
  }

  @SneakyThrows
  public boolean save(File groupsFile, File teamsFile, File playersFile) {
    // save groups
    YamlConfiguration yml = new YamlConfiguration();
    for (int i = 0; i < groups.size(); i++) {
      yml.set("game"+i, Utils.tourneyGroupSerialize(groups.get(i)));
    }
    yml.save(groupsFile);

    // save teams
    yml = new YamlConfiguration();
    for (int i = 0; i < teams.size(); i++) {
      TourneyTeam team = teams.get(i);
      yml.set("team"+i, Utils.tourneyTeamSerialize(team, getGroupOfTeam(team.getName()).getName()));
    }
    yml.save(teamsFile);

    // save players
    yml = new YamlConfiguration();
    for (int i = 0; i < players.size(); i++) {
      TourneyPlayer player = players.get(i);
      yml.set("player"+i, Utils.tourneyPlayerSerialize(player, getTeamOfPlayer(player.getName()).getName()));
    }
    yml.save(playersFile);

    return true;
  }
}
