package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class LoadTourneyEntitiesCommand extends BaseCommand implements ICommand {

  public LoadTourneyEntitiesCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if(!sender.hasPermission("tourney."+this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    Tournament.getInstance().clear();
    Tournament.getInstance().loadSaves();
    sender.sendMessage(ChatColor.GREEN + "Loaded Tournament Entities");
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[] {};
  }

  @Override
  public String getCommand() {
    return "load";
  }

  @Override
  public String getDescription() {
    return "Loads all the Entities from yml files";
  }

  @Override
  public String getName() {
    return "load the Tournament Entities";
  }

  @Override
  public String getPermission() {
    return "manage";
  }

  @Override
  public BedwarsRel.Mode blockDuringMode() {
    return BedwarsRel.Mode.NORMAL;
  }
}
