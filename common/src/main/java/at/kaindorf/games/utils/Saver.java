package at.kaindorf.games.utils;

import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.tournament.TourneyProperties;
import at.kaindorf.games.tournament.models.TourneyGroup;
import at.kaindorf.games.tournament.models.TourneyPlayer;
import at.kaindorf.games.tournament.models.TourneyTeam;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class Saver {

  @SneakyThrows
  public static void saveGroups(List<TourneyGroup> groups) {
    // save groups
    YamlConfiguration yml = new YamlConfiguration();
    for (int i = 0; i < groups.size(); i++) {
      yml.set("game" + i, Utils.tourneyGroupSerialize(groups.get(i)));
    }
    yml.save(TourneyProperties.groupsFile);
  }

  @SneakyThrows
  public static void saveTeams(List<TourneyTeam> teams) {
    // save teams
    YamlConfiguration yml = new YamlConfiguration();
    yml = new YamlConfiguration();
    for (int i = 0; i < teams.size(); i++) {
      TourneyTeam team = teams.get(i);
      yml.set("team" + i, Utils.tourneyTeamSerialize(team, getGroupOfTeam(team.getName()).getName()));
    }
    yml.save(TourneyProperties.teamsFile);
  }

  @SneakyThrows
  public static void savePlayers(List<TourneyPlayer> players) {
    // save players
    YamlConfiguration yml = new YamlConfiguration();
    yml = new YamlConfiguration();
    for (int i = 0; i < players.size(); i++) {
      TourneyPlayer player = players.get(i);
      yml.set("player" + i, Utils.tourneyPlayerSerialize(player, getTeamOfPlayer(player.getUuid()).getName()));
    }
    yml.save(TourneyProperties.playersFile);
  }

  public static void clear() {
    if (TourneyProperties.groupsFile.exists()) TourneyProperties.groupsFile.delete();
    if (TourneyProperties.teamsFile.exists()) TourneyProperties.teamsFile.delete();
    if (TourneyProperties.playersFile.exists()) TourneyProperties.playersFile.delete();
  }

  private static TourneyGroup getGroupOfTeam(String team) {
    return Tournament.getInstance().getGroups().stream().filter(g -> g.getTeams().stream().anyMatch(t -> t.getName().equals(team))).findFirst().get();
  }

  private static TourneyTeam getTeamOfPlayer(String player) {
    return Tournament.getInstance().getTeams().stream().filter(t -> t.getPlayers().stream().anyMatch(p -> p.getUuid().equals(player))).findFirst().get();
  }

}
