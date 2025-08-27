package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.arguments.CommandArgument;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.tournament.Tournament;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("tourney.info.loadedentities"));
    return true;
  }

  @Override
  public CommandArgument[] getNewArguments() {
    return new CommandArgument[0];
  }

  @Override
  public String getCommand() {
    return "load";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.tourney.loadentities.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.tourney.loadentities.name");
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
