package at.kaindorf.games.commands;

import at.kaindorf.games.BedwarsRel;
import at.kaindorf.games.utils.ChatWriter;

import java.lang.reflect.Array;
import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand implements ICommand {

  private BedwarsRel plugin = null;

  public BaseCommand(BedwarsRel plugin) {
    this.plugin = plugin;
  }

  @Override
  public abstract boolean execute(CommandSender sender, ArrayList<String> args);

  @Override
  public abstract String[] getArguments();

  @Override
  public CommandArgument[] getNewArguments() {
    return null;
  }

  @Override
  public abstract String getCommand();

  @Override
  public abstract String getDescription();

  @Override
  public abstract String getName();

  protected BedwarsRel getPlugin() {
    return this.plugin;
  }

  @Override
  public boolean hasPermission(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatWriter.pluginMessage("Only players should execute this command!"));
      return false;
    }

    if (!sender.hasPermission("bw." + this.getPermission())) {
      sender.sendMessage(ChatWriter
          .pluginMessage(ChatColor.RED + "You don't have permission to execute this command!"));
      return false;
    }

    return true;
  }


  public List<BedwarsRel.Mode> blockDuringMode() {
    return Collections.emptyList();
  }

  public boolean isDevCommand() {
    return false;
  }

}
