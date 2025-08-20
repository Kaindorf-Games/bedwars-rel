package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.utils.ChatWriter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class MovePlayersIntoGame extends BaseCommand implements ICommand {
  public MovePlayersIntoGame(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!sender.hasPermission("bw." + this.getPermission())) {
      ChatWriter.wrongPermissionMessage(sender);
      return false;
    }

    String name = args.get(0);
    Game game = BedwarsRel.getInstance().getGameManager().getGame(name);
    if (game == null) {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("errors.gamenotfoundsimple"));
      return false;
    } else if (game.getState() != GameState.WAITING && game.getPlayers().size() != 0) {
      sender.sendMessage(ChatColor.RED + BedwarsRel._l("leaderboard.errors.notwhileingame"));
      return false;
    }

    game.setDisableLeave(true);
    List<Player> players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
    players.stream().filter(p -> !p.isOp()).forEach(game::playerJoins);

    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"gameName"};
  }

  @Override
  public String getCommand() {
    return "moveplayers";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.moveplayers.description");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.moveplayers.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }
}
