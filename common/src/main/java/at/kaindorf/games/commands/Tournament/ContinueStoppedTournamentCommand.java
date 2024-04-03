package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContinueStoppedTournamentCommand extends BaseCommand implements ICommand {

  public ContinueStoppedTournamentCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if(!sender.hasPermission("tourney."+this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return true;
    }

    if(Tournament.getInstance().isTournamentRunning()) {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("tourney.errors.tournamentalreadyrunning"));
      return true;
    }

    Tournament.getInstance().continueStoopedTournament();
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[0];
  }

  @Override
  public String getCommand() {
    return "continue";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.tourney.continuestopped.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.continuestopped.name");
  }

  @Override
  public String getPermission() {
    return "manage";
  }

  @Override
  public List<BedwarsRel.Mode> blockDuringMode() {
    return Arrays.asList(BedwarsRel.Mode.NORMAL);
  }
}
