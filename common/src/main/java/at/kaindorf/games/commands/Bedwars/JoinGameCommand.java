package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.utils.ChatWriter;
import at.kaindorf.games.utils.Utils;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinGameCommand extends BaseCommand {

  public JoinGameCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!super.hasPermission(sender)) {
      return false;
    }

    Player player = (Player) sender;
    Game game = this.getPlugin().getGameManager().getGame(args.get(0));
    Game gameOfPlayer = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (gameOfPlayer != null) {
      if (gameOfPlayer.getState() == GameState.RUNNING) {
        sender.sendMessage(
            ChatWriter
                .pluginMessage(ChatColor.RED + BedwarsRel._l(sender, "errors.notwhileingame")));
        return false;
      }

      if (gameOfPlayer.getState() == GameState.WAITING) {
        gameOfPlayer.playerLeave(player, false);
      }
    }

    if (game == null) {
      if (!args.get(0).equalsIgnoreCase("random")) {
        sender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED
            + BedwarsRel
            ._l(sender, "errors.gamenotfound", ImmutableMap.of("game", args.get(0).toString()))));
        return true;
      }

      ArrayList<Game> games = new ArrayList<>();
      for (Game g : this.getPlugin().getGameManager().getGames()) {
        if (g.getState() == GameState.WAITING) {
          games.add(g);
        }
      }
      if (games.size() == 0) {
        sender.sendMessage(
            ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(sender, "errors.nofreegames")));
        return true;
      }
      game = games.get(Utils.randInt(0, games.size() - 1));
    }

    if (game.playerJoins(player)) {
      sender.sendMessage(
          ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel._l(sender, "success.joined")));
    }
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game"};
  }

  @Override
  public String getCommand() {
    return "join";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.join.desc");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.join.name");
  }

  @Override
  public String getPermission() {
    return "base";
  }

  @Override
  public List<BedwarsRel.Mode> blockDuringMode() {
    return Arrays.asList(BedwarsRel.Mode.TOURNAMENT, BedwarsRel.Mode.LAN);
  }

}
