package at.kaindorf.games.tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.utils.Utils;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class TourneyProperties {
  public static Map<String, Object> values = new HashMap<>();

  static {
    values.put("pointsForWin",5);
    values.put("pointsForBed",2);
    values.put("pointsForFinalKill",1);
  }

  public static File playersFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/players.yml");
  public static File groupsFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/groups.yml");
  public static File teamsFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/teams.yml");
  public static File propertiesFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tourneyProps.yml");
  public static File groupStageFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/groupStage.yml");

  public static Object get(String key) {
    return values.get(key);
  }

  @SneakyThrows
  public static void loadPropertiesFile() {
    YamlConfiguration yaml = new YamlConfiguration();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(new FileInputStream(TourneyProperties.propertiesFile), "UTF-8"));
    yaml.load(reader);
    for(String key : values.keySet()) {
      Object value = yaml.get(key);
      if(value != null) {
        values.replace(key, value);
      }
    }
  }

  @SneakyThrows
  public static void createPropertiesFile() {
    YamlConfiguration yml = new YamlConfiguration();
    for (String key : values.keySet()) {
      yml.set(key, String.valueOf(values.get(key)));
    }
    yml.save(TourneyProperties.propertiesFile);
  }
}
