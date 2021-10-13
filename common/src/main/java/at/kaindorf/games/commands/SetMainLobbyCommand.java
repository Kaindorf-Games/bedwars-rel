package at.kaindorf.games.commands;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.utils.ChatWriter;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetMainLobbyCommand extends BaseCommand implements ICommand {

  public SetMainLobbyCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!super.hasPermission(sender)) {
      return false;
    }

    Player player = (Player) sender;

    Game game = this.getPlugin().getGameManager().getGame(args.get(0));
    if (game == null) {
      player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
          + BedwarsRel
          ._l(player, "errors.gamenotfound", ImmutableMap.of("game", args.get(0).toString()))));
      return false;
    }

    if (game.getState() != GameState.STOPPED) {
      sender.sendMessage(
          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
              ._l(sender, "errors.notwhilegamerunning")));
      return false;
    }

    game.setMainLobby(player.getLocation());
    player.sendMessage(
        ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel._l(player, "success.mainlobbyset")));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game"};
  }

  @Override
  public String getCommand() {
    return "setmainlobby";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.setmainlobby.desc");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.setmainlobby.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
