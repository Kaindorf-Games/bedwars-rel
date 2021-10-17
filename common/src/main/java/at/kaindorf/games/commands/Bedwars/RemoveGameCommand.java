package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.utils.ChatWriter;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RemoveGameCommand extends BaseCommand {

  public RemoveGameCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("bw." + this.getPermission())) {
      return false;
    }

    Game game = this.getPlugin().getGameManager().getGame(args.get(0));

    if (game == null) {
      sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
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

    BedwarsRel.getInstance().getGameManager().unloadGame(game);
    BedwarsRel.getInstance().getGameManager().removeGame(game);
    sender.sendMessage(
        ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel._l(sender, "success.gameremoved")));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game"};
  }

  @Override
  public String getCommand() {
    return "removegame";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.removegame.desc");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.removegame.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
