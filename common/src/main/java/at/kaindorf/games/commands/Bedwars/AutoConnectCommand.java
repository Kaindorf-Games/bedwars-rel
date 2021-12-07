package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameCheckCode;
import at.kaindorf.games.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class AutoConnectCommand extends BaseCommand {

  private BedwarsRel plugin = null;

  public AutoConnectCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {


    ArrayList<Game> showedGames = new ArrayList<Game>();
    List<Game> games = BedwarsRel.getInstance().getGameManager().getGames();
    for (Game game : games) {
      GameCheckCode code = game.checkGame();
      if (code != GameCheckCode.OK && !sender.hasPermission("bw.setup")) {
        continue;
      }

      showedGames.add(game);
      int players = 0;
      if (game.getState() == GameState.RUNNING) {
        players = game.getCurrentPlayerAmount();
      } else {
        players = game.getPlayers().size();
      }
      String status = game.getState().toString().toLowerCase();
      if (game.getState() == GameState.WAITING) {
        sender.sendMessage(ChatColor.GREEN + "Connecting to the game lobby.");
        Player player = getServer().getPlayer(sender.getName());
        String command = "bw join " + game.getName();
        player.performCommand(command);
      }
    }

    if (showedGames.size() == 0) {
      sender.sendMessage(ChatColor.RED + "No Games :(");
    }

    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{};
  }

  @Override
  public String getCommand() {
    return "autojoin";
  }

  @Override
  public String getDescription() {
    return "Auto connect to first lobby";
  }

  @Override
  public String getName() {
    return "autojoin";
  }

  @Override
  public String getPermission() {
    return "base";
  }

  @Override
  public boolean hasPermission(CommandSender sender) {
    return true;
  }

  @Override
  public BedwarsRel.Mode blockDuringMode() {
    return BedwarsRel.Mode.TOURNAMENT;
  }
}
