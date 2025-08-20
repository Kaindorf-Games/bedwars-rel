package at.kaindorf.games.tournament;

import at.kaindorf.games.BedwarsRel;
import lombok.SneakyThrows;

import java.io.File;

public class TourneyProperties {

  public static File playersFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/database/players.yml");
  public static File teamsFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/database/teams.yml");
  public static File currentStateFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/database/currentState.yml");

  public static File logFile = new File(BedwarsRel.getInstance().getDataFolder().getAbsolutePath()+"/tournament/log.txt");

  public static int pointsForFinalKill = 2, pointsForWin = 5, pointsForBed = 3;
  public static boolean playInRounds = false;

  @SneakyThrows
  public static void readTourneyProperties() {
    pointsForFinalKill = BedwarsRel.getInstance().getConfig().getInt("tourney.pointsForFinalKill", pointsForFinalKill);
    pointsForBed = BedwarsRel.getInstance().getConfig().getInt("tourney.pointsForBed", pointsForBed);
    pointsForWin = BedwarsRel.getInstance().getConfig().getInt("tourney.pointsForWin", pointsForWin);
    playInRounds = BedwarsRel.getInstance().getConfig().getBoolean("tourney.playInRounds", playInRounds);
  }
}
