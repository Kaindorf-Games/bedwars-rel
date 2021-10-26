package at.kaindorf.games.tournament;

import at.kaindorf.games.exceptions.TournamentEntityExistsException;
import at.kaindorf.games.tournament.models.TourneyGroup;
import at.kaindorf.games.tournament.models.TourneyGroupMatch;
import at.kaindorf.games.tournament.models.TourneyPlayer;
import at.kaindorf.games.tournament.models.TourneyTeam;
import at.kaindorf.games.tournament.rounds.GroupStage;
import at.kaindorf.games.tournament.rounds.KoStage;
import at.kaindorf.games.utils.Loader;
import at.kaindorf.games.utils.Pair;
import at.kaindorf.games.utils.Saver;
import at.kaindorf.games.utils.UsernameFetcher;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Data
public class Tournament {
  private static Tournament instance;
  private boolean tournamentRunning = false;

  private KoStage koStage;
  private GroupStage groupStage;

  public static Tournament getInstance() {
    if (instance == null) {
      instance = new Tournament();
    }
    return instance;
  }

  private List<TourneyGroup> groups;
  private List<TourneyTeam> teams;
  private List<TourneyPlayer> players;

  private Tournament() {
    this.groups = new LinkedList<>();
    this.teams = new ArrayList<>();
    this.players = new ArrayList<>();

    loadSaves();
  }

  public void loadSaves() {
    Bukkit.getLogger().info("Loading Tournament Saves ...");
    try {
      // load Groups
      for (String group : Loader.loadSavedGroups()) {
        addGroup(group);
      }

      // load Teams
      for (Pair<String, String> p : Loader.loadSavedTeams()) {
        addTeam(p.getFirst(), p.getSecond());
      }

      // load Player
      for (Pair<TourneyPlayer, String> p : Loader.loadSavedPlayers()) {
        TourneyPlayer player = p.getFirst();
        addPlayer(player.getUuid(), p.getSecond(), player.getKills(), player.getDestroyedBeds());
      }
    } catch (TournamentEntityExistsException e) {
      Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
      this.clear();
    }

  }

  public void addGroup(String name) throws TournamentEntityExistsException {
    Optional<TourneyGroup> optional = groups.stream().filter(g -> g.getName().equals(name)).findFirst();
    if (!optional.isPresent()) {
      groups.add(new TourneyGroup(name));
    }
  }

  public void addTeam(String name, String groupName) throws TournamentEntityExistsException {
    Optional<TourneyTeam> optional = teams.stream().filter(t -> t.getName().equals(name)).findFirst();
    if (optional.isPresent()) {
      throw new TournamentEntityExistsException("Team " + name + " exists already");
    }
    TourneyTeam team = new TourneyTeam(name);
    teams.add(team);
    this.getGroup(groupName).addTeam(team);
  }

  public void addPlayer(String uuid, String teamName) throws TournamentEntityExistsException {
    addPlayer(uuid, teamName, 0, 0);
  }

  public void addPlayer(String uuid, String teamName, int kills, int destroyedBeds) throws TournamentEntityExistsException {
    Optional<TourneyPlayer> optional = players.stream().filter(p -> p.getUuid().equals(uuid)).findFirst();
    if (optional.isPresent()) {
      throw new TournamentEntityExistsException("Player " + uuid + " exists already");
    }
    String username = UsernameFetcher.getUsernameFromUUID(uuid);
    TourneyPlayer player = new TourneyPlayer(uuid, username, kills, destroyedBeds);
    players.add(player);
    this.getTeam(teamName).addPlayer(player);
  }


  private TourneyGroup getGroup(String name) {
    return groups.stream().filter(g -> g.getName().equals(name)).findFirst().get();
  }

  private TourneyTeam getTeam(String name) {
    return teams.stream().filter(t -> t.getName().equals(name)).findFirst().get();
  }

  public void clear() {
    this.players.clear();
    this.teams.clear();
    this.groups.clear();
  }

  public void show() {
    String gs = groups.stream().map(TourneyGroup::getName).reduce((g1, g2) -> g1 + ", " + g2).orElse("-");
    String ts = teams.stream().map(TourneyTeam::getName).reduce((g1, g2) -> g1 + ", " + g2).orElse("-");
    String ps = players.stream().map(TourneyPlayer::getUsername).reduce((g1, g2) -> g1 + ", " + g2).orElse("-");

    Bukkit.getLogger().info("Groups: " + gs);
    Bukkit.getLogger().info("Teams: " + ts);
    Bukkit.getLogger().info("Players: " + ps);
  }

  @SneakyThrows
  public void save() {
    Saver.clear();
    Saver.saveGroups(this.groups);
    Saver.saveTeams(this.teams);
    Saver.savePlayers(this.players);
  }


  /*TODO: Generates Matches automatically. At the moment he gets the Matches from a File*/
  public boolean generateGroupMatches() {
    groupStage = new GroupStage();
    return groupStage.readGroupStageFromFile();
  }

  private TourneyTeam findTeam(TourneyGroup g, TourneyTeam t) {
    return g.getTeams().stream().filter(te -> te.getName().equals(t.getName())).findFirst().orElse(null);
  }

  private List<TourneyTeam> getQualifiedTeamsForKoRound(int numberOfQualifiedTeamsPerGroup) {
    return groupStage.getQualifiedTeamsForKoRound(numberOfQualifiedTeamsPerGroup);
  }

  public boolean generateKoMatches(List<TourneyTeam> teams, int qualifiedForNextKoRound, boolean rematch, boolean rematchFinal) {
    koStage = new KoStage(qualifiedForNextKoRound, rematch, rematchFinal);

    koStage.generateKoStage(teams);

    return true;
  }

  public void clearRunningTournament() {
    this.koStage = null;
    this.groupStage = null;
    this.tournamentRunning = false;
  }
}
