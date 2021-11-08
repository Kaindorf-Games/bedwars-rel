package at.kaindorf.games.utils;

import at.kaindorf.games.tournament.TourneyProperties;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.io.FileWriter;
import java.io.IOException;

public class TournamentLogger {

  private static TournamentLogger logger;

  public static TournamentLogger getInstance() {
    if (logger == null) {
      logger = new TournamentLogger();
    }
    return logger;
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
    sb.append(killer + " final killed " + death + "\n");
    write();
  }

  @SneakyThrows
  public void logBedDestroyed(String destroyedByPlayer, String destroyedByTeam) {
    Bukkit.getLogger().info("Bed was destroyed by " + destroyedByPlayer + "(" + destroyedByTeam + ")");
    sb.append("Bed was destroyed by " + destroyedByPlayer + "(" + destroyedByTeam + ")" + "\n");
    write();
  }

  @SneakyThrows
  public void logMatchWin(String team) {
    Bukkit.getLogger().info(team + " has won");
    sb.append(team + " has won" + "\n");
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
