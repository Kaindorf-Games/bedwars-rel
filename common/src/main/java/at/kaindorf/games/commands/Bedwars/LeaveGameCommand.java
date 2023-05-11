package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.game.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveGameCommand extends BaseCommand {

  public LeaveGameCommand(BedwarsRel plugin) {
    super(plugin);
  }

  @Override
  public boolean execute(CommandSender sender, ArrayList<String> args) {
    if (!super.hasPermission(sender)) {
      return false;
    }

    Player player = (Player) sender;
    Game game = BedwarsRel.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return true;
    }

    if (game.isDisableLeave()) {
      sender.sendMessage(ChatColor.RED+"Leaving a game is disabled!");
      return true;
    }

    game.playerLeave(player, false);
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{};
  }

  @Override
  public String getCommand() {
    return "leave";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.leave.desc");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.leave.name");
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
