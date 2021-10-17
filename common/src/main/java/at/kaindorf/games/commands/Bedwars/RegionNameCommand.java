package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.utils.ChatWriter;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegionNameCommand extends BaseCommand implements ICommand {

  public RegionNameCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("bw." + this.getPermission())) {
      return false;
    }

    Player player = (Player) sender;

    Game game = this.getPlugin().getGameManager().getGame(args.get(0));
    String name = args.get(1).toString();

    if (game == null) {
      player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
          + BedwarsRel
          ._l(sender, "errors.gamenotfound", ImmutableMap.of("game", args.get(0).toString()))));
      return false;
    }

    if (game.getState() == GameState.RUNNING) {
      sender.sendMessage(
          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
              ._l(sender, "errors.notwhilegamerunning")));
      return false;
    }

    if (name.length() > 15) {
      player.sendMessage(
          ChatWriter
              .pluginMessage(ChatColor.RED + BedwarsRel._l(player, "errors.toolongregionname")));
      return true;
    }

    game.setRegionName(name);
    player
        .sendMessage(
            ChatWriter
                .pluginMessage(ChatColor.GREEN + BedwarsRel._l(player, "success.regionnameset")));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game", "name"};
  }

  @Override
  public String getCommand() {
    return "regionname";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.regionname.desc");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.regionname.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
