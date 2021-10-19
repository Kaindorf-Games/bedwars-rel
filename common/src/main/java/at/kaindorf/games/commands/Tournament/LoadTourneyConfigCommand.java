package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class LoadTourneyConfigCommand extends BaseCommand implements ICommand {

  public LoadTourneyConfigCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if(!sender.hasPermission("tourney."+this.getPermission())) {
      sender.sendMessage(ChatWriter.wrongPermissionMessage());
      return false;
    }

    Tournament.getInstance().clear();
    Tournament.getInstance().loadConfig();
    sender.sendMessage(ChatColor.GREEN + "Loaded Tournament Config");
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
    return "Loads all the configuration to yml files";
  }

  @Override
  public String getName() {
    return "load the Tournament Configuration";
  }

  @Override
  public String getPermission() {
    return "manage";
  }
}
