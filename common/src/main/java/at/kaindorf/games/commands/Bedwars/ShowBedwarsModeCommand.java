package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class ShowBedwarsModeCommand extends BaseCommand implements ICommand {

  public ShowBedwarsModeCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("bw." + this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    sender.sendMessage(ChatColor.GREEN + "Bedwars Mode: " + BedwarsRel.getInstance().getMode().toString().toLowerCase());
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[0];
  }

  @Override
  public String getCommand() {
    return "showMode";
  }

  @Override
  public String getDescription() {
    return "Show the current Bedwars Mode";
  }

  @Override
  public String getName() {
    return "showMode";
  }

  @Override
  public String getPermission() {
    return "base";
  }
}
