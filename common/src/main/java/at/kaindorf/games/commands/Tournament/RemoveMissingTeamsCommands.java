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

public class RemoveMissingTeamsCommands extends BaseCommand implements ICommand {
  public RemoveMissingTeamsCommands(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("tourney." + this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    try {
      Tournament.getInstance().removeMissingTeams();
    } catch (Exception e) {
      sender.sendMessage(ChatColor.RED + e.getMessage());
      return true;
    }
    sender.sendMessage(ChatColor.GREEN + "Missing teams removed");

    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[0];
  }

  @Override
  public String getCommand() {
    return "removeMissing";
  }

  @Override
  public String getDescription() {
    return "removes all missing teams";
  }

  @Override
  public String getName() {
    return "removeMissing";
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
