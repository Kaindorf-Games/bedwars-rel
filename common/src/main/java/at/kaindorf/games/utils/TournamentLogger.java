package at.kaindorf.games.utils;

import at.kaindorf.games.tournament.TourneyProperties;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.io.FileWriter;
import java.io.IOException;

public class TournamentLogger {

  private static TournamentLogger logger;
  @Setter
  private String mode;

  public static TournamentLogger info() {
    getInstance("INFO");
    return logger;
  }

  public static TournamentLogger error() {
    getInstance("ERROR");
    return logger;
  }

  public static TournamentLogger warning() {
    getInstance("WARNING");
    return logger;
  }

  private static void getInstance(String mode) {
    if (logger == null) {
      logger = new TournamentLogger();
    }
    logger.setMode(mode);
  }

  private StringBuilder sb;

  private TournamentLogger() {
    if (!TourneyProperties.logFile.exists()) {
      try {
        TourneyProperties.logFile.createNewFile();
      } catch (IOException e) {
        Bukkit.getLogger().warning(e.getMessage());
      }
    }
    sb = new StringBuilder();
  }

  @SneakyThrows
  public void logFinalKill(String killer, String death) {
    Bukkit.getLogger().info(killer + " final killed " + death);
    sb.append(mode + ": " + killer + " final killed " + death + "\n");
    write();
  }

  @SneakyThrows
  public void logBedDestroyed(String destroyedByPlayer, String destroyedByTeam) {
    Bukkit.getLogger().info("Bed was destroyed by " + destroyedByPlayer + "(" + destroyedByTeam + ")");
    sb.append(mode + ": Bed was destroyed by " + destroyedByPlayer + "(" + destroyedByTeam + ")" + "\n");
    write();
  }

  @SneakyThrows
  public void logMatchWin(String team) {
    Bukkit.getLogger().info(team + " has won");
    sb.append(mode + ": " + team + " has won" + "\n");
    write();
  }

  @SneakyThrows
  public void logTournamentStop() {
    Bukkit.getLogger().info("Tournament has been stopped");
    sb.append(mode+": the Tournament has been stopped");
    write();
  }

  private void write() {
    try {
      FileWriter writer = new FileWriter(TourneyProperties.logFile);
      writer.write(sb.toString());
      writer.close();
    } catch (IOException e) {
      Bukkit.getLogger().warning(e.getMessage());
    }
  }

}
