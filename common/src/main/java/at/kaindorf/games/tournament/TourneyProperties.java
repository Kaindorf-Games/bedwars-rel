package at.kaindorf.games.tournament;

import at.kaindorf.games.BedwarsRel;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.io.File;

public class TourneyProperties {

  public static File playersFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/database/players.yml");
  public static File groupsFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/database/groups.yml");
  public static File teamsFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/database/teams.yml");
  public static File groupStageFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/groupStage.yml");
  public static File currentStateFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/database/currentState.yml");

  public static File logFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/log.txt");

  public static int pointsForFinalKill = 1, pointsForWin = 5, pointsForBed = 2;

  @SneakyThrows
  public static void readTourneyProperties() {
    pointsForFinalKill = BedwarsRel.getInstance().getConfig().getInt("tourney.pointsForFinalKill", pointsForFinalKill);
    pointsForBed = BedwarsRel.getInstance().getConfig().getInt("tourney.pointsForBed", pointsForBed);
    pointsForWin = BedwarsRel.getInstance().getConfig().getInt("tourney.pointsForWin", pointsForWin);
  }
}
