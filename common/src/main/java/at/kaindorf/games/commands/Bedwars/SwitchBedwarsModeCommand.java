package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.utils.ChatWriter;
import com.google.common.collect.ImmutableMap;
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
        sender.sendMessage(ChatColor.RED + BedwarsRel._l("mode.wrong"));
        return false;
      }

      if (mode == BedwarsRel.getInstance().getMode()) {
        sender.sendMessage(ChatColor.YELLOW + BedwarsRel._l("mode.same"));
        return true;
      }

      BedwarsRel.getInstance().changeModeTo(mode);
      sender.sendMessage(ChatColor.GREEN + BedwarsRel._l("mode.success", ImmutableMap.of("mode", mode.toString().toLowerCase())));
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
    return BedwarsRel._l("commands.switchmode.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.switchmode.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }
}
