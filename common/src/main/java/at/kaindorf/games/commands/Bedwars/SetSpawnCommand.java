package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import com.google.common.collect.ImmutableMap;
import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.game.Team;
import at.kaindorf.games.utils.ChatWriter;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends BaseCommand implements ICommand {

  public SetSpawnCommand(BedwarsRel plugin) {
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

    if (game.getState() == GameState.RUNNING) {
      sender.sendMessage(
          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel
              ._l(sender, "errors.notwhilegamerunning")));
      return false;
    }

    Team team = game.getTeam(args.get(1));
    if (team == null) {
      player.sendMessage(
          ChatWriter.pluginMessage(ChatColor.RED + BedwarsRel._l(player, "errors.teamnotfound")));
      return false;
    }

    team.setSpawnLocation(player.getLocation());
    player
        .sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel
            ._l(player, "success.spawnset",
                ImmutableMap
                    .of("team", team.getChatColor() + team.getDisplayName() + ChatColor.GREEN))));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game", "team"};
  }

  @Override
  public String getCommand() {
    return "setspawn";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.setspawn.desc");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.setspawn.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
