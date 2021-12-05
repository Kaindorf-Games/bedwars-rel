package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;

public class SaveTourneyEntitiesCommand extends BaseCommand implements ICommand {

  public SaveTourneyEntitiesCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if(!sender.hasPermission("tourney."+this.getPermission())) {
      sender.sendMessage(ChatWriter.wrongPermissionMessage());
      return false;
    }

    Tournament.getInstance().save();
    sender.sendMessage(ChatColor.GREEN+"Entities are saved");
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[] {};
  }

  @Override
  public String getCommand() {
    return "save";
  }

  @Override
  public String getDescription() {
    return "Saves all the Entities to yml files";
  }

  @Override
  public String getName() {
    return "save Tournament Entities";
  }

  @Override
  public String getPermission() {
    return "manage";
  }
}
