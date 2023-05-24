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
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    if (args.size() == 1) {
      BedwarsRel.Mode mode = null;
      String in = args.get(0);
      if(in.equalsIgnoreCase("t")) {
        in = "tournament";
      } else if(in.equalsIgnoreCase("n")) {
        in = "normal";
      } else if (in.equalsIgnoreCase("l")) {
        in = "lan";
      }
      try {
        mode = BedwarsRel.Mode.valueOf(in.toUpperCase());
      } catch (IllegalArgumentException ex) {
        sender.sendMessage(ChatColor.RED + "Wrong Mode supplied!");
        return false;
      }

      if (mode == BedwarsRel.getInstance().getMode()) {
        sender.sendMessage(ChatColor.YELLOW + "Mode is already set");
        return true;
      }

      BedwarsRel.getInstance().changeModeTo(mode);
      sender.sendMessage(ChatColor.GREEN + "Mode changed to "+mode.toString().toLowerCase());
      return true;
    }

    return false;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"<normal|tournament|lan>"};
  }

  @Override
  public String getCommand() {
    return "switch";
  }

  @Override
  public String getDescription() {
    return "switches between modes";
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
