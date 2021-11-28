package at.kaindorf.games.utils;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.TourneyProperties;
import at.kaindorf.games.tournament.models.*;
import at.kaindorf.games.tournament.rounds.GroupStage;
import at.kaindorf.games.tournament.rounds.KoRound;
import at.kaindorf.games.tournament.rounds.KoStage;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Loader {

  @SneakyThrows
  public static List<String> loadSavedGroups() {
    List<String> groups = new LinkedList<>();
    // load Groups
    if (TourneyProperties.groupsFile.exists() && TourneyProperties.groupsFile.canRead()) {
      YamlConfiguration yaml = new YamlConfiguration();
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(TourneyProperties.groupsFile), "UTF-8"));
      yaml.load(reader);
      Set<String> keys = yaml.getKeys(false);
      if (keys.size() > 0) {
        for (String key : keys) {
          String groupName = yaml.getString(key + ".name");
          groups.add(groupName);
        }
      } else {
        BedwarsRel.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + "No group config found");
      }
    }
    return groups;
  }

  @SneakyThrows
  public static List<Pair<String, String>> loadSavedTeams() {
    List<Pair<String, String>> teams = new LinkedList<>();
    // load Teams
    if (TourneyProperties.teamsFile.exists() && TourneyProperties.teamsFile.canRead()) {
      YamlConfiguration yaml = new YamlConfiguration();
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(TourneyProperties.teamsFile), "UTF-8"));
      yaml.load(reader);
      Set<String> keys = yaml.getKeys(false);
      if (keys.size() > 0) {
        for (String key : keys) {
          String teamName = yaml.getString(key + ".name");
          String teamGroup = yaml.getString(key + ".group");
          teams.add(new Pair<>(teamName, teamGroup));
        }
      } else {
        BedwarsRel.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + "No team config found");
      }
    }
    return teams;
  }

  @SneakyThrows
  public static List<Pair<TourneyPlayer, String>> loadSavedPlayers() {
    List<Pair<TourneyPlayer, String>> players = new LinkedList<>();
    // load Player
    if (TourneyProperties.playersFile.exists() && TourneyProperties.playersFile.canRead()) {
      YamlConfiguration yaml = new YamlConfiguration();
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(TourneyProperties.playersFile), "UTF-8"));
      yaml.load(reader);
      Set<String> keys = yaml.getKeys(false);
      if (keys.size() > 0) {
        for (String key : keys) {
          int kills = yaml.getInt(key + ".kills");
          String teamName = yaml.getString(key + ".team");
          int destroyedBeds = yaml.getInt(key + ".destroyedBeds");
          String uuid = yaml.getString(key + ".uuid");
          players.add(new Pair<>(new TourneyPlayer(uuid, "", kills, destroyedBeds), teamName));
        }
      } else {
        BedwarsRel.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + "No player config found");
      }
    }
    return players;
  }

  @SneakyThrows
  public static CurrentState loadTournamentState() {
    if (TourneyProperties.currentStateFile.exists() && TourneyProperties.currentStateFile.canRead()) {
      Bukkit.getLogger().info("load Data");

      YamlConfiguration yaml = new YamlConfiguration();
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(new FileInputStream(TourneyProperties.currentStateFile), "UTF-8"));
      yaml.load(reader);

      // load basic info (state, rematch, qualifiedTeams, ...)
      CurrentState state = CurrentState.valueOf(yaml.getString("state"));
      Bukkit.getLogger().info(state.toString());

      boolean rematchFinal = yaml.getBoolean("config.rematchFinal");
      Tournament.getInstance().setRematchFinal(rematchFinal);
      boolean rematch = yaml.getBoolean("config.rematch");
      Tournament.getInstance().setRematchKo(rematch);
      int qualifiedTeams = yaml.getInt("config.qualifiedTeams");
      Tournament.getInstance().setQualifiedTeams(qualifiedTeams);

      // load groups (if necessary), teams and players
      if (state == CurrentState.GROUP_STAGE) {
        loadGroups(yaml);
      } else {
        loadTeams(yaml, "", "");
      }

      GroupStage groupStage = new GroupStage();
      KoStage koStage = new KoStage(qualifiedTeams, rematch, rematchFinal);
      boolean rematchKoRound = rematch;
      if (Tournament.getInstance().getTeams().size() <= 4) {
        rematchKoRound = rematchFinal;
      }
      KoRound koRound = new KoRound(null, rematchKoRound);

      // load MatchesDone
      for (String key : yaml.getConfigurationSection("matchesDone").getKeys(false)) {
        ConfigurationSection matchSection = yaml.getConfigurationSection("matchesDone." + key);
        if (state == CurrentState.GROUP_STAGE) {
          TourneyGroupMatch match = new TourneyGroupMatch(matchSection.getInt("id"), Tournament.getInstance().getTeamsPerId(matchSection.getIntegerList("teams")));
          addTeamStatisticToTeam(matchSection, match);
          groupStage.addDoneMatch(match);
        } else {
          TourneyKoMatch match = new TourneyKoMatch(matchSection.getInt("id"), Tournament.getInstance().getTeamsPerId(matchSection.getIntegerList("teams")),matchSection.getInt("rematchId",-1));
          addTeamStatisticToTeam(matchSection, match);
          koRound.addDoneMatch(match);
        }
      }

      // load MatchesToDo
      for (String key : yaml.getConfigurationSection("matchesToDo").getKeys(false)) {
        ConfigurationSection matchSection = yaml.getConfigurationSection("matchesToDo." + key);
        if (state == CurrentState.GROUP_STAGE) {
          TourneyGroupMatch match = new TourneyGroupMatch(matchSection.getInt("id"), Tournament.getInstance().getTeamsPerId(matchSection.getIntegerList("teams")));
          groupStage.addToDoMatch(match);
        } else {
          TourneyKoMatch match = new TourneyKoMatch(matchSection.getInt("id"), Tournament.getInstance().getTeamsPerId(matchSection.getIntegerList("teams")), matchSection.getInt("rematchId",-1));
          koRound.addToDoMatch(match);
        }
      }

      // find the rematch if required
      if(state == CurrentState.KO_STAGE) {
        for(TourneyKoMatch match : koRound.getMatchesTodo()) {
          match.findRematch(koRound);
        }
        for(TourneyKoMatch match : koRound.getMatchesDone()) {
          match.findRematch(koRound);
        }
      }

      koStage.addKoRound(koRound);
      koStage.startKoRound();
      Tournament.getInstance().setGroupStage(groupStage);
      Tournament.getInstance().setKoStage(koStage);
      return state;
    }
    return null;
  }

  private static void addTeamStatisticToTeam(ConfigurationSection matchSection, TourneyMatch match) {
    for (String teamKey : matchSection.getConfigurationSection("teamStats").getKeys(false)) {
      ConfigurationSection section = matchSection.getConfigurationSection("teamStats." + teamKey);

      TourneyTeamStatistics statistics = new TourneyTeamStatistics(
          section.getInt("id"),
          match,
          section.getInt("finalKills"),
          section.getInt("destroyedBeds"),
          section.getBoolean("win")
      );

      TourneyTeam team = Tournament.getInstance().getTeamPerId(section.getInt("teamId"));
      team.addStatistic(statistics);
    }
  }

  @SneakyThrows
  private static void loadGroups(YamlConfiguration yaml) {
    Set<String> groupKeys = yaml.getConfigurationSection("groups").getKeys(false);
    for (String groupKey : groupKeys) {
      ConfigurationSection section = yaml.getConfigurationSection("groups." + groupKey);
      String name = section.getString("name");
      Tournament.getInstance().addGroup(section.getInt("id"), name);
      loadTeams(yaml, "groups." + groupKey + ".", name);
    }
  }

  @SneakyThrows
  private static void loadTeams(YamlConfiguration yaml, String path, String groupName) {
    Set<String> teamKeys = yaml.getConfigurationSection(path + "teams").getKeys(false);
    for (String teamKey : teamKeys) {
      ConfigurationSection section = yaml.getConfigurationSection(path + "teams." + teamKey);
      String name = section.getString("name");
      Tournament.getInstance().addTeam(section.getInt("id"), name, groupName);
      loadPlayers(yaml, path + "teams." + teamKey + ".", name);
    }
  }

  @SneakyThrows
  private static void loadPlayers(YamlConfiguration yaml, String path, String teamName) {
    Set<String> playerKeys = yaml.getConfigurationSection(path + "players").getKeys(false);
    for (String playerKey : playerKeys) {
      ConfigurationSection section = yaml.getConfigurationSection(path + "players." + playerKey);
      Tournament.getInstance().addPlayer(section.getInt("id"), section.getString("uuid"), teamName, section.getInt("kills"), section.getInt("destroyedBeds"));
    }
  }

}

