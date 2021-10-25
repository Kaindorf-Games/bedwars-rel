package at.kaindorf.games.utils;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.TourneyProperties;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Set;

public class Loader {

  @SneakyThrows
  public static void loadSavedGroups() {
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
          Tournament.getInstance().addGroup(groupName);
        }
      } else {
        BedwarsRel.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + "No group config found");
      }
    }
  }

  @SneakyThrows
  public static void loadSavedTeams() {
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
          Tournament.getInstance().addTeam(teamName, teamGroup);
        }
      } else {
        BedwarsRel.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + "No team config found");
      }
    }
  }

  @SneakyThrows
  public static void loadSavedPlayers() {
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
          Tournament.getInstance().addPlayer(uuid, teamName, kills, destroyedBeds);
        }
      } else {
        BedwarsRel.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + "No player config found");
      }
    }
  }
}
