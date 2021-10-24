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

public class SaveTourneyConfigCommand extends BaseCommand implements ICommand {

  public SaveTourneyConfigCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if(!sender.hasPermission("tourney."+this.getPermission())) {
      sender.sendMessage(ChatWriter.wrongPermissionMessage());
      return false;
    }

    boolean res = Tournament.getInstance().save();
    if(res) sender.sendMessage(ChatColor.GREEN + "Configuration is saved");
    else sender.sendMessage(ChatColor.RED + "Configuration could not be saved");
    return res;
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
    return "Saves all the configuration to yml files";
  }

  @Override
  public String getName() {
    return "save Tournament Configuration";
  }

  @Override
  public String getPermission() {
    return "manage";
  }
}
