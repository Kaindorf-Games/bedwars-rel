package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Locale;

public class SwitchBedwarsModeCommand extends BaseCommand implements ICommand {

  public SwitchBedwarsModeCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("bw." + this.getPermission())) {
      sender.sendMessage(ChatWriter.wrongPermissionMessage());
      return false;
    }

    if (args.size() == 1) {
      BedwarsRel.Mode mode = null;
      try {
        mode = BedwarsRel.Mode.valueOf(args.get(0).toUpperCase());
      } catch (IllegalArgumentException ex) {
        sender.sendMessage(ChatColor.RED + "Wrong Mode supplied!");
        return false;
      }

      if (mode == BedwarsRel.getInstance().getMode()) {
        sender.sendMessage(ChatColor.YELLOW + "Mode is already set");
      }

      BedwarsRel.getInstance().changeModeTo(mode);
      sender.sendMessage(ChatColor.GREEN + "Mode change complete");
      return true;
    }

    return false;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"<normal|tournament>"};
  }

  @Override
  public String getCommand() {
    return "switch";
  }

  @Override
  public String getDescription() {
    return "switches between normal and Tournament Mode";
  }

  @Override
  public String getName() {
    return "switch";
  }

  @Override
  public String getPermission() {
    return "setup";
  }
}
