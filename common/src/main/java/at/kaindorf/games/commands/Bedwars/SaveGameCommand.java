package at.kaindorf.games.commands.Bedwars;

import at.kaindorf.games.commands.BaseCommand;
import at.kaindorf.games.commands.ICommand;
import com.google.common.collect.ImmutableMap;
import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.game.Game;
import at.kaindorf.games.game.GameState;
import at.kaindorf.games.utils.ChatWriter;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SaveGameCommand extends BaseCommand implements ICommand {

  public SaveGameCommand(BedwarsRel plugin) {
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

    if (!game.saveGame(sender, true)) {
      return false;
    }

    sender
        .sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + BedwarsRel
            ._l(sender, "success.saved")));
    return true;
  }

  @Override
  public String[] getArguments() {
    return new String[]{"game"};
  }

  @Override
  public String getCommand() {
    return "save";
  }

  @Override
  public String getDescription() {
    return BedwarsRel._l("commands.save.desc");
  }

  @Override
  public String getName() {
    return BedwarsRel._l("commands.save.name");
  }

  @Override
  public String getPermission() {
    return "setup";
  }

}
