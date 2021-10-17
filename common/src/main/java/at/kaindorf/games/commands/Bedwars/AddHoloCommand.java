package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.utils.ChatWriter;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddHoloCommand extends BaseCommand implements ICommand {

  public AddHoloCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!super.hasPermission(sender)) {
      return false;
    }

    if (!BedwarsRel.getInstance().isHologramsEnabled()) {
      String missingholodependency = BedwarsRel.getInstance().getMissingHoloDependency();

      sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
          + BedwarsRel._l(sender, "errors.holodependencynotfound",
          ImmutableMap.of("dependency", missingholodependency))));
      return true;
    }

    Player player = (Player) sender;
    BedwarsRel.getInstance().getHolographicInteractor()
        .addHologramLocation(player.getEyeLocation());
    BedwarsRel.getInstance().getHolographicInteractor().updateHolograms();
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{};
  }

  @Override
  public String getCommand() {
    return "addholo";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.addholo.desc");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.addholo.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
