package at.kaindorf.games.commands.Tournament;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class InitCommand extends BaseCommand implements ICommand {

  public InitCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    String uuid = "9626aab5-9c1c-48c6-aa5c-39b2bd16c351";
//    Player p = BedwarsRel.getInstance().getServer().getPlayer()
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{};
  }

  @Override
  public String getCommand() {
    return "init";
  }

  @Override
  public String getDescription() {
    return "null";
  }

  @Override
  public String getName() {
    return "null";
  }

  @Override
  public String getPermission() {
    return "manage";
  }
}
