package at.kaindorf.games.tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.events.TournamentStartEvent;
import at.kaindorf.games.exceptions.TournamentEntityExistsException;
import at.kaindorf.games.statistics.PlayerStatistic;
import at.kaindorf.games.tournament.models.*;
import at.kaindorf.games.tournament.rounds.GroupStage;
import at.kaindorf.games.tournament.rounds.KoStage;
import at.kaindorf.games.utils.Loader;
import at.kaindorf.games.utils.Pair;
import at.kaindorf.games.utils.Saver;
import at.kaindorf.games.utils.UsernameFetcher;
import lombok.Data;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Data
public class Tournament {
  private static Tournament instance;

  private KoStage koStage;
  private GroupStage groupStage;

  private boolean softStop, hardStop;
  private int qualifiedTeams;
  private boolean rematchKo, rematchFinal;

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

  public void addGroup(int id, String name) throws TournamentEntityExistsException {
    Optional<TourneyGroup> optional = groups.stream().filter(g -> g.getName().equals(name)).findFirst();
    if (!optional.isPresent()) {
      groups.add(new TourneyGroup(id, name));
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

  public void addTeam(int id, String name, String groupName) throws TournamentEntityExistsException {
    Optional<TourneyTeam> optional = teams.stream().filter(t -> t.getName().equals(name)).findFirst();
    if (optional.isPresent()) {
      throw new TournamentEntityExistsException("Team " + name + " exists already");
    }
    TourneyTeam team = new TourneyTeam(id, name);
    teams.add(team);
    if (!groupName.isEmpty()) {
      this.getGroup(groupName).addTeam(team);
    }
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

  public void addPlayer(int id, String uuid, String teamName, int kills, int destroyedBeds) throws TournamentEntityExistsException {
    Optional<TourneyPlayer> optional = players.stream().filter(p -> p.getUuid().equals(uuid)).findFirst();
    if (optional.isPresent()) {
      throw new TournamentEntityExistsException("Player " + uuid + " exists already");
    }
    String username = UsernameFetcher.getUsernameFromUUID(uuid);
    TourneyPlayer player = new TourneyPlayer(id, uuid, username, kills, destroyedBeds);
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

    this.hardStop = false;
    this.softStop = false;
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

  public void generateKoMatches(List<TourneyTeam> teams, int qualifiedForNextKoRound, boolean rematch, boolean rematchFinal) {
    koStage = new KoStage(qualifiedForNextKoRound, rematch, rematchFinal);

    koStage.generateKoStage(teams);
  }

  public void clearRunningTournament() {
    this.koStage = null;
    this.groupStage = null;
    if (BedwarsRel.getInstance().getGameLoopTask() != null) {
      BedwarsRel.getInstance().setGameLoopTask(null);
    }
  }

  public void announceWinner(TourneyTeam team) {
    BedwarsRel.getInstance().getGameLoopTask().cancel();
    BedwarsRel.getInstance().setGameLoopTask(null);

    // add to tournament Win to playerstatistics
    if (BedwarsRel.getInstance().statisticsEnabled()) {
      team.getPlayers().forEach(player -> {
        OfflinePlayer offlinePlayer = BedwarsRel.getInstance().getServer().getOfflinePlayer(UUID.fromString(player.getUuid()));
        PlayerStatistic winner = BedwarsRel.getInstance().getPlayerStatisticManager().getStatistic(offlinePlayer);

        winner.setCurrentTournamentWins(winner.getCurrentTournamentWins() + 1);
        winner.setCurrentScore(winner.getCurrentScore() + BedwarsRel
            .getInstance().getIntConfig("statistics.scores.tournamentWin", 150));

        BedwarsRel.getInstance().getPlayerStatisticManager().storeStatistic(winner);
      });

    }

    try {
      for (Player p : Bukkit.getServer().getOnlinePlayers()) {

        Class<?> clazz = Class.forName("at.kaindorf.games.com."
            + BedwarsRel.getInstance().getCurrentVersion().toLowerCase() + ".Title");
        Method showTitle = clazz
            .getDeclaredMethod("showTitle", Player.class, String.class,
                double.class, double.class, double.class);

        double titleFadeIn =
            BedwarsRel.getInstance().getConfig()
                .getDouble("titles.win.title-fade-in", 1.5);
        double titleStay =
            BedwarsRel.getInstance().getConfig().getDouble("titles.win.title-stay", 5.0);
        double titleFadeOut =
            BedwarsRel
                .getInstance().getConfig().getDouble("titles.win.title-fade-out", 2.0);

        showTitle.invoke(null, p, ChatColor.GOLD + team.getName() + " has won", titleFadeIn, titleStay, titleFadeOut);
      }
    } catch (Exception e) {
      Bukkit.getLogger().log(Level.SEVERE, e.getMessage());
    }
    Bukkit.getLogger().info("We have a Winner!!! - " + team.getName());
  }

  public boolean isTournamentRunning() {
    return BedwarsRel.getInstance().getGameLoopTask() != null;
  }

  public void identifyPlayers() {
    players.stream().filter(p -> p.getPlayer() == null).forEach(TourneyPlayer::initPlayer);
  }

  public Optional<TourneyTeam> getTourneyTeamOfPlayer(Player player) {
    return teams.stream().filter(t -> t.getPlayers().stream().map(p -> UUID.fromString(p.getUuid())).anyMatch(p -> p.compareTo(player.getUniqueId()) == 0)).findFirst();
  }

  private void saveCurrentState() {
    if (!groupStage.isFinished()) {
      Saver.saveCurrentState(CurrentState.GROUP_STAGE,
          groupStage.getMatchesToDo().stream().map(t -> (TourneyMatch) t).collect(Collectors.toList()),
          groupStage.getMatchesDone().stream().map(t -> (TourneyMatch) t).collect(Collectors.toList()),
          qualifiedTeams, rematchKo, rematchFinal, teams, groups);
    } else if (!koStage.isFinished()) {
      Saver.saveCurrentState(CurrentState.KO_STAGE,
          koStage.currentKoRound().getMatchesTodo().stream().map(t -> (TourneyMatch) t).collect(Collectors.toList()),
          koStage.currentKoRound().getMatchesDone().stream().map(t -> (TourneyMatch) t).collect(Collectors.toList()),
          qualifiedTeams, rematchKo, rematchFinal, teams, null);
    }
  }

  public void cancel() {
    saveCurrentState();

    BedwarsRel.getInstance().getGameLoopTask().cancel();
    BedwarsRel.getInstance().setGameLoopTask(null);
  }

  public void continueStoopedTournament() {
    clear();
    CurrentState state = Loader.loadTournamentState();

    TournamentStartEvent event = new TournamentStartEvent(qualifiedTeams, rematchKo, rematchFinal);
    BedwarsRel.getInstance().getServer().getPluginManager().callEvent(event);
  }

  public List<TourneyTeam> getTeamsPerId(List<Integer> ids) {
    List<TourneyTeam> teams = new ArrayList<>();

    for (int id : ids) {
      teams.add(getTeamPerId(id));
    }

    return teams;
  }

  public TourneyTeam getTeamPerId(int id) {
    return this.teams.stream().filter(t -> t.getId() == id).findFirst().get();
  }

  public Optional<TourneyTeam> getTeamPerName(String name) {
    return this.teams.stream().filter(t -> t.getName().equals(name)).findFirst();
  }

  public boolean deleteTourneyTeamByName(String name) {
    Optional<TourneyTeam> optional = teams.stream().filter(t -> t.getName().equals(name)).findFirst();

    if (!optional.isPresent()) {
      return false;
    }

    TourneyTeam team = optional.get();
    this.teams.remove(team);

    // remove from the group
    if (groupStage != null) {
      groups.forEach(g -> g.getTeams().removeIf(t -> t.getId() == team.getId()));
      groups.removeIf(g -> g.getTeams().size() == 0);
    }

    // remove from the To do groupStage matches and from the to do matches of the current ko round
    List<TourneyMatch> matches = new LinkedList<>();
    if (groupStage != null) {
      matches.addAll(groupStage.getMatchesToDo());
    }
    if (koStage != null) {
      matches.addAll(koStage.currentKoRound().getMatchesTodo());
    }
    for(TourneyMatch match: matches) {
      boolean isTeamToRemove = match.getTeams().stream().anyMatch(t -> t.getId() == team.getId());
      if(isTeamToRemove) {
        match.getTeams().removeIf(t -> t.getId() == team.getId());
        if (match.isRunning()) {
          match.setAborted(true);
          match.getGame().stop();
        }
      }
    }
    matches.removeIf(match -> match.getTeams().size() == 0);


    return true;
  }

  public boolean addPointsToTeam(String team, int points) {
    Optional<TourneyTeam> optional = teams.stream().filter(t -> t.getName().equals(team)).findFirst();

    if (!optional.isPresent()) {
      return false;
    }

    TourneyTeam tourneyTeam = optional.get();
    if (tourneyTeam.getStatistics().size() == 0) {
      return false;
    }
    tourneyTeam.getStatistics().get(tourneyTeam.getStatistics().size() - 1).addPoints(points);

    return true;

  }

  public TourneyTeam findWinner(List<TourneyTeam> teams, List<TourneyMatch> matches) {
    List<TourneyTeam> winner = new LinkedList<>();
    winner.add(teams.get(0));
    long maxWins = teams.get(0).getStatistics().stream().filter(st -> matches.contains(st.getMatch()) && st.isWin()).count();

    for (int i = 1; i < teams.size(); i++) {
      long wins = teams.get(i).getStatistics().stream().filter(st -> matches.contains(st.getMatch()) && st.isWin()).count();

      if (wins > maxWins) {
        winner = new LinkedList<>();
        winner.add(teams.get(i));
        maxWins = wins;
      } else if (wins == maxWins) {
        winner.add(teams.get(i));
      }
    }

    if (winner.size() == 1) {
      return winner.get(0);
    }

    List<Pair<TourneyTeam, Integer>> winners = new LinkedList<>();

    for (TourneyTeam team : winner) {
      List<TourneyGameStatistic> stats = team.getStatistics().stream().filter(st -> matches.contains(st.getMatch()) && st.isWin()).collect(Collectors.toList());
      int points = stats.stream().map(TourneyGameStatistic::calculatePoints).reduce(Integer::sum).orElse(0);
      winners.add(new Pair<>(team, points * -1));
    }

    winners.sort(Comparator.comparingInt(Pair::getSecond));
    return winner.get(0);
  }

  public CurrentState getCurrentState() {
    if (groupStage != null && !groupStage.isFinished()) {
      return CurrentState.GROUP_STAGE;
    } else if (koStage != null && !koStage.isFinished()) {
      return CurrentState.KO_STAGE;
    } else if (groupStage == null) {
      return CurrentState.NOT_STARTED;
    }

    return CurrentState.ALREADY_FINISHED;
  }
}
