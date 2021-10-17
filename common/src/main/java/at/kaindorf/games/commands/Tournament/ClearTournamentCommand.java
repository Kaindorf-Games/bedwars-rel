package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class ClearTournamentCommand extends BaseCommand implements ICommand {
  public ClearTournamentCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    Tournament.getInstance().clear();
    Tournament.getInstance().show();
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{};
  }

  @Override
  public String getCommand() {
    return "clear";
  }

  @Override
  public String getDescription() {
    return "Clears all tournament configuration (teams, players, matches, ...)";
  }

  @Override
  public String getName() {
    return "Clear Tournament";
  }

  @Override
  public String getPermission() {
    return "manage";
  }
}
