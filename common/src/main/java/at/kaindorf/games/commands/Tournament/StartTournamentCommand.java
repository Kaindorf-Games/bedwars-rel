package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.utils.ChatWriter;
import javafx.scene.chart.BubbleChart;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class StartTournamentCommand extends BaseCommand implements ICommand {
  public StartTournamentCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      sender.sendMessage(ChatWriter.wrongPermissionMessage());
      return false;
    }

    Bukkit.getLogger().info("Start Tournament!");

    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{};
  }

  @Override
  public String getCommand() {
    return "start";
  }

  @Override
  public String getDescription() {
    return "Command to start the tournament";
  }

  @Override
  public String getName() {
    return "Start Tournament";
  }

  @Override
  public String getPermission() {
    return "manage";
  }
}
