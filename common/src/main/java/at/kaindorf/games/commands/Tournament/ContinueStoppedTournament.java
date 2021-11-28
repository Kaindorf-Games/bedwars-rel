package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import com.sun.xml.internal.stream.writers.WriterUtility;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class ContinueStoppedTournament extends BaseCommand implements ICommand {

  public ContinueStoppedTournament(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if(!sender.hasPermission("tourney."+this.getPermission())) {
      sender.sendMessage(ChatWriter.wrongPermissionMessage());
      return true;
    }

    if(Tournament.getInstance().isTournamentRunning()) {
      sender.sendMessage(ChatColor.RED+"Tournament is already running");
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
    return "Continues a stopped tournament";
  }

  @Override
  public String getName() {
    return "continue";
  }

  @Override
  public String getPermission() {
    return "manage";
  }
}
